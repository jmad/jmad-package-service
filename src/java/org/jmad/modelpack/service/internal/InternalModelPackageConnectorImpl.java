/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.internal;

import static org.jmad.modelpack.service.internal.domain.InternalModelPackage.INTERNAL;
import static org.jmad.modelpack.service.internal.domain.InternalPackageVariant.NONE;

import org.jmad.modelpack.domain.ModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.ModelPackageVariantImpl;
import org.jmad.modelpack.service.InternalModelPackageConnector;
import org.jmad.modelpack.service.internal.domain.InternalRepository;
import org.springframework.beans.factory.annotation.Autowired;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
import reactor.core.publisher.Flux;

public class InternalModelPackageConnectorImpl implements InternalModelPackageConnector {

    @Autowired
    private JMadService jmadService;

    @Override
    public Flux<ModelPackageVariant> availablePackages(ModelPackageRepository repository) {
        if (repository == InternalRepository.INTERNAL) {
            return Flux.just(new ModelPackageVariantImpl(INTERNAL, NONE));
        } else {
            return Flux.empty();
        }
    }

    @Override
    public Flux<JMadModelDefinition> modelDefinitionsFor(ModelPackageVariant modelPackage) {
        if ((modelPackage.modelPackage() != INTERNAL) || (modelPackage.variant() != NONE)) {
            return Flux.empty();
        }
        return Flux.fromIterable(jmadService.getModelDefinitionManager().getAllModelDefinitions());
    }

}
