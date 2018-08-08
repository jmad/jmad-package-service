/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import java.util.Comparator;
import java.util.Objects;

import org.jmad.modelpack.util.VersionStrings;

/**
 * Utility methods for jmad model packages
 * 
 * @author kfuchsbe
 */
public final class ModelPackages {

    private static final Comparator<Variant> LATEST_RELEASE_LAST_COMPARATOR = (Variant v1, Variant v2) -> {

        if (Objects.equals(v1, v2)) {
            return 0;
        } else if (Objects.isNull(v1)) {
            return -1;
        } else if (Objects.isNull(v2)) {
            return 1;
        }

        int variantTypeOrder = v1.type().ordinal() - v2.type().ordinal();
        if (variantTypeOrder != 0) {
            return variantTypeOrder;
        }

        if (isRelease(v1)) {
            return VersionStrings.versionComparator().compare(v1.name(), v2.name());
        } else {
            return v1.name().compareToIgnoreCase(v2.name());
        }
    };

    private static final Comparator<Variant> LATEST_FIRST_VARIANT_COMPARATOR = LATEST_RELEASE_LAST_COMPARATOR
            .reversed();

    private static final Comparator<ModelPackageVariant> PACKAGE_VARIANT_COMPARATOR = Comparator
            .<ModelPackageVariant, String> comparing(ti -> ti.modelPackage().name())
            .thenComparing(ModelPackageVariant::variant, LATEST_FIRST_VARIANT_COMPARATOR);

    private ModelPackages() {
        /* Only static methods */
    }

    public static Comparator<ModelPackageVariant> latestFirstPackageVariantComparator() {
        return PACKAGE_VARIANT_COMPARATOR;
    }

    /**
     * Provides a comparator for {@link Variant}s that sorts according the following:
     * <ol>
     * <li>{@code null} elements</li>
     * <li>branches, internally sorted alphabetically by name, ignoring case</li>
     * <li>tags, internally sorted alphabetically by name, ignoring case</li>
     * <li>releases sorted by version numbers, according to {@link VersionStrings#versionComparator()}</li>
     * </ol>
     * 
     * @return a comparator following the
     */
    public static Comparator<Variant> latestLastVariantComparator() {
        return LATEST_RELEASE_LAST_COMPARATOR;
    }

    private static boolean isRelease(Variant v1) {
        return VariantType.RELEASE == v1.type();
    }

}
