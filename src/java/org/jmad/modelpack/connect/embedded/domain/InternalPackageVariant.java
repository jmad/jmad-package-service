/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.embedded.domain;

import org.jmad.modelpack.domain.Commit;
import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.domain.VariantType;

public class InternalPackageVariant extends Variant {
    public static final InternalPackageVariant NONE = new InternalPackageVariant();

    public InternalPackageVariant() {
        super("INTERNAL", new Commit("INTERNAL", "INTERNAL"), VariantType.RELEASE);
    }

}
