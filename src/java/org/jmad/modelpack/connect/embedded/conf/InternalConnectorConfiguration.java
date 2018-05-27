/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.embedded.conf;

import org.jmad.modelpack.connect.embedded.InternalModelPackageConnectorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalConnectorConfiguration {

    @Bean
    public InternalModelPackageConnectorImpl internalModelPackageConnector() {
        return new InternalModelPackageConnectorImpl();
    }

}
