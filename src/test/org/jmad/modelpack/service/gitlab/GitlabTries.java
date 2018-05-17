/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab;

import org.jmad.modelpack.service.gitlab.internals.GitlabProject;
import org.jmad.modelpack.service.gitlab.internals.GitlabTag;
import org.jmad.modelpack.service.gitlab.internals.GitlabTreeNode;
import org.junit.Before;
import org.junit.Ignore;
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
    public void fetchProjectOfGroups() {
        String path = "/api/v4/groups/jmad-modelpacks-testing/projects";

        // @formatter:off
        Flux<GitlabProject> flux = webClient.get()
                .uri(path)
                .retrieve()
                .bodyToFlux(GitlabProject.class);
        // @formatter:on

        LOGGER.info("Received: {}", flux.collectList().block());
    }

}
