/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.impl;

import static java.util.Collections.emptyMap;
import static org.jmad.modpack.service.ModelPackageRepositoryManager.EnableState.ENABLED;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jmad.modpack.domain.ModelPackageRepository;
import org.jmad.modpack.service.ModelPackageRepositoryManager;
import org.jmad.modpack.service.ModelPackageRepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

public class ConcurrentModelPackageRepositoryManager implements ModelPackageRepositoryProvider, ModelPackageRepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentModelPackageRepositoryManager.class);
    
    private final Map<ModelPackageRepository, EnableState> repositories = new ConcurrentHashMap<>();
    
    private final ReplayProcessor<Map<ModelPackageRepository, EnableState>> states = ReplayProcessor
            .cacheLastOrDefault(emptyMap());

    @Override
    public void remove(ModelPackageRepository repository) {
        repositories.remove(repository);
        LOGGER.info("Removed repository {}.", repository);
        publishState();
    }

    @Override
    public void enable(ModelPackageRepository repository) {
        repositories.put(repository, EnableState.ENABLED);
        LOGGER.info("Enabled repository {}.", repository);
        publishState();
    }

    @Override
    public void disable(ModelPackageRepository repository) {
        repositories.put(repository, EnableState.DISABLED);
        LOGGER.info("Disabled repository {}.", repository);
        publishState();
    }

    @Override
    public Flux<ModelPackageRepository> enabledRepositories() {
        // @formatter:off
        return Flux.fromIterable(repositories.entrySet())
                .filter(e -> ENABLED == e.getValue())
                .map(Entry::getKey);
        // @formatter:on
    }

    private void publishState() {
        states.onNext(ImmutableMap.copyOf(repositories));
    }

    @Override
    public Flux<Map<ModelPackageRepository, EnableState>> state() {
        return this.states;
    }
    
}
