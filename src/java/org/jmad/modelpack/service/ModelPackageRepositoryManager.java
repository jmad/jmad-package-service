/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service;

import java.util.Map;

import org.jmad.modelpack.domain.ModelPackageRepository;

import reactor.core.publisher.Flux;

public interface ModelPackageRepositoryManager {

    void remove(ModelPackageRepository repository);

    void enable(ModelPackageRepository repository);

    void disable(ModelPackageRepository repository);

    Flux<Map<ModelPackageRepository, EnableState>> state();
    
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
