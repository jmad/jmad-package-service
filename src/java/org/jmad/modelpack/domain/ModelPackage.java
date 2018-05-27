/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

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
        return (JMadModelPackageRepository) sourceRepository();
    }

}
