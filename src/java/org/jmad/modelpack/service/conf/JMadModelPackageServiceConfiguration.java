/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.conf;

import static org.jmad.modelpack.domain.JMadModelRepositories.cernGitlabPro;
import static org.jmad.modelpack.domain.JMadModelRepositories.cernGitlabTesting;
import static org.jmad.modelpack.domain.JMadModelRepositories.internal;

import org.jmad.modelpack.cache.ModelPackageFileCache;
import org.jmad.modelpack.cache.impl.ModelPackageFileCacheImpl;
import org.jmad.modelpack.connect.embedded.conf.InternalConnectorConfiguration;
import org.jmad.modelpack.connect.gitlab.conf.GitlabConnectorConfiguration;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.impl.ConcurrentModelPackageRepositoryManager;
import org.jmad.modelpack.service.impl.MultiConnectorModelPackageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import cern.accsoft.steering.jmad.util.TempFileUtil;

@Import({ GitlabConnectorConfiguration.class, InternalConnectorConfiguration.class })
public class JMadModelPackageServiceConfiguration {

    @Bean
    public ModelPackageFileCache modelPackageFileCache(TempFileUtil tempFileUtil) {
        return new ModelPackageFileCacheImpl(tempFileUtil);
    }

    @Bean
    public ConcurrentModelPackageRepositoryManager packageRepositoryManager() {
        ConcurrentModelPackageRepositoryManager manager = new ConcurrentModelPackageRepositoryManager();
        manager.enable(cernGitlabPro());
        manager.enable(internal());
        manager.disable(cernGitlabTesting());
        return manager;
    }

    @Bean
    public JMadModelPackageService jmadModelPackageService() {
        return new MultiConnectorModelPackageService();
    }
}
