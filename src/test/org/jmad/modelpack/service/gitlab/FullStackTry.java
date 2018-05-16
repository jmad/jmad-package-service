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

import com.google.common.collect.Iterables;

import cern.accsoft.steering.jmad.domain.ex.JMadModelException;
import cern.accsoft.steering.jmad.domain.result.tfs.TfsSummary;
import cern.accsoft.steering.jmad.model.JMadModel;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JMadModelPackageServiceConfiguration.class)
@Ignore("Try")
public class FullStackTry {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullStackTry.class);

    @Autowired
    private JMadService jmadService;

    @Autowired
    private JMadModelPackageService packageService;

    @Test
    public void loadOneModel() throws JMadModelException {

        List<ModelPackageVariant> pkgs = packageService.availablePackages().collectList().block();
        System.out.println(pkgs);
        
        // @formatter:off
        List<JMadModelDefinition> definitions = packageService.availablePackages()
                .doOnNext(p -> LOGGER.info("model package found: {}", p))
                .take(1)
                .flatMap(packageService::modelDefinitionsFrom)
                .collectList()
                .block();
        // @formatter:on

        JMadModelDefinition def = Iterables.getFirst(definitions, null);

        LOGGER.info("Opening model from definition: {}.", def);
        JMadModel model = jmadService.createModel(def);
        
        TfsSummary ts = model.calcTwissSummary();
        LOGGER.info("Twiss summary for model {}: {}.", model, ts);

    }

}
