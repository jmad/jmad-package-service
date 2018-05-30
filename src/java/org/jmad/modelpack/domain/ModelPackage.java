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
        return sourceRepository();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + ((repository == null) ? 0 : repository.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ModelPackage other = (ModelPackage) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (projectId == null) {
            if (other.projectId != null) {
                return false;
            }
        } else if (!projectId.equals(other.projectId)) {
            return false;
        }
        if (repository == null) {
            if (other.repository != null) {
                return false;
            }
        } else if (!repository.equals(other.repository)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ModelPackage [name=" + name + ", repository=" + repository + ", projectId=" + projectId
                + ", description=" + description + "]";
    }

}
