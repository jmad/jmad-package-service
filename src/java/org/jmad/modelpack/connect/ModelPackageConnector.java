/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect;

import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;

import reactor.core.publisher.Flux;

import java.util.Set;

public interface ModelPackageConnector {

    Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository);

    Set<String> handledSchemes();

    default boolean canHandle(JMadModelPackageRepository repo) {
        return handledSchemes().contains(repo.connectorScheme());
    }

}
