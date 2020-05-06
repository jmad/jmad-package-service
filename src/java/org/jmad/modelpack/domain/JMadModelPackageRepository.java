/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class JMadModelPackageRepository {

    private final URI repositoryUri;

    private JMadModelPackageRepository(URI uri) {
        this.repositoryUri = requireNonNull(uri, "URI must not be null");
    }

    public static JMadModelPackageRepository fromUri(URI uri) {
        return new JMadModelPackageRepository(normalizedUri(uri.toASCIIString()));
    }

    public static JMadModelPackageRepository fromUri(String uri) {
        return new JMadModelPackageRepository(normalizedUri(uri));
    }

    private static URI normalizedUri(String uriString) {
        try {
            URI uri = new URI(uriString).normalize();
            if (uri.getPath() == null || uri.getPath().isEmpty() || uri.getPath().endsWith("/")) {
                return uri;
            }
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath() + "/",
                    uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public URI uri() {
        return repositoryUri;
    }

    public String connectorScheme() {
        return repositoryUri.getScheme();
    }

    @Override
    public String toString() {
        return repositoryUri.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JMadModelPackageRepository that = (JMadModelPackageRepository) o;
        return Objects.equals(repositoryUri, that.repositoryUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositoryUri);
    }
}
