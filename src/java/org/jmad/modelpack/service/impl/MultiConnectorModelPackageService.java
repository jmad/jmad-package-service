/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.impl;

import static java.util.stream.Collectors.toList;
import static org.jmad.modelpack.service.JMadModelPackageService.Mode.ONLINE;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.jmad.modelpack.cache.ModelPackageFileCache;
import org.jmad.modelpack.connect.InternalModelPackageConnector;
import org.jmad.modelpack.connect.ModelPackageConnector;
import org.jmad.modelpack.connect.ZipModelPackageConnector;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import cern.accsoft.steering.jmad.service.JMadService;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.JMadModelPackageRepositoryProvider;
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
    private JMadModelPackageRepositoryProvider provider;
    @Autowired
    private List<ModelPackageConnector> connectors;
    @Autowired
    private JMadService jMadService;
    @Autowired
    private ModelPackageFileCache cache;

    private final AtomicReference<Mode> mode = new AtomicReference<>(ONLINE);

    @PostConstruct
    public void init() {
        LOGGER.info("Available model package connectors: {}.", connectors);
    }

    @Override
    public Flux<ModelPackageVariant> availablePackages() {
        if (ONLINE == mode.get()) {
            return provider.enabledRepositories().flatMap(this::packagesFrom);
        } else {
            return cache.cachedPackageVariants();
        }
    }

    private Function<ModelPackageVariant, Mono<Resource>> resourceCallback() {
        if (ONLINE == mode.get()) {
            return this::zipResourceFrom;
        } else {
            return this::errorOffline;
        }
    }

    @Override
    public Flux<JMadModelDefinition> modelDefinitionsFrom(ModelPackageVariant modelPackage) {
        return definitionsFromDirect(modelPackage).switchIfEmpty(definitionsFromFile(modelPackage));
    }

    @Override
    public Mono<Void> clearCache() {
        return cache.clear();
    }

    private Mono<Resource> errorOffline(@SuppressWarnings("unused") ModelPackageVariant modelPackage) {
        return Mono.error(new IllegalStateException("service is in OFFLINE mode. No resource download is possible."));
    }

    private Flux<JMadModelDefinition> definitionsFromDirect(ModelPackageVariant modelPackage) {
        // @formatter:off
        List<Flux<JMadModelDefinition>> directStreams = 
                connectors.stream()
                .filter(c -> c instanceof InternalModelPackageConnector)
                .map(c -> (InternalModelPackageConnector)c)
                .map(c -> c.modelDefinitionsFor(modelPackage))
                .collect(toList());
        // @formatter:on

        return Flux.merge(directStreams);
    }

    private Flux<JMadModelDefinition> definitionsFromFile(ModelPackageVariant modelPackage) {
        // @formatter:off
        return cache.fileFor(modelPackage, resourceCallback())
                .flatMapMany(this::modelDefinitionsFrom);
        // @formatter:on
    }

    private Flux<JMadModelDefinition> modelDefinitionsFrom(File file) {
        return Flux.fromIterable(jMadService.getModelDefinitionImporter().importModelDefinitions(file));
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

    private Flux<ModelPackageVariant> packagesFrom(JMadModelPackageRepository repo) {
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

    @Override
    public Mode mode() {
        return this.mode.get();
    }

    @Override
    public void setMode(Mode mode) {
        this.mode.set(mode);
    }

}
