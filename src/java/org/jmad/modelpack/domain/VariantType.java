/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

public enum VariantType {
    /* NOTE: The order is relevant here as the comparators compare by ordinal here. */
    BRANCH,
    TAG,
    RELEASE;
}
