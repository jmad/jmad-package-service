/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect;

import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;

public interface ModelPackageConnector {

    Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository);

    Mono<ModelPackageVariant> packageFromUri(URI uri);

    Set<String> handledSchemes();

    default boolean canHandle(JMadModelPackageRepository repo) {
        return handledSchemes().contains(repo.connectorScheme());
    }

    default boolean canHandle(URI uri) {
        return handledSchemes().contains(uri.getScheme());
    }

}
