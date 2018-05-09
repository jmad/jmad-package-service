package org.jmad.modpack.service;

import org.jmad.modpack.domain.ModelPackageVariant;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import reactor.core.publisher.Flux;

/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

/**
 * This is the top interface to retrieve jmad model packages and to get available model definitions from them. It is
 * designed using reactive concepts, to acknowledge the fact that packages might be remotely queried and thus are better
 * handled in an asynchronous way.
 * 
 * @author kfuchsbe
 */
public interface JMadModelPackageService {

    Flux<ModelPackageVariant> availablePackages();

    Flux<JMadModelDefinition> modelDefinitionsFrom(ModelPackageVariant modelPackage);
}
