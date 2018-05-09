/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.gitlab;

import org.jmad.modpack.service.gitlab.internals.GitlabProject;
import org.jmad.modpack.service.gitlab.internals.GitlabTag;
import org.jmad.modpack.service.gitlab.internals.GitlabTreeNode;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GitlabTries {

    private static Logger LOGGER = LoggerFactory.getLogger(GitlabTries.class);

    private WebClient webClient;

    @Before
    public void setUp() {
        this.webClient = WebClient.create("https://gitlab.cern.ch");
    }

    @Test
    public void fetchProjectOfGroups() throws InterruptedException {
        String path = "/api/v4/groups/jmad-models-cern-testing/projects";

        // @formatter:off
        Flux<GitlabProject> flux = webClient.get()
                .uri(path)
                .retrieve()
                .bodyToFlux(GitlabProject.class);
        // @formatter:on

        LOGGER.info("Received: {}", flux.collectList().block());
    }

    @Test
    public void fetchProjectEntries() {
        String uri = "api/v4/projects/37589/repository/tree";

        // @formatter:off
        Flux<GitlabTreeNode> flux = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(GitlabTreeNode.class);
        // @formatter:on

        LOGGER.info("Received: {}", flux.collectList().block());
    }

    @Test
    public void downloadZipFile() {
        String uri = "api/v4/projects/37589/repository/archive.zip";

     // @formatter:off
        Mono<Resource> flux = webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(Resource.class);
        // @formatter:on

        LOGGER.info("Received: {}", flux.block());
    }

    @Test
    public void fetchTags() {
        String uri = "api/v4/projects/37589/repository/tags";

        // @formatter:off
           Flux<GitlabTag> flux = webClient.get()
                   .uri(uri)
                   .retrieve()
                   .bodyToFlux(GitlabTag.class);
           // @formatter:on

        LOGGER.info("Received: {}", flux.collectList().block());
    }
}
