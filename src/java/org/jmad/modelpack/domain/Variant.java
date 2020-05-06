/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A variant of a model package. This can e.g. be branches or tags.
 * 
 * @author kfuchsbe
 */
public class Variant {
    protected final String name;
    protected final VariantType type;

    public Variant(String name, VariantType type) {
        this.name = requireNonNull(name, "name must not be null");
        this.type = requireNonNull(type, "type must not be null");
    }

    public String fullName() {
        return name();
    }

    public String name() {
        return this.name;
    }

    public VariantType type() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variant variant = (Variant) o;
        return Objects.equals(name, variant.name) &&
                type == variant.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "Variant{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
