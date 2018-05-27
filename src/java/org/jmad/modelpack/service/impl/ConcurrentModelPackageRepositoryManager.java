/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.impl;

import static java.util.Collections.emptyMap;
import static org.jmad.modelpack.service.JMadModelPackageRepositoryManager.EnableState.ENABLED;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.service.JMadModelPackageRepositoryManager;
import org.jmad.modelpack.service.JMadModelPackageRepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

public class ConcurrentModelPackageRepositoryManager
        implements JMadModelPackageRepositoryManager, JMadModelPackageRepositoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentModelPackageRepositoryManager.class);

    private final Map<JMadModelPackageRepository, EnableState> repositories = new ConcurrentHashMap<>();

    private final ReplayProcessor<Map<JMadModelPackageRepository, EnableState>> states = ReplayProcessor
            .cacheLastOrDefault(emptyMap());

    @Override
    public void remove(JMadModelPackageRepository repository) {
        repositories.remove(repository);
        LOGGER.info("Removed repository {}.", repository);
        publishState();
    }

    @Override
    public void enable(JMadModelPackageRepository repository) {
        repositories.put(repository, EnableState.ENABLED);
        LOGGER.info("Enabled repository {}.", repository);
        publishState();
    }

    @Override
    public void disable(JMadModelPackageRepository repository) {
        repositories.put(repository, EnableState.DISABLED);
        LOGGER.info("Disabled repository {}.", repository);
        publishState();
    }

    @Override
    public Flux<JMadModelPackageRepository> enabledRepositories() {
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
    public Flux<Map<JMadModelPackageRepository, EnableState>> state() {
        return this.states;
    }

}
