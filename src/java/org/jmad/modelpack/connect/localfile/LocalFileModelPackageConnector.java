package org.jmad.modelpack.connect.localfile;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.service.JMadService;
import org.jmad.modelpack.connect.ConnectorIds;
import org.jmad.modelpack.connect.InternalModelPackageConnector;
import org.jmad.modelpack.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.io.File;

import static java.util.Arrays.stream;

public class LocalFileModelPackageConnector implements InternalModelPackageConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileModelPackageConnector.class);
    private static final Variant LOCAL_VARIANT = Variant.release("LOCAL", new Commit("LOCAL", "LOCAL"));
    public static final String LOCAL_NAME_PREFIX = "LOCAL-";

    @Autowired
    JMadService jMadService;

    @Override
    public Flux<JMadModelDefinition> modelDefinitionsFor(ModelPackageVariant modelPackage) {
        if (!canHandle(modelPackage.modelPackage().repository())) {
            return Flux.empty();
        }
        String pathToModelPack = modelPackage.modelPackage().repository().baseUrl()
                + File.separator + modelPackage.modelPackage().id();
        try {
            File modelPackDir = new File(pathToModelPack);
            return Flux.fromIterable(jMadService.getModelDefinitionImporter().importModelDefinitions(modelPackDir));
        } catch (Exception e) {
            LOGGER.error("Error loading LOCAL modelpack from '{}'", pathToModelPack, e);
            return Flux.error(e);
        }
    }

    @Override
    public Flux<ModelPackageVariant> availablePackages(JMadModelPackageRepository repository) {
        if (!canHandle(repository)) {
            return Flux.empty();
        }
        try {
            File dir = new File(repository.baseUrl());
            LOGGER.info("Searching for local models in {}", dir);
            if (!dir.isDirectory()) {
                return Flux.error(new IllegalArgumentException(dir.getAbsolutePath() + " is not a directory."));
            }
            return Flux.fromStream( //
                    stream(dir.listFiles(File::isDirectory)) //
                            .map(File::getName) //
                            .map(n -> modelPackage(n, repository)));
        } catch (Exception e) {
            LOGGER.error("Error loading LOCAL repository '{}'", repository.baseUrl(), e);
            return Flux.error(e);
        }
    }

    private ModelPackageVariant modelPackage(String name, JMadModelPackageRepository repository) {
        ModelPackage modelPackage = new ModelPackage(LOCAL_NAME_PREFIX +name, repository, name, "Local ModelPack: " + name);
        return new ModelPackageVariant(modelPackage, LOCAL_VARIANT);
    }

    @Override
    public String connectorId() {
        return ConnectorIds.LOCAL_FILE_CONNECTOR_ID;
    }

}
