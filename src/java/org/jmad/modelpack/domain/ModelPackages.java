/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static java.util.Comparator.comparing;

import java.util.Comparator;

import org.jmad.modelpack.util.VersionStrings;

/**
 * Utility methods for jmad model packages
 * 
 * @author kfuchsbe
 */
public final class ModelPackages {

    private static final Comparator<Variant> LATEST_RELEASE_LAST_COMPARATOR = (Variant v1, Variant v2) -> {
        if (isRelease(v1) && isRelease(v2)) {
            return VersionStrings.versionComparator().compare(v1.name(), v2.name());
        }
        return 0;
    };

    private static final Comparator<Variant> VARIANT_COMPARATOR = comparing(Variant::type)
            .thenComparing(LATEST_RELEASE_LAST_COMPARATOR.reversed())
            .thenComparing(comparing(Variant::name).reversed());

    private static final Comparator<ModelPackageVariant> PACKAGE_VARIANT_COMPARATOR = Comparator
            .<ModelPackageVariant, String> comparing(ti -> ti.modelPackage().name())
            .thenComparing(ModelPackageVariant::variant, VARIANT_COMPARATOR);

    private ModelPackages() {
        /* Only static methods */
    }

    public static Comparator<ModelPackageVariant> packageVariantComparator() {
        return PACKAGE_VARIANT_COMPARATOR;
    }

    public static Comparator<Variant> variantComparator() {
        return VARIANT_COMPARATOR;
    }

    private static boolean isRelease(Variant v1) {
        return VariantType.RELEASE == v1.type();
    }

}
