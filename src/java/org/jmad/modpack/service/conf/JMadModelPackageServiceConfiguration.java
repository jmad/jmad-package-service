/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.conf;

import static org.jmad.modpack.JMadModelRepositories.cernGitlabPro;
import static org.jmad.modpack.JMadModelRepositories.cernGitlabTesting;

import org.jmad.modpack.service.JMadModelPackageService;
import org.jmad.modpack.service.ModelPackageFileCache;
import org.jmad.modpack.service.gitlab.conf.GitlabConnectorConfiguration;
import org.jmad.modpack.service.impl.ConcurrentModelPackageRepositoryManager;
import org.jmad.modpack.service.impl.ModelPackageFileCacheImpl;
import org.jmad.modpack.service.impl.MultiConnectorModelPackageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import cern.accsoft.steering.jmad.util.TempFileUtil;

@Import(GitlabConnectorConfiguration.class)
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
        return manager;
    }
    
    @Bean
    public JMadModelPackageService jmadModelPackageService() {
        return new MultiConnectorModelPackageService();
    }
}
