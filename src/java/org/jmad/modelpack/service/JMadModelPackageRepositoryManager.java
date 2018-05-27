/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service;

import java.util.Map;

import org.jmad.modelpack.domain.JMadModelPackageRepository;

import reactor.core.publisher.Flux;

public interface JMadModelPackageRepositoryManager {

    void remove(JMadModelPackageRepository repository);

    void enable(JMadModelPackageRepository repository);

    void disable(JMadModelPackageRepository repository);

    Flux<Map<JMadModelPackageRepository, EnableState>> state();

    public enum EnableState {
        ENABLED(true),
        DISABLED(false);

        private EnableState(boolean enabled) {
            this.enabled = enabled;
        }

        private final boolean enabled;

        public boolean asBoolEnabled() {
            return enabled;
        }
    }
}
