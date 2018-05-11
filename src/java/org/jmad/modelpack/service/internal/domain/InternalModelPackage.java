/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.internal.domain;

import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageRepository;

public enum InternalModelPackage implements ModelPackage {
    INTERNAL;

    @Override
    public ModelPackageRepository sourceRepository() {
        return InternalRepository.INTERNAL;
    }

}
