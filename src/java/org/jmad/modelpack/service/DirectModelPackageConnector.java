/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service;

import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import reactor.core.publisher.Flux;

public interface DirectModelPackageConnector extends ModelPackageConnector {

    Flux<JMadModelDefinition> modelDefinitionsFor(ModelPackageVariant modelPackage);

}
