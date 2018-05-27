/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.conf;

import org.springframework.context.annotation.Import;

import cern.accsoft.steering.jmad.conf.JMadServiceConfiguration;

/**
 * Ready to use configuration for the jmad-modelpack-service project. If you need more fine grained management of the
 * beans, you can use the {@link JMadModelPackageServiceConfiguration}.
 */
@Import({ JMadModelPackageServiceConfiguration.class, JMadServiceConfiguration.class })
public class JMadModelPackageServiceStandaloneConfiguration {
    /* meta-configuration */
}
