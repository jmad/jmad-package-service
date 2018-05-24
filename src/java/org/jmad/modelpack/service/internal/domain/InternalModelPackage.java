/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.internal.domain;

import org.jmad.modelpack.domain.ModelPackage;

public class InternalModelPackage extends ModelPackage {

    private static final String INTERNAL_STRING = "INTERNAL";
    public static InternalModelPackage INTERNAL = new InternalModelPackage();

    public InternalModelPackage() {
        super(INTERNAL_STRING, InternalRepository.INTERNAL, INTERNAL_STRING, INTERNAL_STRING);
    }

}
