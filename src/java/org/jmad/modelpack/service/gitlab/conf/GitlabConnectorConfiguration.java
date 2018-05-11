/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab.conf;

import org.jmad.modelpack.service.gitlab.GitlabGroupModelPackageConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitlabConnectorConfiguration {

    @Bean
    public GitlabGroupModelPackageConnector gitlabConnector() {
        return new GitlabGroupModelPackageConnector();
    }

}