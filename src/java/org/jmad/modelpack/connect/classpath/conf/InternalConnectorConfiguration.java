/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.classpath.conf;

import org.jmad.modelpack.connect.classpath.ClasspathModelPackageConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalConnectorConfiguration {

    @Bean
    public ClasspathModelPackageConnector internalModelPackageConnector() {
        return new ClasspathModelPackageConnector();
    }

}
