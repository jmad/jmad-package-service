/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmad.modelpack.JMadModelRepositories.cernGitlabTesting;

import java.util.List;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.gitlab.GitlabGroupModelPackageConnector;
import org.jmad.modelpack.service.gitlab.GitlabModelPackageRepository;
import org.junit.Before;
import org.junit.Test;

import reactor.core.publisher.Flux;

public class GitlabGroupModelPackageConnectorTest {

    private static final GitlabModelPackageRepository REPO = cernGitlabTesting();
    private GitlabGroupModelPackageConnector service;

    @Before
    public void setUp() {
        service = new GitlabGroupModelPackageConnector();
    }

    @Test
    public void notNullFluxIsReturned() {
        assertThat(cernModelFlux()).isNotNull();
    }

    @Test
    public void containsLhcModel() {
        assertThat(cernModels().stream().anyMatch(p -> p.modelPackage().name().equals("jmad-model-lhc"))).isTrue();
    }

    @Test
    public void printCernModels() {
        System.out.println(cernModels());
    }

    private List<ModelPackageVariant> cernModels() {
        List<ModelPackageVariant> cernModels = cernModelFlux().collectList().block();
        System.out.println("CERN models: " + cernModels);
        return cernModels;
    }

    private Flux<ModelPackageVariant> cernModelFlux() {
        return service.availablePackages(REPO);
    }

}
