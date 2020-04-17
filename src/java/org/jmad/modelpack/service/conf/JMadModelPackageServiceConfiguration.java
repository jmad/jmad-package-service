/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.conf;

import cern.accsoft.steering.jmad.service.JMadService;
import cern.accsoft.steering.jmad.util.JMadPreferences;
import cern.accsoft.steering.jmad.util.TempFileUtilImpl;
import org.jmad.modelpack.cache.ModelPackageFileCache;
import org.jmad.modelpack.cache.impl.ModelPackageFileCacheImpl;
import org.jmad.modelpack.connect.classpath.conf.InternalConnectorConfiguration;
import org.jmad.modelpack.connect.gitlab.conf.GitlabConnectorConfiguration;
import org.jmad.modelpack.connect.localfile.conf.LocalFileConnectorConfiguration;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.impl.ConcurrentModelPackageRepositoryManager;
import org.jmad.modelpack.service.impl.MultiConnectorModelPackageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.jmad.modelpack.domain.JMadModelRepositories.cernGitlabOld;
import static org.jmad.modelpack.domain.JMadModelRepositories.cernGitlabPro;
import static org.jmad.modelpack.domain.JMadModelRepositories.cernGitlabTesting;
import static org.jmad.modelpack.domain.JMadModelRepositories.defaultLocalFileRepository;
import static org.jmad.modelpack.domain.JMadModelRepositories.internal;

/**
 * Spring configuration that only creates the beans for the jmad-modelpack-service. It expects all the necessary beans
 * already in the context. You can use the {@link JMadModelPackageServiceStandaloneConfiguration} if you want to
 * have a fully configured, ready-to-use, context.
 */
@Import({GitlabConnectorConfiguration.class,
        InternalConnectorConfiguration.class,
        LocalFileConnectorConfiguration.class})
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
        manager.enable(cernGitlabOld());
        manager.enable(cernGitlabPro());
        manager.enable(internal());
        defaultLocalFileRepository().ifPresent(manager::enable);
        manager.disable(cernGitlabTesting());
        return manager;
    }

    @Bean("jmadModelPackageService")
    public JMadModelPackageService jmadModelPackageService() {
        return new MultiConnectorModelPackageService();
    }
}
