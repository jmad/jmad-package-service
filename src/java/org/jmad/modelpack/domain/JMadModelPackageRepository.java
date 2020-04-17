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

    public JMadModelPackageRepository(URI uri) {
        this.repositoryUri = requireNonNull(uri, "URI must not be null");
    }

    public static JMadModelPackageRepository fromUri(URI uri) {
        return new JMadModelPackageRepository(uri);
    }

    public static JMadModelPackageRepository fromUri(String uri) {
        try {
            return new JMadModelPackageRepository(new URI(uri).normalize());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public URI repoUri() {
        return repositoryUri;
    }

    public String connectorScheme() {
        return repositoryUri.getScheme();
    }

    @Override
    public String toString() {
        return "JMadModelPackageRepository [uri=" + repositoryUri + "]";
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
