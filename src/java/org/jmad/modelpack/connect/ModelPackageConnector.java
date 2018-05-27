/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect;

import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;

import reactor.core.publisher.Flux;

public interface ModelPackageConnector {

    Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository);

    String connectorId();

    default boolean canHandle(JMadModelPackageRepository repo) {
        return connectorId().equals(repo.connectorId());
    }

}
