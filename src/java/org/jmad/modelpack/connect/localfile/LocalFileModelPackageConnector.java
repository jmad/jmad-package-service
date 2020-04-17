package org.jmad.modelpack.connect.localfile;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.io.impl.ModelDefinitionUtil;
import cern.accsoft.steering.jmad.service.JMadService;
import org.jmad.modelpack.connect.ConnectorUriSchemes;
import org.jmad.modelpack.connect.DirectModelPackageConnector;
import org.jmad.modelpack.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static org.jmad.modelpack.connect.ConnectorUriSchemes.LOCAL_FILE_SCHEME;

public class LocalFileModelPackageConnector implements DirectModelPackageConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileModelPackageConnector.class);
    private static final Variant LOCAL_VARIANT = Variant.release("LOCAL", new Commit("LOCAL", "LOCAL"));
    private static final String LOCAL_NAME_PREFIX = "LOCAL-";

    @Autowired
    private JMadService jMadService;

    @Override
    public Flux<JMadModelDefinition> modelDefinitionsFor(ModelPackageVariant modelPackage) {
        if (!canHandle(modelPackage.modelPackage().repository())) {
            return Flux.empty();
        }
        String pathToModelPack = modelPackage.modelPackage().uri().getPath();
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
            File dir = new File(repository.repoUri().getPath());
            LOGGER.info("Searching for local models in {}", dir);
            if (!dir.isDirectory()) {
                return Flux.error(new IllegalArgumentException(dir.getAbsolutePath() + " is not a directory."));
            }
            return Flux.fromStream( //
                    stream(requireNonNull(dir.listFiles(File::isDirectory))) //
                            .filter(sub -> !ModelDefinitionUtil.modelDefinitionFilesBelow(sub.toPath()).isEmpty())
                            .map(File::getName) //
                            .map(n -> modelPackage(n, repository)));
        } catch (Exception e) {
            LOGGER.error("Error loading LOCAL repository '{}'", repository.repoUri(), e);
            return Flux.error(e);
        }
    }

    @Override
    public Set<String> handledSchemes() {
        return Collections.singleton(LOCAL_FILE_SCHEME);
    }

    private ModelPackageVariant modelPackage(String name, JMadModelPackageRepository repository) {
        URI modelPackUri = repository.repoUri().resolve(name);
        ModelPackage modelPackage = new ModelPackage(LOCAL_NAME_PREFIX + name, repository, modelPackUri);
        return new ModelPackageVariant(modelPackage, LOCAL_VARIANT);
    }

}
