/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import org.jmad.modelpack.connect.ConnectorIds;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GitlabGroupModelPackageConnector implements ZipModelPackageConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabGroupModelPackageConnector.class);

    private final WebClient webClient = WebClient.create();

    @Override
    public Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository) {
        if (!canHandle(repository)) {
            return Flux.empty();
        }
        String uri = repository.baseUrl() + "/api/v4/groups/" + repository.repoName() + "/projects";
        LOGGER.info("Querying model packages from '{}'.", uri);

        // @formatter:off
        return retrieve(uri)
                .bodyToFlux(GitlabProject.class)
                .flatMap(p -> variantsFor(repository, p).map(v -> p.toModelPackage(repository, v)));
        // @formatter:on
    }

    @Override
    public Mono<Resource> zipResourceFor(ModelPackageVariant modelPackage) {
        ModelPackage pkg = modelPackage.modelPackage();
        JMadModelPackageRepository repo = pkg.repository();
        if (!canHandle(repo)) {
            return Mono.empty();
        }

        String uri = repositoryUri(repo, pkg.id()) + "/archive.zip" + variantParam(modelPackage.variant());
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
        return "?sha=" + variant.name();
    }

    public Flux<Variant> variantsFor(JMadModelPackageRepository repo, GitlabProject pkg) {
        return Flux.merge(tagsFor(repo, pkg.id), branchesFor(repo, pkg.id));
    }

    private Flux<Variant> branchesFor(JMadModelPackageRepository repository, String id) {
        String uri = repositoryUri(repository, id) + "/branches";

        // @formatter:off
        return retrieve(uri)
               .bodyToFlux(GitlabBranch.class)
               .map(GitlabBranch::toBranch);
        // @formatter:on
    }

    private Flux<Variant> tagsFor(JMadModelPackageRepository repository, String id) {
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

    private static String repositoryUri(JMadModelPackageRepository repository, String id) {
        return repository.baseUrl() + "/api/v4/projects/" + id + "/repository";
    }

    @Override
    public String connectorId() {
        return ConnectorIds.GITLAB_GROUP_API_V4;
    }

}