/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.domain;

import static java.util.Objects.requireNonNull;

public abstract class AbstractModelPackage implements ModelPackage {

    private final String name;
    private final ModelPackageRepository repository;

    public AbstractModelPackage(String name, ModelPackageRepository repository) {
        this.name = requireNonNull(name, "name must not be null");
        this.repository = requireNonNull(repository, "repository must not be null");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ModelPackageRepository sourceRepository() {
        return repository;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        AbstractModelPackage other = (AbstractModelPackage) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
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
        return "AbstractModelPackage [name=" + name + ", repository=" + repository + "]";
    }

}
