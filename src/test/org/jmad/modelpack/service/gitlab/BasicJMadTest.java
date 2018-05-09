/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import cern.accsoft.steering.jmad.service.JMadService;
import cern.accsoft.steering.jmad.service.JMadServiceFactory;

public class BasicJMadTest {

    @Test
    public void jmadServiceIsCreatedWithoutErrors() {
        JMadService service = JMadServiceFactory.createJMadService();
        assertThat(service).isNotNull();
    }

}
