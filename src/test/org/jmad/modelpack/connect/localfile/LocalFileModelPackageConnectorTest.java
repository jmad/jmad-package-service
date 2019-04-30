package org.jmad.modelpack.connect.localfile;

import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.io.JMadModelDefinitionImporter;
import cern.accsoft.steering.jmad.service.JMadService;
import org.jmad.modelpack.connect.ConnectorIds;
import org.jmad.modelpack.domain.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalFileModelPackageConnectorTest {

    @Mock
    JMadService jMadService;

    @Mock
    JMadModelDefinitionImporter importer;

    @InjectMocks
    LocalFileModelPackageConnector connector;

    JMadModelPackageRepository repository;
    Path tmpRepoDir;

    @Before
    public void setUp() throws Exception {
        tmpRepoDir = Files.createTempDirectory("test-modelpack");
        Files.createDirectory(modelpackFile("modelpack-1").toPath());
        Files.createDirectory(modelpackFile("test-modelpack").toPath());
        Files.createDirectory(modelpackFile("another-modelpack").toPath());
        repository = new JMadModelPackageRepository(tmpRepoDir.toAbsolutePath().toString(), "TEST", ConnectorIds.LOCAL_FILE_CONNECTOR_ID);
        when(jMadService.getModelDefinitionImporter()).thenReturn(importer);
    }

    @After
    public void cleanUp() throws Exception {
        FileSystemUtils.deleteRecursively(tmpRepoDir);
    }

    private File modelpackFile(String name) {
        String tmpPrefix = tmpRepoDir.toAbsolutePath().toString() + File.separator;
        return new File(tmpPrefix + name);
    }

    @Test
    public void modelDefinitionsFor_unsupportedModelPack_shouldReturnEmpty() {
        JMadModelPackageRepository unsupportedRepo = new JMadModelPackageRepository("foo",
                "TEST", ConnectorIds.INTERNAL_CONNECTOR_ID);
        ModelPackage modelPackage = new ModelPackage("modelpack-1", unsupportedRepo, "modelpack-1", "Test");
        ModelPackageVariant variant = new ModelPackageVariant(modelPackage, Variant.release("LOCAL", new Commit("LOCAL", "LOCAL")));
        List<JMadModelDefinition> modelDefinitions = connector.modelDefinitionsFor(variant).collectList().block();
        verifyZeroInteractions(importer);
        assertThat(modelDefinitions).isEmpty();
    }

    @Test
    public void modelDefinitionsFor_validModelPack_shouldDelegate() {
        ModelPackage modelPackage = new ModelPackage("modelpack-1", repository, "modelpack-1", "Test");
        ModelPackageVariant variant = new ModelPackageVariant(modelPackage, Variant.release("LOCAL", new Commit("LOCAL", "LOCAL")));
        connector.modelDefinitionsFor(variant).blockFirst();
        verify(importer, times(1)).importModelDefinitions(modelpackFile("modelpack-1"));
    }

    @Test
    public void availablePackages_forUnsupportedModelRepo_shouldReturnEmpty() {
        JMadModelPackageRepository repo = new JMadModelPackageRepository("foo",
                "TEST", ConnectorIds.INTERNAL_CONNECTOR_ID);
        List<ModelPackageVariant> allModelPacks = connector.availablePackages(repo).collectList().block();
        assertThat(allModelPacks).isEmpty();
    }

    @Test
    public void availablePackages_forSupportedModelRepoButBadDirectory_shouldReturnError() {
        JMadModelPackageRepository repo = new JMadModelPackageRepository("/i/do/not/exist",
                "TEST", ConnectorIds.LOCAL_FILE_CONNECTOR_ID);
        assertThatExceptionOfType(IllegalArgumentException.class) //
                .isThrownBy(() -> connector.availablePackages(repo).blockFirst()) //
                .withMessageContaining("/i/do/not/exist is not a directory");
    }

    @Test
    public void availablePackages_forSupportedModelRepo_shouldListModelPacks() {
        List<ModelPackageVariant> allModelPacks = connector.availablePackages(repository).collectList().block();
        assertThat(allModelPacks.stream().map(ModelPackageVariant::modelPackage).map(ModelPackage::id))
                .containsExactlyInAnyOrder("modelpack-1", "test-modelpack", "another-modelpack");
        assertThat(allModelPacks.stream().map(ModelPackageVariant::modelPackage).map(ModelPackage::name))
                .containsExactlyInAnyOrder("LOCAL-modelpack-1", "LOCAL-test-modelpack", "LOCAL-another-modelpack");
    }
}