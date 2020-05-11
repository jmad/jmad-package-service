/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.impl;

import static java.util.stream.Collectors.toList;
import static org.jmad.modelpack.service.JMadModelPackageService.Mode.ONLINE;
import static org.jmad.modelpack.util.ModelUris.findModelDefinitionFromUri;
import static org.jmad.modelpack.util.ModelUris.startupConfigurationFromUri;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import cern.accsoft.steering.jmad.factory.JMadModelFactory;
import cern.accsoft.steering.jmad.model.JMadModel;
import cern.accsoft.steering.jmad.model.JMadModelStartupConfiguration;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinitionImpl;
import org.jmad.modelpack.cache.ModelPackageFileCache;
import org.jmad.modelpack.connect.DirectModelPackageConnector;
import org.jmad.modelpack.connect.ModelPackageConnector;
import org.jmad.modelpack.connect.ZipModelPackageConnector;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.JMadModelPackageRepositoryProvider;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
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
    public Mono<JMadModel> createModelFromUri(URI uri) {
        return packageFromUri(uri).flatMapMany(this::modelDefinitionsFrom).collectList().map(modelPackList -> {
            JMadModelDefinition modelDefinition = findModelDefinitionFromUri(uri, modelPackList);
            JMadModelStartupConfiguration startupConfiguration = startupConfigurationFromUri(uri, modelDefinition);
            return jMadService.createModel(modelDefinition, startupConfiguration);
        });
    }

    @Override
    public Mono<ModelPackageVariant> packageFromUri(URI uri) {
        return connectors.stream().filter(c -> c.canHandle(uri)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No connector can handle URI " + uri))
                .packageFromUri(uri);
    }

    @Override
    public Flux<JMadModelDefinition> modelDefinitionsFrom(ModelPackageVariant modelPackage) {
        return definitionsFromDirect(modelPackage).switchIfEmpty(definitionsFromFile(modelPackage))
                .doOnNext(md -> setModelPackUri(md, modelPackage));
    }

    private static void setModelPackUri(JMadModelDefinition md, ModelPackageVariant modelPackage) {
        JMadModelDefinitionImpl modelDefinition = (JMadModelDefinitionImpl) md;
        modelDefinition.setModelPackUri(modelPackage.uri().toASCIIString());
    }

    @Override
    public Mono<Void> clearCache() {
        return cache.clear();
    }

    private Mono<Resource> errorOffline(@SuppressWarnings("unused") ModelPackageVariant modelPackage) {
        return Mono.error(new IllegalStateException("service is in OFFLINE mode. No resource download is possible."));
    }

    private Flux<JMadModelDefinition> definitionsFromDirect(ModelPackageVariant modelPackage) {
        List<Flux<JMadModelDefinition>> directStreams = connectors.stream()
                .filter(c -> c instanceof DirectModelPackageConnector).map(c -> (DirectModelPackageConnector) c)
                .map(c -> c.modelDefinitionsFor(modelPackage)).collect(toList());

        return Flux.merge(directStreams);
    }

    private Flux<JMadModelDefinition> definitionsFromFile(ModelPackageVariant modelPackage) {
        return cache.fileFor(modelPackage, resourceCallback()).flatMapMany(this::modelDefinitionsFrom);
    }

    private Flux<JMadModelDefinition> modelDefinitionsFrom(File file) {
        return Flux.fromIterable(jMadService.getModelDefinitionImporter().importModelDefinitions(file));
    }

    private Mono<Resource> zipResourceFrom(ModelPackageVariant modelPackage) {
        List<Mono<Resource>> connectorStreams = connectors.stream().filter(c -> c instanceof ZipModelPackageConnector)
                .map(c -> (ZipModelPackageConnector) c).map(c -> c.zipResourceFor(modelPackage)).collect(toList());

        return Mono.first(connectorStreams);
    }

    private Flux<ModelPackageVariant> packagesFrom(JMadModelPackageRepository repo) {
        List<Flux<ModelPackageVariant>> connectorStreams = connectors.stream().filter(c -> c.canHandle(repo))
                .map(c -> c.availablePackages(repo).onErrorResume(t -> {
                    LOGGER.warn("Error while retrieving packages from repo {} from connector {}. Returning empty.",
                            repo, c, t);
                    return Flux.empty();
                })).collect(toList());

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
