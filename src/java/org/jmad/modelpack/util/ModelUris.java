package org.jmad.modelpack.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import cern.accsoft.steering.jmad.domain.machine.RangeDefinition;
import cern.accsoft.steering.jmad.domain.machine.SequenceDefinition;
import cern.accsoft.steering.jmad.model.JMadModel;
import cern.accsoft.steering.jmad.model.JMadModelStartupConfiguration;
import cern.accsoft.steering.jmad.modeldefs.domain.JMadModelDefinition;
import cern.accsoft.steering.jmad.modeldefs.domain.OpticsDefinition;

public final class ModelUris {

    private static final String SEQUENCE_URI_PARAMETER = "seq";
    private static final String OPTIC_URI_PARAMETER = "optic";

    private ModelUris() {
        throw new UnsupportedOperationException("static only");
    }

    public static URI modelUri(JMadModel model) {
        RangeDefinition rangeDefinition = model.getActiveRangeDefinition();
        OpticsDefinition opticsDefinition = model.getActiveOpticsDefinition();
        JMadModelStartupConfiguration startupConfiguration = new JMadModelStartupConfiguration();
        startupConfiguration.setLoadDefaultRange(false);
        startupConfiguration.setInitialOpticsDefinition(opticsDefinition);
        startupConfiguration.setInitialRangeDefinition(rangeDefinition);
        return modelDefinitionUri(model.getModelDefinition(), startupConfiguration);
    }

    public static URI modelDefinitionUri(JMadModelDefinition modelDefinition,
            JMadModelStartupConfiguration startupConfiguration) {
        requireNonNull(modelDefinition, "model definition must not be null");
        String modelPackUri = modelDefinition.getModelPackUri();
        checkState(modelPackUri != null,
                "model pack URI of model definition [" + modelDefinition.getName() + "] is not set. "
                        + "Perhaps this model definition was not load from a model pack?");
        URI baseUri = URI.create(modelPackUri);
        String localPart = buildUriLocalPart(modelDefinition.getName(), startupConfiguration);
        try {
            return new URI(baseUri.getScheme(), baseUri.getAuthority(), baseUri.getPath(), baseUri.getQuery(),
                    localPart);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Error building Model Pack URI", e);
        }
    }

    public static URI modelDefinitionUri(JMadModelDefinition modelDefinition) {
        return modelDefinitionUri(modelDefinition, null);
    }

    private static String buildUriLocalPart(String modelName, JMadModelStartupConfiguration startupConfiguration) {
        String localPart = encodeUriComponent(modelName);
        if (startupConfiguration == null) {
            return localPart;
        }
        RangeDefinition initialRangeDefinition = startupConfiguration.getInitialRangeDefinition();
        if (initialRangeDefinition != null) {
            localPart += uriParameter(SEQUENCE_URI_PARAMETER) + encodeUriComponent(
                    initialRangeDefinition.getSequenceDefinition().getName());
        }
        OpticsDefinition opticsDefinition = startupConfiguration.getInitialOpticsDefinition();
        if (opticsDefinition != null) {
            localPart += uriParameter(OPTIC_URI_PARAMETER) + encodeUriComponent(opticsDefinition.getName());
        }

        return localPart;
    }

    private static String uriParameter(String parameterId) {
        return "&" + parameterId + "=";
    }

    public static JMadModelStartupConfiguration startupConfigurationFromUri(URI uri,
            JMadModelDefinition modelDefinition) {
        String localPart = uri.getFragment();
        checkArgument(!isNullOrEmpty(localPart), "No model information in URI");
        Map<String, String> attributes = Arrays.stream(localPart.split("&")) //
                .map(ModelUris::decodeUriComponent) //
                .skip(1).map(s -> s.split("=")) //
                .collect(toMap(s -> s[0], s -> s[1]));
        JMadModelStartupConfiguration startupConfiguration = new JMadModelStartupConfiguration();

        if (attributes.containsKey(SEQUENCE_URI_PARAMETER)) {
            startupConfiguration.setLoadDefaultRange(false);
            String sequenceName = attributes.get(SEQUENCE_URI_PARAMETER);
            SequenceDefinition sequenceDefinition = modelDefinition.getSequenceDefinition(sequenceName);
            if (sequenceDefinition == null) {
                throw new NoSuchElementException(
                        "No sequence '" + sequenceName + "' defined in model " + modelDefinition.getName());
            }
            startupConfiguration.setInitialRangeDefinition(sequenceDefinition.getDefaultRangeDefinition());
        }

        if (attributes.containsKey(OPTIC_URI_PARAMETER)) {
            String opticName = attributes.get(OPTIC_URI_PARAMETER);
            OpticsDefinition opticsDefinition = modelDefinition.getOpticsDefinition(opticName);
            if (opticsDefinition == null) {
                throw new NoSuchElementException(
                        "No optic '" + opticName + "' defined in model " + modelDefinition.getName());
            }
            startupConfiguration.setInitialOpticsDefinition(opticsDefinition);
        }

        return startupConfiguration;
    }

    public static JMadModelDefinition findModelDefinitionFromUri(URI uri, Collection<JMadModelDefinition> candidates) {
        String localPart = uri.getFragment();
        checkArgument(!isNullOrEmpty(localPart), "No model information in URI");
        String modelName = decodeUriComponent(localPart.split("&", 2)[0]);
        return candidates.stream().filter(mDef -> mDef.getName().equalsIgnoreCase(modelName)).findFirst().orElseThrow(
                () -> new NoSuchElementException(
                        "No model definition found for '" + modelName + "' in model pack identified by " + uri));
    }

    private static String encodeUriComponent(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String decodeUriComponent(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
