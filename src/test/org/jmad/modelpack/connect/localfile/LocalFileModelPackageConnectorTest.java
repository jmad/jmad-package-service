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
import java.io.IOException;
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
        createModelpack("modelpack-1");
        createModelpack("test-modelpack");
        repository = new JMadModelPackageRepository(tmpRepoDir.toAbsolutePath().toString(), "TEST", ConnectorIds.LOCAL_FILE_CONNECTOR_ID);
        when(jMadService.getModelDefinitionImporter()).thenReturn(importer);
    }

    private void createModelpack(String s) throws IOException {
        Path dir = modelpackPath(s);
        Files.createDirectory(dir);
        File xmlFile = new File(dir.toAbsolutePath() + File.separator + "model.jmd.xml");
        xmlFile.createNewFile();
    }

    @After
    public void cleanUp() throws Exception {
        FileSystemUtils.deleteRecursively(tmpRepoDir);
    }

    private Path modelpackPath(String name) {
        String tmpPrefix = tmpRepoDir.toAbsolutePath().toString() + File.separator;
        return new File(tmpPrefix + name).toPath();
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
        verify(importer, times(1)).importModelDefinitions(modelpackPath("modelpack-1").toFile());
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
    public void availablePackages_forSupportedModelRepo_shouldListModelPacks() throws Exception {
        List<ModelPackageVariant> allModelPacks = connector.availablePackages(repository).collectList().block();
        assertThat(allModelPacks.stream().map(ModelPackageVariant::modelPackage).map(ModelPackage::id))
                .containsExactlyInAnyOrder("modelpack-1", "test-modelpack");
        assertThat(allModelPacks.stream().map(ModelPackageVariant::modelPackage).map(ModelPackage::name))
                .containsExactlyInAnyOrder("LOCAL-modelpack-1", "LOCAL-test-modelpack");
    }

    @Test
    public void availablePackages_forSupportedRepoWithSomeInvalidSubDirs_shouldFilterOutFakeDirs() throws Exception {
        Files.createDirectory(modelpackPath("NOT-A-modelpack"));
        Files.createDirectory(modelpackPath("YET-another-FAKE-modelpack"));
        List<ModelPackageVariant> allModelPacks = connector.availablePackages(repository).collectList().block();
        assertThat(allModelPacks.stream().map(ModelPackageVariant::modelPackage).map(ModelPackage::id))
                .doesNotContain("NOT-A-modelpack", "YET-another-FAKE-modelpack");
        assertThat(allModelPacks.stream().map(ModelPackageVariant::modelPackage).map(ModelPackage::name))
                .doesNotContain("LOCAL-NOT-A-modelpack", "LOCAL-YET-another-FAKE-modelpack");
    }
}