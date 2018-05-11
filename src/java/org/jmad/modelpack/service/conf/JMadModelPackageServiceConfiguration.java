/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.conf;

import static org.jmad.modelpack.JMadModelRepositories.cernGitlabPro;
import static org.jmad.modelpack.JMadModelRepositories.cernGitlabTesting;
import static org.jmad.modelpack.JMadModelRepositories.internal;

import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.ModelPackageFileCache;
import org.jmad.modelpack.service.gitlab.conf.GitlabConnectorConfiguration;
import org.jmad.modelpack.service.impl.ConcurrentModelPackageRepositoryManager;
import org.jmad.modelpack.service.impl.ModelPackageFileCacheImpl;
import org.jmad.modelpack.service.impl.MultiConnectorModelPackageService;
import org.jmad.modelpack.service.internal.conf.InternalConnectorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import cern.accsoft.steering.jmad.util.TempFileUtil;

@Import({ GitlabConnectorConfiguration.class, InternalConnectorConfiguration.class })
@ImportResource(locations = "classpath:app-ctx-jmad-service.xml")
public class JMadModelPackageServiceConfiguration {

    @Bean
    public ModelPackageFileCache modelPackageFileCache(TempFileUtil tempFileUtil) {
        return new ModelPackageFileCacheImpl(tempFileUtil);
    }

    @Bean
    public ConcurrentModelPackageRepositoryManager packageRepositoryManager() {
        ConcurrentModelPackageRepositoryManager manager = new ConcurrentModelPackageRepositoryManager();
        manager.enable(cernGitlabTesting());
        manager.disable(cernGitlabPro());
        manager.enable(internal());
        return manager;
    }

    @Bean
    public JMadModelPackageService jmadModelPackageService() {
        return new MultiConnectorModelPackageService();
    }
}
