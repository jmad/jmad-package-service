/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmad.modelpack.util.VersionStrings.versionDigits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class VersionStringsTest {

    private static final int SHUFFLES = 50;

    private static final List<String> EXPECTED_SORTED = Arrays.asList(null, null, " ", "A", "b", "1", " 1 . 1 ", "1.2",
            "2.1", "v1", "v2", " v2.1", "v2.1");

    @Test
    public void nameSplittingForParsableString() {
        assertThat(versionDigits("v7.2")).containsExactly(7, 2);
    }

    @Test
    public void singleDigit() {
        assertThat(versionDigits("1")).containsExactly(1);
    }

    @Test
    public void nameNoFirstNumber() {
        assertThat(versionDigits("v.2")).isEmpty();
    }

    @Test
    public void invalidStringContained() {
        assertThat(versionDigits("x1.2")).isEmpty();
    }

    @Test
    public void sortingAllKind() {
        assertThat(sort("1.2", "2.1", "v2.1", "v1", " v2.1", "v2", "b", null, "A", " ", " 1 . 1 ", null, "1"))
                .isEqualTo(EXPECTED_SORTED);
    }

    @Test
    @Parameters(method = "shuffledLists")
    public void randomShuffleSorting(List<String> listToSort) {
        ArrayList<String> copy = new ArrayList<>(listToSort);
        Collections.sort(copy, VersionStrings.versionComparator());
        assertThat(copy).isEqualTo(EXPECTED_SORTED);
    }

    @SuppressWarnings("unused")
    /* this is used! (as parameters of the above method) */
    private Object[] shuffledLists() {
        Object[] params = new Object[SHUFFLES];
        for (int i = 0; i < SHUFFLES; i++) {
            params[i] = new Object[] { newShuffled() };
        }
        return params;
    }

    private List<String> newShuffled() {
        List<String> shuffled = new ArrayList<>(EXPECTED_SORTED);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    private static List<String> sort(String... strings) {
        List<String> list = Arrays.asList(strings);
        Collections.sort(list, VersionStrings.versionComparator());
        return list;
    }

}
