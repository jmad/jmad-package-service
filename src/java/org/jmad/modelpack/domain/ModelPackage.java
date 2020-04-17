/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Objects;

public class ModelPackage {

    protected final String name;
    protected final JMadModelPackageRepository repository;
    protected final URI uri;

    public ModelPackage(String name, JMadModelPackageRepository repository, URI uri) {
        this.name = requireNonNull(name, "name must not be null");
        this.repository = requireNonNull(repository, "repository must not be null");
        this.uri = requireNonNull(uri, "URI must not be null");
    }

    public String name() {
        return name;
    }

    public URI uri() {
        return uri;
    }

    public JMadModelPackageRepository repository() {
        return repository;
    }

    @Override
    public String toString() {
        return "ModelPackage [uri=" + uri + ", name=" + name + ", repository=" + repository + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelPackage that = (ModelPackage) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
