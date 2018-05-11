/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service;

import org.jmad.modelpack.domain.ModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;

import reactor.core.publisher.Flux;

public interface ModelPackageConnector {

    Flux<ModelPackageVariant> availablePackages(ModelPackageRepository repository);

   

}
