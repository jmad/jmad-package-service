/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.gitlab;

import org.jmad.modpack.domain.ModelPackageRepository;

public class GitlabModelPackageRepository implements ModelPackageRepository {

    private final String baseUrl;
    private final String groupName;

    public GitlabModelPackageRepository(String baseUrl, String groupName) {
        this.baseUrl = baseUrl;
        this.groupName = groupName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseUrl() == null) ? 0 : baseUrl().hashCode());
        result = prime * result + ((groupName() == null) ? 0 : groupName().hashCode());
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
        GitlabModelPackageRepository other = (GitlabModelPackageRepository) obj;
        if (baseUrl() == null) {
            if (other.baseUrl() != null) {
                return false;
            }
        } else if (!baseUrl().equals(other.baseUrl())) {
            return false;
        }
        if (groupName() == null) {
            if (other.groupName() != null) {
                return false;
            }
        } else if (!groupName().equals(other.groupName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GitlabModelPackageRepository [baseUrl=" + baseUrl() + ", groupName=" + groupName() + "]";
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String groupName() {
        return groupName;
    }

}
