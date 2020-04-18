/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import java.net.URI;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class ModelPackageVariant {

    protected final ModelPackage modelPackage;
    protected final Variant variant;
    protected final URI uri;

    public ModelPackageVariant(URI uri, ModelPackage modelPackage, Variant variant) {
        this.uri = requireNonNull(uri, "URI must not be null");
        this.modelPackage = requireNonNull(modelPackage, "modelPackage must not be null");
        this.variant = requireNonNull(variant, "variant must not be null");
    }

    public ModelPackage modelPackage() {
        return this.modelPackage;
    }

    public Variant variant() {
        return this.variant;
    }

    public String fullName() {
        return modelPackage.name() + "-" + variant.fullName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelPackageVariant that = (ModelPackageVariant) o;
        return Objects.equals(modelPackage, that.modelPackage) &&
                Objects.equals(variant, that.variant) &&
                Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelPackage, variant, uri);
    }

    @Override
    public String toString() {
        return "ModelPackageVariant{" +
                "modelPackage=" + modelPackage +
                ", variant=" + variant +
                ", uri=" + uri +
                '}';
    }
}
