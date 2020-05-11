package org.jmad.modelpack.util;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.model.JMadModelStartupConfiguration;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

public class ModelUrisTest {

    @Test
    public void modelDefinitionUri_onlyModelDefinitionProvided_shouldGenerateUri() {
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class);
        when(modelDefinition.getName()).thenReturn("Test Model");
        when(modelDefinition.getModelPackUri()).thenReturn("test://model/pack@version");
        assertThat(ModelUris.modelDefinitionUri(modelDefinition).toString())
                .isEqualTo("test://model/pack@version#Test+Model");
    }

    @Test
    public void modelDefinitionUri_modelDefinitionAndFullStartupConfigProvided_shouldGenerateUri() {
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class);
        when(modelDefinition.getName()).thenReturn("Test_Model");
        when(modelDefinition.getModelPackUri()).thenReturn("test://model/pack@version");
        JMadModelStartupConfiguration startupConfiguration = mock(JMadModelStartupConfiguration.class,
                RETURNS_DEEP_STUBS);
        when(startupConfiguration.getInitialOpticsDefinition().getName()).thenReturn("R2020_TestOptic");
        when(startupConfiguration.isLoadDefaultRange()).thenReturn(false);
        when(startupConfiguration.getInitialRangeDefinition().getSequenceDefinition().getName()).thenReturn("testb1");
        assertThat(ModelUris.modelDefinitionUri(modelDefinition, startupConfiguration).toString())
                .isEqualTo("test://model/pack@version#Test_Model&seq=testb1&optic=R2020_TestOptic");
    }

    @Test
    public void modelDefinitionUri_modelDefinitionAndPartialStartupConfigProvided_shouldGenerateUri() {
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class);
        when(modelDefinition.getName()).thenReturn("Test_Model");
        when(modelDefinition.getModelPackUri()).thenReturn("test://model/pack@version");
        JMadModelStartupConfiguration startupConfiguration = mock(JMadModelStartupConfiguration.class,
                RETURNS_DEEP_STUBS);
        when(startupConfiguration.getInitialOpticsDefinition().getName()).thenReturn("Optic Test");
        when(startupConfiguration.getInitialRangeDefinition()).thenReturn(null);
        when(startupConfiguration.isLoadDefaultRange()).thenReturn(true);
        assertThat(ModelUris.modelDefinitionUri(modelDefinition, startupConfiguration).toString())
                .isEqualTo("test://model/pack@version#Test_Model&optic=Optic+Test");
    }

    @Test
    public void modelDefinitionUri_modelDefinitionWithoutModelPackUriProvided_shouldThrow() {
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class);
        when(modelDefinition.getModelPackUri()).thenReturn(null);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> ModelUris.modelDefinitionUri(modelDefinition))
                .withMessageContaining("model pack URI");
    }

    @Test
    public void startupConfigurationFromUri_fullUriProvided_shouldReturn() {
        URI uri = URI.create("test://model/pack@version#Test_Model&seq=testb1&optic=R2020+TestOptic");
        OpticsDefinition opticsDefinition = mock(OpticsDefinition.class);
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class, RETURNS_DEEP_STUBS);
        when(modelDefinition.getOpticsDefinition("R2020 TestOptic")).thenReturn(opticsDefinition);
        RangeDefinition rangeDefinition = mock(RangeDefinition.class);
        when(modelDefinition.getSequenceDefinition("testb1").getDefaultRangeDefinition()).thenReturn(rangeDefinition);
        JMadModelStartupConfiguration startupConfiguration = ModelUris
                .startupConfigurationFromUri(uri, modelDefinition);
        assertThat(startupConfiguration.getInitialOpticsDefinition()).isSameAs(opticsDefinition);
        assertThat(startupConfiguration.getInitialRangeDefinition()).isSameAs(rangeDefinition);
        assertThat(startupConfiguration.isLoadDefaultRange()).isFalse();
    }

    @Test
    public void startupConfigurationFromUri_partialUriProvided_shouldReturn() {
        URI uri = URI.create("test://model/pack@version#Test_Model&range=testb1&optic=R2020+TestOptic");
        OpticsDefinition opticsDefinition = mock(OpticsDefinition.class);
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class);
        when(modelDefinition.getOpticsDefinition("R2020 TestOptic")).thenReturn(opticsDefinition);
        JMadModelStartupConfiguration startupConfiguration = ModelUris
                .startupConfigurationFromUri(uri, modelDefinition);
        assertThat(startupConfiguration.getInitialOpticsDefinition()).isSameAs(opticsDefinition);
        assertThat(startupConfiguration.getInitialRangeDefinition()).isNull();
        assertThat(startupConfiguration.isLoadDefaultRange()).isTrue();
    }

    @Test
    public void startupConfigurationFromUri_uriWithInvalidOpticNameProvided_shouldThrow() {
        URI uri = URI.create("test://model/pack@version#Test_Model&seq=testb1&optic=R2020+TestOptic");
        OpticsDefinition opticsDefinition = mock(OpticsDefinition.class);
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class, RETURNS_DEEP_STUBS);
        when(modelDefinition.getOpticsDefinition(any())).thenReturn(null);
        RangeDefinition rangeDefinition = mock(RangeDefinition.class);
        when(modelDefinition.getSequenceDefinition("testb1").getDefaultRangeDefinition()).thenReturn(rangeDefinition);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> ModelUris.startupConfigurationFromUri(uri, modelDefinition))
                .withMessageContaining("No optic");
    }

    @Test
    public void startupConfigurationFromUri_uriWithInvalidSequenceNameProvided_shouldThrow() {
        URI uri = URI.create("test://model/pack@version#Test_Model&seq=fail&optic=TestOptic");
        OpticsDefinition opticsDefinition = mock(OpticsDefinition.class);
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class, RETURNS_DEEP_STUBS);
        when(modelDefinition.getOpticsDefinition("TestOptic")).thenReturn(opticsDefinition);
        when(modelDefinition.getSequenceDefinition(any())).thenReturn(null);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> ModelUris.startupConfigurationFromUri(uri, modelDefinition))
                .withMessageContaining("No sequence");
    }

    @Test
    public void startupConfigurationFromUri_uriWithNoModelInformationProvided_shouldThrow() {
        URI uri = URI.create("test://model/pack@version");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ModelUris.startupConfigurationFromUri(uri, mock(JMadModelDefinition.class)));
    }

    @Test
    public void findModelDefinitionFromUri_fullUriProvided_shouldReturn() {
        JMadModelDefinition correctModelDefinition = mockModelDefinitionOfName("Test Model");
        Set<JMadModelDefinition> modelDefinitionCandidates = ImmutableSet.of(//
                mockModelDefinitionOfName("Test"), //
                mockModelDefinitionOfName("Test_Model"), //
                correctModelDefinition //
        );
        URI uri = URI.create("test://model/pack@version#Test+Model&seq=testb1&optic=TestOptic");
        assertThat(ModelUris.findModelDefinitionFromUri(uri, modelDefinitionCandidates))
                .isSameAs(correctModelDefinition);
    }

    @Test
    public void findModelDefinitionFromUri_partialUriProvided_shouldReturn() {
        JMadModelDefinition correctModelDefinition = mockModelDefinitionOfName("Test Model");
        Set<JMadModelDefinition> modelDefinitionCandidates = ImmutableSet.of(//
                mockModelDefinitionOfName("Test"), //
                mockModelDefinitionOfName("Test_Model"), //
                correctModelDefinition //
        );
        URI uri = URI.create("test://model/pack@version#Test+Model");
        assertThat(ModelUris.findModelDefinitionFromUri(uri, modelDefinitionCandidates))
                .isSameAs(correctModelDefinition);
    }

    @Test
    public void findModelDefinitionFromUri_uriWithNoModelInformationProvided_shouldThrow() {
        URI uri = URI.create("test://model/pack@version");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ModelUris.findModelDefinitionFromUri(uri, emptyList()));
    }

    @Test
    public void findModelDefinitionFromUri_uriWithInvalidModelNameProvided_shouldThrow() {
        Set<JMadModelDefinition> modelDefinitionCandidates = ImmutableSet.of(//
                mockModelDefinitionOfName("Test"), //
                mockModelDefinitionOfName("Test_Model"));
        URI uri = URI.create("test://model/pack@version#ThereIsNoModel");
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> ModelUris.findModelDefinitionFromUri(uri, modelDefinitionCandidates))
                .withMessageContaining("No model definition found");
    }

    private JMadModelDefinition mockModelDefinitionOfName(String name) {
        JMadModelDefinition modelDefinition = mock(JMadModelDefinition.class);
        when(modelDefinition.getName()).thenReturn(name);
        return modelDefinition;
    }
}