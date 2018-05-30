/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab;

import org.jmad.modelpack.connect.ConnectorIds;
import org.jmad.modelpack.connect.gitlab.GitlabGroupModelPackageConnector;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;

public class GitlabTries {

    private static Logger LOGGER = LoggerFactory.getLogger(GitlabTries.class);

    private GitlabGroupModelPackageConnector connector;
    private JMadModelPackageRepository repo;

    @Before
    public void setUp() {
        connector = new GitlabGroupModelPackageConnector();
        repo = new JMadModelPackageRepository("https://gitlab.cern.ch", "jmad-modelpacks-testing",
                ConnectorIds.GITLAB_GROUP_API_V4);
    }

    @Test
    public void fetchProjectOfGroups() {
        Flux<ModelPackageVariant> flux = connector.availablePackages(repo);
        LOGGER.info("Received: {}", flux.collectList().block());
    }

}
