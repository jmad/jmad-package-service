/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static java.util.Comparator.comparing;

import java.util.Comparator;

/**
 * Utility methods for jmad model packages
 * 
 * @author kfuchsbe
 */
public final class ModelPackages {

    private ModelPackages() {
        /* Only static methods */
    }

    public static Comparator<ModelPackageVariant> packageVariantComparator() {
        return Comparator.<ModelPackageVariant, String> comparing(ti -> ti.modelPackage().name())
                .thenComparing(ModelPackageVariant::variant, variantComparator());
    }

    public static Comparator<Variant> variantComparator() {
        return comparing(Variant::type).thenComparing(comparing(Variant::name).reversed());
    }

}
