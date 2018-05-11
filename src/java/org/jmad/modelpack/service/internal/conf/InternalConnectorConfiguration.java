/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.internal.conf;

import org.jmad.modelpack.service.internal.InternalModelPackageConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalConnectorConfiguration {

    @Bean
    public InternalModelPackageConnector internalModelPackageConnector() {
        return new InternalModelPackageConnector();
    }

}