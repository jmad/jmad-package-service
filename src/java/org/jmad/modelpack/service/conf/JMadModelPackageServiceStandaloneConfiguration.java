/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.conf;

import cern.accsoft.steering.jmad.conf.JMadServiceConfiguration;
import cern.accsoft.steering.jmad.util.TempFileUtil;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.ModelPackageFileCache;
import org.jmad.modelpack.service.gitlab.conf.GitlabConnectorConfiguration;
import org.jmad.modelpack.service.impl.ConcurrentModelPackageRepositoryManager;
import org.jmad.modelpack.service.impl.ModelPackageFileCacheImpl;
import org.jmad.modelpack.service.impl.MultiConnectorModelPackageService;
import org.jmad.modelpack.service.internal.conf.InternalConnectorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.jmad.modelpack.JMadModelRepositories.cernGitlabPro;
import static org.jmad.modelpack.JMadModelRepositories.cernGitlabTesting;
import static org.jmad.modelpack.JMadModelRepositories.internal;

/**
 * Ready to use configuration for the jmad-modelpack-service project. If you need more fine grained management of the
 * beans, you can use the {@link JMadModelPackageServiceConfiguration}.
 */
@Import({ JMadModelPackageServiceConfiguration.class, JMadServiceConfiguration.class })
public class JMadModelPackageServiceStandaloneConfiguration {
    /* meta-configuration */
}
