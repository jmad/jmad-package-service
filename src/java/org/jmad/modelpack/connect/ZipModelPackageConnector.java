/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.springframework.core.io.Resource;

import reactor.core.publisher.Mono;

public interface ZipModelPackageConnector extends ModelPackageConnector {

    Mono<Resource> zipResourceFor(ModelPackageVariant modelPackage);
    
}
