/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.internal.domain;

import org.jmad.modelpack.domain.ModelPackageRepository;

public class InternalRepository extends ModelPackageRepository {
    public static final InternalRepository INTERNAL = new InternalRepository();

    public InternalRepository() {
        super("INTERNAL", "INTERNAL", "internal");
    }

}
