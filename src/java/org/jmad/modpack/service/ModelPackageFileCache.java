/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service;

import java.io.File;
import java.util.function.Function;

import org.jmad.modpack.domain.ModelPackageVariant;
import org.springframework.core.io.Resource;

import reactor.core.publisher.Mono;

public interface ModelPackageFileCache {

    Mono<File> fileFor(ModelPackageVariant packageVariant,
            Function<ModelPackageVariant, Mono<Resource>> zipFileResourceCallback);
    
}
