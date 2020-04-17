/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.classpath;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
import org.jmad.modelpack.connect.DirectModelPackageConnector;
import org.jmad.modelpack.domain.Commit;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Set;

import static org.jmad.modelpack.connect.ConnectorUriSchemes.INTERNAL_SCHEME;

public class ClasspathModelPackageConnector implements DirectModelPackageConnector {
    public static final String INTERNAL = "INTERNAL";
    private static final Variant INTERNAL_VARIANT = Variant.release(INTERNAL, new Commit(INTERNAL, INTERNAL));

    @Autowired
    private JMadService jmadService;

    @Override
    public Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository) {
        if (canHandle(repository)) {
            return Flux.just(internalModelPackVariant(repository));
        } else {
            return Flux.empty();
        }
    }

    private static ModelPackageVariant internalModelPackVariant(JMadModelPackageRepository repository) {
        return new ModelPackageVariant(new ModelPackage(INTERNAL, repository, repository.repoUri()), INTERNAL_VARIANT);
    }

    @Override
    public Flux<JMadModelDefinition> modelDefinitionsFor(ModelPackageVariant modelPackage) {
        if (modelPackage.variant() != INTERNAL_VARIANT || !canHandle(modelPackage.modelPackage().repository())) {
            return Flux.empty();
        }
        return Flux.fromIterable(jmadService.getModelDefinitionManager().getAllModelDefinitions());
    }

    @Override
    public Set<String> handledSchemes() {
        return Collections.singleton(INTERNAL_SCHEME);
    }
}
