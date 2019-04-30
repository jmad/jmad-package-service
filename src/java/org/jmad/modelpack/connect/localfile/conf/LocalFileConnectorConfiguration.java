package org.jmad.modelpack.connect.localfile.conf;

import org.jmad.modelpack.connect.ModelPackageConnector;
import org.jmad.modelpack.connect.localfile.LocalFileModelPackageConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalFileConnectorConfiguration {
    @Bean
    public ModelPackageConnector localFileConnector() {
        return new LocalFileModelPackageConnector();
    }
}
