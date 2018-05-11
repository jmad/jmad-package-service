/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.internal.domain;

import org.jmad.modelpack.domain.Variant;

public enum InternalPackageVariant implements Variant {
    NONE;

    @Override
    public String fullName() {
        return name();
    }

}