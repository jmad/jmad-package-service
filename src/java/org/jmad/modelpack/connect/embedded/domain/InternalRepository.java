/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.embedded.domain;

import org.jmad.modelpack.connect.ConnectorIds;
import org.jmad.modelpack.domain.JMadModelPackageRepository;

public class InternalRepository extends JMadModelPackageRepository {
    public static final InternalRepository INTERNAL = new InternalRepository();

    public InternalRepository() {
        super("INTERNAL", "INTERNAL", ConnectorIds.INTERNAL_CONNECTOR_ID);
    }

}
