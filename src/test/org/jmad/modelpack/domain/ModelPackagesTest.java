/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmad.modelpack.domain.ModelPackages.latestLastVariantComparator;
import static org.jmad.modelpack.domain.VariantType.BRANCH;
import static org.jmad.modelpack.domain.VariantType.RELEASE;
import static org.jmad.modelpack.domain.VariantType.TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ModelPackagesTest {

    private static final Commit ANY_COMMIT = Mockito.mock(Commit.class);

    private static final List<Variant> SORTED_EXPECTED = Arrays.asList(null, null, /* nulls first */
            /* branches (alphabetically only, no version recognized) */
            branch("A"), branch("v2018.10"), branch("v2018.2"), branch("zzz"),
            /* tags (alphabetically only, no version recognized) */
            tag("aaa"), tag("B"), tag("v2018.10"), tag("v2018.2"),
            /* releases, correct version sorting */
            release("2017.2"), release("v2017.1"), release("v2018.1"), release("v2018.2"), release("v2018.10"));

    private static final int SHUFFLES = 50;

    @Test
    public void releaseIsLast() {
        Variant release = new Variant("", ANY_COMMIT, RELEASE);
        Variant tag = new Variant("", ANY_COMMIT, TAG);

        assertThat(latestLastVariantComparator().compare(release, tag)).isEqualTo(1);
    }

    @Test
    public void releaseSortingIsCorrect() {
        Variant release2018_1 = release("v2018.1");
        Variant release2018_2 = release("v2018.2");
        Variant release2018_10 = release("v2018.10");

        assertThat(latestLastVariantComparator().compare(release2018_1, release2018_2)).isEqualTo(-1);
        assertThat(latestLastVariantComparator().compare(release2018_2, release2018_10)).isEqualTo(-1);
        assertThat(latestLastVariantComparator().compare(release2018_2, release2018_1)).isEqualTo(1);
        assertThat(latestLastVariantComparator().compare(release2018_10, release2018_2)).isEqualTo(1);
    }

    @Test
    public void sortingAny() throws Exception {
        assertThat(sort(release("v2018.1"), release("v2018.2"), release("v2018.10")))
                .containsExactly(release("v2018.1"), release("v2018.2"), release("v2018.10"));
    }

    @Test
    @Parameters(method = "shuffledLists")
    public void randomShuffleSorting(List<Variant> listToSort) {
        ArrayList<Variant> copy = new ArrayList<>(listToSort);
        Collections.sort(copy, ModelPackages.latestLastVariantComparator());
        assertThat(copy).isEqualTo(SORTED_EXPECTED);
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

    private List<Variant> newShuffled() {
        List<Variant> shuffled = new ArrayList<>(SORTED_EXPECTED);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    private static Variant release(String name) {
        return new Variant(name, ANY_COMMIT, RELEASE);
    }

    private static Variant tag(String name) {
        return new Variant(name, ANY_COMMIT, TAG);
    }

    private static Variant branch(String name) {
        return new Variant(name, ANY_COMMIT, BRANCH);
    }

    private static List<Variant> sort(Variant... strings) {
        List<Variant> list = Arrays.asList(strings);
        Collections.sort(list, ModelPackages.latestLastVariantComparator());
        return list;
    }

}
