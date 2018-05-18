/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import org.jmad.modelpack.domain.ModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.service.ZipModelPackageConnector;
import org.jmad.modelpack.service.gitlab.domain.Branch;
import org.jmad.modelpack.service.gitlab.domain.Tag;
import org.jmad.modelpack.service.gitlab.internals.GitlabBranch;
import org.jmad.modelpack.service.gitlab.internals.GitlabProject;
import org.jmad.modelpack.service.gitlab.internals.GitlabTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GitlabGroupModelPackageConnector implements ZipModelPackageConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabGroupModelPackageConnector.class);

    private final WebClient webClient = WebClient.create();

    @Override
    public Flux<ModelPackageVariant> availablePackages(ModelPackageRepository repository) {
        if (!(repository instanceof GitlabModelPackageRepository)) {
            return Flux.empty();
        }
        GitlabModelPackageRepository repo = (GitlabModelPackageRepository) repository;

        String uri = repo.baseUrl() + "/api/v4/groups/" + repo.groupName() + "/projects";
        LOGGER.info("Querying model packages from '{}'.", uri);

        // @formatter:off
        return retrieve(uri)
                .bodyToFlux(GitlabProject.class)
                .flatMap(p -> variantsFor(repo, p).map(v -> p.toModelPackage(repo, v)));
        // @formatter:on
    }

    @Override
    public Mono<Resource> zipResourceFor(ModelPackageVariant modelPackage) {
        if (!(modelPackage.modelPackage() instanceof GitlabModelPackage)) {
            return Mono.empty();
        }
        GitlabModelPackage pkg = (GitlabModelPackage) modelPackage.modelPackage();

        String uri = repositoryUri(pkg.repository(), pkg.id()) + "/archive.zip" + variantParam(modelPackage.variant());
        LOGGER.info("Retrieving package from {}.", uri);

        // @formatter:off
        return webClient.get()
                 .uri(uri)
                 .accept(APPLICATION_OCTET_STREAM)
                 .retrieve()
                 .bodyToMono(Resource.class);
        // @formatter:on
    }

    private static String variantParam(Variant variant) {
        if (variant instanceof Tag) {
            return "?sha=" + variant.name();
        }
        if (variant instanceof Branch) {
            return "?sha=" + variant.name();
        }
        return "";
    }

    public Flux<Variant> variantsFor(GitlabModelPackageRepository repo, GitlabProject pkg) {
        return Flux.merge(tagsFor(repo, pkg.id), branchesFor(repo, pkg.id));
    }

    private Flux<Branch> branchesFor(GitlabModelPackageRepository repository, String id) {
        String uri = repositoryUri(repository, id) + "/branches";

        // @formatter:off
        return retrieve(uri)
               .bodyToFlux(GitlabBranch.class)
               .map(GitlabBranch::toBranch);
        // @formatter:on
    }

    private Flux<Tag> tagsFor(GitlabModelPackageRepository repository, String id) {
        String uri = repositoryUri(repository, id) + "/tags";

        // @formatter:off
        return retrieve(uri)
               .bodyToFlux(GitlabTag.class)
               .map(GitlabTag::toTag);
        // @formatter:on
    }

    private ResponseSpec retrieve(String uri) {
        // @formatter:off
        return webClient.get()
                .uri(uri)
                .retrieve();
        // @formatter:on
    }

    private static String repositoryUri(GitlabModelPackageRepository repository, String id) {
        return repository.baseUrl() + "/api/v4/projects/" + id + "/repository";
    }

}