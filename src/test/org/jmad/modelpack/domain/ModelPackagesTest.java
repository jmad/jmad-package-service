/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static org.jmad.modelpack.domain.ModelPackages.variantComparator;

import org.assertj.core.api.Assertions;
import org.jmad.modelpack.service.gitlab.domain.Commit;
import org.jmad.modelpack.service.gitlab.domain.Release;
import org.jmad.modelpack.service.gitlab.domain.Tag;
import org.junit.Test;
import org.mockito.Mockito;

public class ModelPackagesTest {

    @Test
    public void releaseIsFirst() {
        
        Commit anyCommit = Mockito.mock(Commit.class);
        
        Release release = new Release("", anyCommit);
        Tag tag = new Tag("", anyCommit);
        
        Assertions.assertThat(variantComparator().compare(release, tag)).isEqualTo(-1);
    }

}
