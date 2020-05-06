/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.classpath;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
import org.jmad.modelpack.connect.DirectModelPackageConnector;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.domain.VariantType;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.jmad.modelpack.connect.ConnectorUriSchemes.INTERNAL_SCHEME;

public class ClasspathModelPackageConnector implements DirectModelPackageConnector {
    private static final String INTERNAL = "INTERNAL";
    private static final Variant INTERNAL_VARIANT = new Variant(INTERNAL, VariantType.RELEASE);
    public static final URI INTERNAL_URI = URI.create(INTERNAL_SCHEME + ":/");
    public static final ModelPackage INTERNAL_MODEL_PACKAGE = new ModelPackage(INTERNAL,
            JMadModelPackageRepository.fromUri(INTERNAL_URI), INTERNAL_URI);
    private static final ModelPackageVariant INTERNAL_MODEL_PACKAGE_VARIANT = new ModelPackageVariant(INTERNAL_URI,
            INTERNAL_MODEL_PACKAGE, INTERNAL_VARIANT);

    @Autowired
    private JMadService jmadService;

    @Override
    public Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository) {
        if (canHandle(repository)) {
            return Flux.just(INTERNAL_MODEL_PACKAGE_VARIANT);
        } else {
            return Flux.empty();
        }
    }

    @Override
    public Mono<ModelPackageVariant> packageFromUri(URI uri) {
        if (canHandle(uri)) {
            return Mono.just(INTERNAL_MODEL_PACKAGE_VARIANT);
        } else {
            return Mono.empty();
        }
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
