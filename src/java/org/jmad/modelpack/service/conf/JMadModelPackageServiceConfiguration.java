/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.conf;

import static org.jmad.modelpack.JMadModelRepositories.cernGitlabPro;
import static org.jmad.modelpack.JMadModelRepositories.cernGitlabTesting;
import static org.jmad.modelpack.JMadModelRepositories.internal;

import cern.accsoft.steering.jmad.service.JMadService;
import cern.accsoft.steering.jmad.util.JMadPreferences;
import cern.accsoft.steering.jmad.util.TempFileUtilImpl;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.ModelPackageFileCache;
import org.jmad.modelpack.service.gitlab.conf.GitlabConnectorConfiguration;
import org.jmad.modelpack.service.impl.ConcurrentModelPackageRepositoryManager;
import org.jmad.modelpack.service.impl.ModelPackageFileCacheImpl;
import org.jmad.modelpack.service.impl.MultiConnectorModelPackageService;
import org.jmad.modelpack.service.internal.conf.InternalConnectorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import cern.accsoft.steering.jmad.util.TempFileUtil;

/**
 * Spring configuration that only creates the beans for the jmad-modelpack-service. It expects all the necessary beans
 * already in the context. You can use the {@link JMadModelPackageServiceStandaloneConfiguration} if you want to
 * have a fully configured, ready-to-use, context.
 */
@Import({ GitlabConnectorConfiguration.class, InternalConnectorConfiguration.class })
public class JMadModelPackageServiceConfiguration {

    @Bean
    public ModelPackageFileCache modelPackageFileCache(JMadService jMadService) {
        JMadPreferences preferences = jMadService.getPreferences();
        TempFileUtilImpl tempFileUtil = new TempFileUtilImpl();
        tempFileUtil.setPreferences(preferences);
        tempFileUtil.init();
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

    @Bean("jmadModelPackageService")
    public JMadModelPackageService jmadModelPackageService() {
        return new MultiConnectorModelPackageService();
    }
}
