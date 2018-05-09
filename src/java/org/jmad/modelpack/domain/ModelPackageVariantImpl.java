/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static java.util.Objects.requireNonNull;

public class ModelPackageVariantImpl implements ModelPackageVariant {

    private final ModelPackage modelPackage;
    private final Variant variant;

    public ModelPackageVariantImpl(ModelPackage modelPackage, Variant variant) {
        this.modelPackage = requireNonNull(modelPackage, "modelPackage must not be null");
        this.variant = requireNonNull(variant, "variant must not be null");
    }

    @Override
    public ModelPackage modelPackage() {
        return this.modelPackage;
    }

    @Override
    public Variant variant() {
        return this.variant;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modelPackage == null) ? 0 : modelPackage.hashCode());
        result = prime * result + ((variant == null) ? 0 : variant.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ModelPackageVariantImpl other = (ModelPackageVariantImpl) obj;
        if (modelPackage == null) {
            if (other.modelPackage != null) {
                return false;
            }
        } else if (!modelPackage.equals(other.modelPackage)) {
            return false;
        }
        if (variant == null) {
            if (other.variant != null) {
                return false;
            }
        } else if (!variant.equals(other.variant)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ModelPackageVariantImpl [modelPackage=" + modelPackage + ", variant=" + variant + "]";
    }

    @Override
    public String fullName() {
        return modelPackage.name() + "-" + variant.fullName();
    }

}
