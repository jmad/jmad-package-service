/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

public enum VariantType {

    /* NOTE: The order is relevant here as the comparators compare by ordinal here. */
    BRANCH,
    TAG,
    RELEASE;

    public String serializedName() {
        return name().toLowerCase();
    }

    public static VariantType fromSerialized(String name) {
        for (VariantType type : VariantType.values()) {
            if (type.serializedName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Name '" + name + "' cannote be converted into a variant type.");
    }

}
