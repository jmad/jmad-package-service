/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service;

import org.jmad.modelpack.domain.JMadModelPackageRepository;

import reactor.core.publisher.Flux;

public interface JMadModelPackageRepositoryProvider {

    Flux<JMadModelPackageRepository> enabledRepositories();

}
