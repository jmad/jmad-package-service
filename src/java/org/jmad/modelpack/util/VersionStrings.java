/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.util;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;

/**
 * Contains utility methods which can be used to treat strings that represent versions.
 * 
 * @author kfuchsbe
 */
public final class VersionStrings {

    private static final String VERSION_PREFIX = "v";
    private static final Comparator<String> NULLS_FIRST_VERSION_COMPARATOR = (String v1, String v2) -> {

        if (Objects.equals(v1, v2)) {
            return 0;
        } else if (Objects.isNull(v1)) {
            return -1;
        } else if (Objects.isNull(v2)) {
            return 1;
        }

        List<Integer> v1digits = versionDigits(v1);
        List<Integer> v2digits = versionDigits(v2);

        if (v1digits.isEmpty() && v2digits.isEmpty()) {
            return v1.compareToIgnoreCase(v2);
        } else if (v1digits.isEmpty()) {
            return -1;
        } else if (v2digits.isEmpty()) {
            return 1;
        }

        if (!hasVersionPrefix(v1) && hasVersionPrefix(v2)) {
            return -1;
        } else if (hasVersionPrefix(v1) && (!hasVersionPrefix(v2))) {
            return 1;
        }

        int smallestSize = Math.min(v1digits.size(), v2digits.size());
        for (int i = 0; i < smallestSize; i++) {
            if (v1digits.get(i) < v2digits.get(i)) {
                return -1;
            } else if (v1digits.get(i) > v2digits.get(i)) {
                return 1;
            }
        }

        if (v2digits.size() > smallestSize) {
            return -1;
        } else if (v1digits.size() > smallestSize) {
            return 1;
        }

        return v1.compareToIgnoreCase(v2);
    };

   
    private static boolean hasVersionPrefix(String v1) {
        return v1.trim().startsWith(VERSION_PREFIX);
    }

    private VersionStrings() {
        /* only static methods */
    }

    /**
     * Returns a comparator to be used for strings that represent software versions. The following format is
     * recommended: 'v10.2.1', whereas the number of digits can vary. The 'v' in the beginning is optional, but
     * recommended. However a version of e.g. 'v1.1' is not considered the same as a version of '1.1'. There must be no
     * space between the 'v' and the first digit. Leading or trailing spaces are ignored, also in between dots. However,
     * spaces anywhere in a version string are strongly discouraged.
     * <p>
     * If used for sorting, then the final order will follow the following rules:
     * <ol>
     * <li>{@code null} values</li>
     * <li>unparsable version numbers (e.g. containing other letters than a leading 'v' or no digits). These are sorted
     * internally by string natural order.</li>
     * <li>version numbers without a leading 'v' before version numbers with a leading 'v'. Both sorted internally
     * ascending from the most significant digit (left) to the least significant. If different numbers of digits are
     * used, the one with less digits are considered as missing digits at the end and ar considered as less then those
     * with digits at the end. For example, the versions 'v1.2', 'v1.1.0', 'v1.1' would be sorted as: [ 'v1.1',
     * 'v1.1.0', 'v1.2']. If the different strings result in the same numerical versions (e.g. 'v1.1' and ' v1.1', then
     * again natural string ordering is used.</li>
     * </ol>
     * 
     * @return a comparator following the described behaviour.
     */
    public static Comparator<String> versionComparator() {
        return NULLS_FIRST_VERSION_COMPARATOR;
    }

    /**
     * Considers strings as version strings and splits them into their digits. The first entry in the list will be the
     * most significant digit, the next one the one less significant digit. Whenever any digit cannot be parsed (the
     * string is invalid), then an empty list is returned. The only letter which is allowed in the input string is a
     * leading {@value #VERSION_PREFIX}. If this is present, it will be ignored by this method. Whitespaces are ignored.
     * 
     * @param versionString the string to be transformed into integer digits
     * @return a list, containing the digits (most significant first), or an empty list, if the given string does not
     *         represent a valid version.
     * @throws NullPointerException in case the given string is {@code null}
     */
    @VisibleForTesting
    static List<Integer> versionDigits(String versionString) {
        requireNonNull(versionString, "versionString must not be null");

        String trimmed = versionString.trim();
        if (hasVersionPrefix(trimmed)) {
            trimmed = trimmed.substring(VERSION_PREFIX.length());
        }
        try {
            return Stream.of(trimmed.split("\\.")).map(String::trim).map(Integer::parseInt).collect(toList());
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

}
