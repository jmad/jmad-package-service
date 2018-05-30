/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class ModelPackage {

    protected final String name;
    protected final JMadModelPackageRepository repository;
    protected final String projectId;
    protected final String description;

    public ModelPackage(String name, JMadModelPackageRepository repository, String projectId, String description) {
        this.name = requireNonNull(name, "name must not be null");
        this.repository = requireNonNull(repository, "repository must not be null");
        this.projectId = requireNonNull(projectId, "project must not be null");
        this.description = requireNonNull(description, "description must not be null");
    }

    public String name() {
        return name;
    }

    public JMadModelPackageRepository sourceRepository() {
        return repository;
    }

    public String id() {
        return projectId;
    }

    public String description() {
        return description;
    }

    public JMadModelPackageRepository repository() {
        return sourceRepository();
    }

    @Override
    public String toString() {
        return "ModelPackage [name=" + name + ", repository=" + repository + ", projectId=" + projectId
                + ", description=" + description + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ModelPackage that = (ModelPackage) o;
        return Objects.equals(name, that.name) && Objects.equals(repository, that.repository)
                && Objects.equals(projectId, that.projectId) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, repository, projectId, description);
    }
}
