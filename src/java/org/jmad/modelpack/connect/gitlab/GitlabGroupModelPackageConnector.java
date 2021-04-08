/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab;

import static org.jmad.modelpack.connect.ConnectorUriSchemes.GITLAB_HTTPS_SCHEME;
import static org.jmad.modelpack.connect.ConnectorUriSchemes.GITLAB_HTTP_SCHEME;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import org.jmad.modelpack.connect.ZipModelPackageConnector;
import org.jmad.modelpack.connect.gitlab.domain.GitlabModelPackage;
import org.jmad.modelpack.connect.gitlab.domain.GitlabModelPackageVariant;
import org.jmad.modelpack.connect.gitlab.domain.GitlabVariant;
import org.jmad.modelpack.connect.gitlab.internals.GitlabBranch;
import org.jmad.modelpack.connect.gitlab.internals.GitlabProject;
import org.jmad.modelpack.connect.gitlab.internals.GitlabTag;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.ModelPackages;
import org.jmad.modelpack.domain.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

public class GitlabGroupModelPackageConnector implements ZipModelPackageConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabGroupModelPackageConnector.class);
    private static final String JMAD_IGNORE_TAG = "jmad-ignore";

    private ExecutorService runner = Executors.newCachedThreadPool();

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository) {
        if (!canHandle(repository)) {
            return Flux.empty();
        }
        String uri = baseUrlOf(repository) + "/api/v4/groups/" + gitlabGroupNameOf(repository) + "/projects";
        LOGGER.info("Querying model packages from '{}'.", uri);

        // @formatter:off
        return flux(uri,GitlabProject[].class)
                .filter(GitlabGroupModelPackageConnector::filterOutIgnoredRepos)
                .flatMap(p -> variantsFor(repository, p).map(v -> modelPackageVariant(repository, p, v)));
        // @formatter:on
    }

    @Override
    public Mono<ModelPackageVariant> packageFromUri(URI rawUri) {
        if (!canHandle(rawUri)) {
            return Mono.empty();
        }
        URI uri = rawUri.normalize();
        String uriPath = uri.getRawPath();
        int firstSlash = uriPath.indexOf("/", 1);
        if (firstSlash == -1) {
            return Mono.error(new IllegalArgumentException("Malformed GitLab URI: " + uri));
        }
        String gitlabGroupPath = uriPath.substring(0, firstSlash + 1);
        JMadModelPackageRepository repository;
        try {
            repository = JMadModelPackageRepository.fromUri(new URI(uri.getScheme(), uri.getRawAuthority(),
                    gitlabGroupPath, null));
        } catch (URISyntaxException e) {
            return Mono.error(e);
        }
        String modelPackPart = uriPath.substring(firstSlash + 1);
        int firstAt = modelPackPart.indexOf("@");
        String modelPackName;
        Optional<String> modelPackVariant;
        if (firstAt == -1) {
            modelPackName = modelPackPart;
            modelPackVariant = Optional.empty();
        } else {
            modelPackName = modelPackPart.substring(0, firstAt);
            modelPackVariant = Optional.of(modelPackPart.substring(firstAt + 1));
        }
        return availablePackages(repository)
                .filter(mpv -> mpv.modelPackage().name().equals(modelPackName))
                .filter(mpv -> modelPackVariant.map(mpv.variant().name()::equals).orElse(true))
                .sort(ModelPackages.latestFirstPackageVariantComparator())
                .next();
    }

    public ModelPackageVariant modelPackageVariant(JMadModelPackageRepository repo,
                                                   GitlabProject project, GitlabVariant variant) {
        return new GitlabModelPackageVariant(new GitlabModelPackage(project, repo), variant);
    }

    private static String baseUrlOf(JMadModelPackageRepository repository) {
        URI uri = repository.uri();
        String protocol = uri.getScheme().replace("gitlab+", "");
        String host = uri.getAuthority();
        return protocol + "://" + host;
    }

    private static String gitlabGroupNameOf(JMadModelPackageRepository repository) {
        String uriPath = repository.uri().getRawPath();
        int firstSlash = uriPath.indexOf("/", 1);
        if (firstSlash == -1) {
            return uriPath.substring(1);
        } else {
            return uriPath.substring(1, firstSlash);
        }
    }

    @Override
    public Mono<Resource> zipResourceFor(ModelPackageVariant modelPackageVariant) {
        if (!(modelPackageVariant instanceof GitlabModelPackageVariant)) {
            return Mono.empty();
        }
        GitlabModelPackageVariant pkgVariant = (GitlabModelPackageVariant) modelPackageVariant;
        GitlabModelPackage pkg = (GitlabModelPackage) pkgVariant.modelPackage();

        String uri = repositoryUri(pkg.repository(), pkg.id()) + "/archive.zip" + variantParam(pkgVariant.variant());
        LOGGER.info("Retrieving package from {}.", uri);

        return mono(() -> {
            RequestEntity<Void> request = RequestEntity.get(URI.create(uri)).accept(MediaType.APPLICATION_OCTET_STREAM)
                    .build();
            ResponseEntity<Resource> r = restTemplate.exchange(request, Resource.class);
            return r.getBody();
        });

    }

    private static String variantParam(Variant variant) {
        return "?sha=" + variant.name();
    }

    public Flux<GitlabVariant> variantsFor(JMadModelPackageRepository repo, GitlabProject pkg) {
        return Flux.merge(tagsFor(repo, pkg.id), branchesFor(repo, pkg.id));
    }

    private static boolean filterOutIgnoredRepos(GitlabProject p) {
        if (p.tag_list.contains(JMAD_IGNORE_TAG)) {
            LOGGER.info("Ignoring Gitlab project {} because of {} tag", p.name, JMAD_IGNORE_TAG);
            return false;
        }
        return true;
    }

    private Flux<GitlabVariant> branchesFor(JMadModelPackageRepository repository, String id) {
        String uri = repositoryUri(repository, id) + "/branches?per_page=100";

        // @formatter:off
        return flux(uri, GitlabBranch[].class)
               .map(GitlabBranch::toBranch);
        // @formatter:on
    }

    private Flux<GitlabVariant> tagsFor(JMadModelPackageRepository repository, String id) {
        String uri = repositoryUri(repository, id) + "/tags?per_page=100";

        // @formatter:off
        return flux(uri, GitlabTag[].class)
               .map(GitlabTag::toTag);
        // @formatter:on
    }

    private static String repositoryUri(JMadModelPackageRepository repository, String id) {
        return baseUrlOf(repository) + "/api/v4/projects/" + id + "/repository";
    }

    @Override
    public Set<String> handledSchemes() {
        return ImmutableSet.of(GITLAB_HTTP_SCHEME, GITLAB_HTTPS_SCHEME);
    }

    private <T> Mono<T> mono(Supplier<T> supplier) {
        ReplayProcessor<T> subject = ReplayProcessor.create();
        runner.submit(() -> {
            try {
                subject.onNext(supplier.get());
                subject.onComplete();
            } catch (Exception e) {
                subject.onError(e);
            }
        });
        return Mono.fromDirect(subject);
    }

    private <T> Flux<T> flux(String uri, Class<T[]> listClass) {
        return mono(() -> restTemplate.getForObject(uri, listClass)).flatMapIterable(Arrays::asList);
    }

}