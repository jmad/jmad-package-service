/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static java.util.function.Function.identity;

import java.util.Comparator;

import org.jmad.modelpack.service.gitlab.domain.Release;
import org.jmad.modelpack.service.gitlab.domain.Tag;

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
        return Comparator.<Variant, Variant> comparing(identity(), (c1, c2) -> {
            if ((c1 instanceof Release) && !(c2 instanceof Release)) {
                return -1;
            }
            if ((c2 instanceof Release) && !(c1 instanceof Release)) {
                return 1;
            }

            if ((c1 instanceof Tag) && !(c2 instanceof Tag)) {
                return -1;
            }
            if ((c2 instanceof Tag) && !(c1 instanceof Tag)) {
                return 1;
            }
            return 0;
        }).thenComparing(Comparator.comparing(Variant::name).reversed());
    }

}
