/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmad.modelpack.domain.ModelPackages.variantComparator;
import static org.jmad.modelpack.domain.VariantType.RELEASE;
import static org.jmad.modelpack.domain.VariantType.TAG;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class ModelPackagesTest {

    private static final Commit ANY_COMMIT = Mockito.mock(Commit.class);

    @Test
    public void releaseIsFirst() {
        Variant release = new Variant("", ANY_COMMIT, RELEASE);
        Variant tag = new Variant("", ANY_COMMIT, TAG);

        assertThat(variantComparator().compare(release, tag)).isEqualTo(-1);
    }

    @Test
    public void releaseSortingIsCorrect() {
        Variant release2018_1 = release("v2018.1");
        Variant release2018_2 = release("v2018.2");
        Variant release2018_10 = release("v2018.10");

        assertThat(variantComparator().compare(release2018_1, release2018_2)).isEqualTo(1);
        assertThat(variantComparator().compare(release2018_2, release2018_10)).isEqualTo(1);
        assertThat(variantComparator().compare(release2018_2, release2018_1)).isEqualTo(-1);
        assertThat(variantComparator().compare(release2018_10, release2018_2)).isEqualTo(-1);
    }

    @Test
    public void sortingAny() throws Exception {
        assertThat(sort(release("v2018.1"), release("v2018.2"), release("v2018.10")))
                .containsExactly(release("v2018.10"), release("v2018.2"), release("v2018.1"));
    }

    private Variant release(String name) {
        return new Variant(name, ANY_COMMIT, RELEASE);
    }

    private static List<Variant> sort(Variant... strings) {
        List<Variant> list = Arrays.asList(strings);
        Collections.sort(list, ModelPackages.variantComparator());
        return list;
    }

}
