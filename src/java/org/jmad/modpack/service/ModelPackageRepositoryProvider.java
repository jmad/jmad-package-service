/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service;

import org.jmad.modpack.domain.ModelPackageRepository;

import reactor.core.publisher.Flux;

public interface ModelPackageRepositoryProvider {

    Flux<ModelPackageRepository> enabledRepositories();

}
