/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmad.modelpack.domain.JMadModelRepositories.cernGitlabTesting;
import static org.jmad.modelpack.domain.VariantType.BRANCH;
import static org.jmad.modelpack.domain.VariantType.RELEASE;

import java.net.URI;
import java.util.List;

import org.jmad.modelpack.connect.ModelPackageConnector;
import org.jmad.modelpack.connect.gitlab.GitlabGroupModelPackageConnector;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.junit.Before;
import org.junit.Test;

import reactor.core.publisher.Flux;

public class GitlabGroupModelPackageConnectorTest {

    private static final JMadModelPackageRepository REPO = cernGitlabTesting();
    public static final String LHC_TESTING_REPO = "jmad-modelpack-lhc-testing";
    private ModelPackageConnector service;

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
        assertThat(cernModels().stream().anyMatch(p -> p.modelPackage().name().equals(LHC_TESTING_REPO))).isTrue();
    }

    @Test
    public void resolvesLhcModelUriWithVariantToMaster() {
        ModelPackageVariant mpv = service.packageFromUri(URI.create(lhcTestRepoUri() + "@master")).block();
        assertThat(mpv).isNotNull();
        assertThat(mpv.variant().name()).isEqualTo("master");
        assertThat(mpv.variant().type()).isEqualTo(BRANCH);
    }

    @Test
    public void resolvesLhcModelUriWithoutVariantToLatestRelease() {
        ModelPackageVariant mpv = service.packageFromUri(URI.create(lhcTestRepoUri())).block();
        assertThat(mpv).isNotNull();
        assertThat(mpv.variant().name()).isEqualTo("v2018.1");
        assertThat(mpv.variant().type()).isEqualTo(RELEASE);
    }

    private String lhcTestRepoUri() {
        return cernGitlabTesting().uri() + "/" + LHC_TESTING_REPO;
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
