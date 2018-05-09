/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.gitlab;

import static java.util.Objects.requireNonNull;

import org.jmad.modpack.domain.AbstractModelPackage;

public class GitlabModelPackage extends AbstractModelPackage {

    private final String gitlabProjectId;
    private final String description;

    public GitlabModelPackage(GitlabModelPackageRepository repo, String gitlabProjectId, String name,
            String description) {
        super(name, repo);
        this.gitlabProjectId = requireNonNull(gitlabProjectId, "gitlabProjectId must not be null");
        this.description = requireNonNull(description, "description must not be null");
    }

    public String id() {
        return gitlabProjectId;
    }

    public String description() {
        return description;
    }

    public GitlabModelPackageRepository repository() {
        return (GitlabModelPackageRepository) sourceRepository();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((gitlabProjectId == null) ? 0 : gitlabProjectId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GitlabModelPackage other = (GitlabModelPackage) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (gitlabProjectId == null) {
            if (other.gitlabProjectId != null) {
                return false;
            }
        } else if (!gitlabProjectId.equals(other.gitlabProjectId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GitlabModelPackage [gitlabProjectId=" + gitlabProjectId + ", description=" + description + "]";
    }

}
