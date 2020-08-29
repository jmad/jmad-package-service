package org.jmad.modelpack.service;

import java.net.URI;

import cern.accsoft.steering.jmad.model.JMadModel;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import org.jmad.modelpack.domain.ModelPackageVariant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    Mono<JMadModel> createModelFromUri(URI uri);

    Mono<ModelPackageVariant> packageFromUri(URI uri);

    Flux<ModelPackageVariant> availablePackages();

    Flux<JMadModelDefinition> modelDefinitionsFrom(ModelPackageVariant modelPackage);

    Mono<Void> clearCache();

    Mode mode();

    void setMode(Mode mode);

    enum Mode {
        ONLINE,
        OFFLINE;
    }
}
