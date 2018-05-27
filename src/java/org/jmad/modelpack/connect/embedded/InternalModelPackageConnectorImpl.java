/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.embedded;

import static org.jmad.modelpack.connect.embedded.domain.InternalModelPackage.INTERNAL;
import static org.jmad.modelpack.connect.embedded.domain.InternalPackageVariant.NONE;

import org.jmad.modelpack.connect.ConnectorIds;
import org.jmad.modelpack.connect.InternalModelPackageConnector;
import org.jmad.modelpack.connect.embedded.domain.InternalRepository;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.springframework.beans.factory.annotation.Autowired;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
import reactor.core.publisher.Flux;

public class InternalModelPackageConnectorImpl implements InternalModelPackageConnector {

    @Autowired
    private JMadService jmadService;

    @Override
    public Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository) {
        if (repository == InternalRepository.INTERNAL) {
            return Flux.just(new ModelPackageVariant(INTERNAL, NONE));
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

    @Override
    public String connectorId() {
        return ConnectorIds.INTERNAL_CONNECTOR_ID;
    }

}
