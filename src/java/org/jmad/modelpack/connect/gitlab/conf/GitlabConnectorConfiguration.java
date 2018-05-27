/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab.conf;

import org.jmad.modelpack.connect.ModelPackageConnector;
import org.jmad.modelpack.connect.gitlab.GitlabGroupModelPackageConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitlabConnectorConfiguration {

    @Bean
    public ModelPackageConnector gitlabConnector() {
        return new GitlabGroupModelPackageConnector();
    }

}
