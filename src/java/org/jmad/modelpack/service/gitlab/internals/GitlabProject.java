/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab.internals;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.ModelPackageVariantImpl;
import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.service.gitlab.GitlabModelPackage;
import org.jmad.modelpack.service.gitlab.GitlabModelPackageRepository;

public class GitlabProject {

    public String id;
    public String name;
    public String description;

    @Override
    public String toString() {
        return "GitlabProject [id=" + id + ", name=" + name + ", description=" + description + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        GitlabProject other = (GitlabProject) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public ModelPackageVariant toModelPackage(GitlabModelPackageRepository repo, Variant variant) {
        GitlabModelPackage pkg = new GitlabModelPackage(repo, id, name, description);
        return new ModelPackageVariantImpl(pkg, variant);
    }

}
