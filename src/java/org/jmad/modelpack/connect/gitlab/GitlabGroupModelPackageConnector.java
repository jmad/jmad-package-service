/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import org.jmad.modelpack.connect.ZipModelPackageConnector;
import org.jmad.modelpack.connect.gitlab.internals.GitlabBranch;
import org.jmad.modelpack.connect.gitlab.internals.GitlabProject;
import org.jmad.modelpack.connect.gitlab.internals.GitlabTag;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
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

import static org.jmad.modelpack.connect.ConnectorUriSchemes.GITLAB_HTTPS_SCHEME;
import static org.jmad.modelpack.connect.ConnectorUriSchemes.GITLAB_HTTP_SCHEME;

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
                .flatMap(p -> variantsFor(repository, p).map(v -> p.toModelPackage(repository, v)));
        // @formatter:on
    }

    private static String baseUrlOf(JMadModelPackageRepository repository) {
        URI uri = repository.repoUri();
        String protocol = uri.getScheme().replace("gitlab+", "");
        String host = uri.getAuthority();
        return protocol + "://" + host;
    }

    private static String gitlabGroupNameOf(JMadModelPackageRepository repository) {
        return repository.repoUri().getRawPath().substring(1);
    }

    @Override
    public Mono<Resource> zipResourceFor(ModelPackageVariant modelPackage) {
        ModelPackage pkg = modelPackage.modelPackage();
        if (!(pkg instanceof GitlabModelPackage)) {
            return Mono.empty();
        }
        GitlabModelPackage gitPkg = (GitlabModelPackage) pkg;

        String uri = repositoryUri(gitPkg.repository(), gitPkg.id()) + "/archive.zip" + variantParam(modelPackage.variant());
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

    public Flux<Variant> variantsFor(JMadModelPackageRepository repo, GitlabProject pkg) {
        return Flux.merge(tagsFor(repo, pkg.id), branchesFor(repo, pkg.id));
    }

    private static boolean filterOutIgnoredRepos(GitlabProject p) {
        if (p.tag_list.contains(JMAD_IGNORE_TAG)) {
            LOGGER.info("Ignoring Gitlab project {} because of {} tag", p.name, JMAD_IGNORE_TAG);
            return false;
        }
        return true;
    }

    private Flux<Variant> branchesFor(JMadModelPackageRepository repository, String id) {
        String uri = repositoryUri(repository, id) + "/branches";

        // @formatter:off
        return flux(uri, GitlabBranch[].class)
               .map(GitlabBranch::toBranch);
        // @formatter:on
    }

    private Flux<Variant> tagsFor(JMadModelPackageRepository repository, String id) {
        String uri = repositoryUri(repository, id) + "/tags";

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