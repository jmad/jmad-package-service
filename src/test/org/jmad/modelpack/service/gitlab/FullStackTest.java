/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab;

import java.util.List;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.conf.JMadModelPackageServiceConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cern.accsoft.steering.jmad.domain.ex.JMadModelException;
import cern.accsoft.steering.jmad.domain.result.tfs.TfsSummary;
import cern.accsoft.steering.jmad.model.JMadModel;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;

@Ignore("Contact to cern gitlab from outside seems not to be possible (yet)")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JMadModelPackageServiceConfiguration.class)
public class FullStackTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullStackTest.class);

    @Autowired
    private JMadService jmadService;

    @Autowired
    private JMadModelPackageService packageService;

    @Test
    public void loadOneModel() throws JMadModelException {

        List<ModelPackageVariant> pkgs = packageService.availablePackages().collectList().block();
        LOGGER.info("Packages found: {}", pkgs);

        // @formatter:off
        List<JMadModelDefinition> definitions = packageService.availablePackages()
                .doOnNext(p -> LOGGER.info("model package found: {}", p))
                .take(1)
                .flatMap(packageService::modelDefinitionsFrom)
                .collectList()
                .block();
        // @formatter:on

        if (definitions.isEmpty()) {
            throw new IllegalStateException("No model packages could be found!");
        }
        JMadModelDefinition def = definitions.get(0);

        LOGGER.info("Opening model from definition: {}.", def);
        JMadModel model = jmadService.createModel(def);

        TfsSummary ts = model.calcTwissSummary();
        LOGGER.info("Twiss summary for model {}: {}.", model, ts);

    }

}
