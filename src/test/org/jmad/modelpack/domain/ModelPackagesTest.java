/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static org.jmad.modelpack.domain.ModelPackages.variantComparator;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

public class ModelPackagesTest {

    @Test
    public void releaseIsFirst() {
        Commit anyCommit = Mockito.mock(Commit.class);

        Variant release = new Variant("", anyCommit, VariantType.RELEASE);
        Variant tag = new Variant("", anyCommit, VariantType.TAG);

        Assertions.assertThat(variantComparator().compare(release, tag)).isEqualTo(-1);
    }

}
