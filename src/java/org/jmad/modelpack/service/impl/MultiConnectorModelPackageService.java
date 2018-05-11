/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.impl;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jmad.modelpack.domain.ModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.DirectModelPackageConnector;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.ModelPackageConnector;
import org.jmad.modelpack.service.ModelPackageFileCache;
import org.jmad.modelpack.service.ModelPackageRepositoryProvider;
import org.jmad.modelpack.service.ZipModelPackageConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.io.JMadModelDefinitionImporter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultiConnectorModelPackageService implements JMadModelPackageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiConnectorModelPackageService.class);

    @Autowired
    private ModelPackageRepositoryProvider provider;
    @Autowired
    private List<ModelPackageConnector> connectors;
    @Autowired
    private JMadModelDefinitionImporter importer;
    @Autowired
    private ModelPackageFileCache cache;

    @PostConstruct
    public void init() {
        LOGGER.info("Available model package connectors: {}.", connectors);
    }

    @Override
    public Flux<ModelPackageVariant> availablePackages() {
        return provider.enabledRepositories().flatMap(this::packagesFrom);
    }

    @Override
    public Flux<JMadModelDefinition> modelDefinitionsFrom(ModelPackageVariant modelPackage) {
        return definitionsFromDirect(modelPackage).switchIfEmpty(definitiionsFromFile(modelPackage));
    }

    private Flux<JMadModelDefinition> definitionsFromDirect(ModelPackageVariant modelPackage) {
        // @formatter:off
        List<Flux<JMadModelDefinition>> directStreams = 
                connectors.stream()
                .filter(c -> c instanceof DirectModelPackageConnector)
                .map(c -> (DirectModelPackageConnector)c)
                .map(c -> c.modelDefinitionsFor(modelPackage))
                .collect(toList());
        // @formatter:on

        return Flux.merge(directStreams);
    }

    private Flux<JMadModelDefinition> definitiionsFromFile(ModelPackageVariant modelPackage) {
        // @formatter:off
        return cache.fileFor(modelPackage, this::zipResourceFrom)
                .flatMapMany(this::modelDefinitionsFrom);
        // @formatter:on
    }

    private Flux<JMadModelDefinition> modelDefinitionsFrom(File file) {
        return Flux.fromIterable(importer.importModelDefinitions(file));
    }

    private Mono<Resource> zipResourceFrom(ModelPackageVariant modelPackage) {
        // @formatter:off
        List<Mono<Resource>> connectorStreams = 
                connectors.stream()
                .filter(c -> c instanceof ZipModelPackageConnector)
                .map(c -> (ZipModelPackageConnector)c)
                .map(c -> c.zipResourceFor(modelPackage))
                .collect(toList());
        // @formatter:on

        return Mono.first(connectorStreams);
    }

    private Flux<ModelPackageVariant> packagesFrom(ModelPackageRepository repo) {
        // @formatter:off
        List<Flux<ModelPackageVariant>> connectorStreams 
                = connectors.stream()
                    .map(c -> c.availablePackages(repo).onErrorResume(t -> {
                        LOGGER.warn("Error while retrieving packages from repo {} from connector {}. Returning empty.", repo, c, t);
                        return Flux.empty();}))
                    .collect(toList());
        // @formatter:on

        return Flux.merge(connectorStreams);
    }

}
