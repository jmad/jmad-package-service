/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

public class JMadModelPackageRepository {

    private final String baseUrl;
    private final String repoName;
    private final String connectorId;

    public JMadModelPackageRepository(String baseUrl, String repoName, String connectorId) {
        this.baseUrl = baseUrl;
        this.repoName = repoName;
        this.connectorId = connectorId;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String repoName() {
        return repoName;
    }

    public String connectorId() {
        return connectorId;
    }

    @Override
    public String toString() {
        return "JMadModelPackageRepository [baseUrl=" + baseUrl + ", repoName=" + repoName + ", connectorId=" + connectorId
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseUrl == null) ? 0 : baseUrl.hashCode());
        result = prime * result + ((connectorId == null) ? 0 : connectorId.hashCode());
        result = prime * result + ((repoName == null) ? 0 : repoName.hashCode());
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
        JMadModelPackageRepository other = (JMadModelPackageRepository) obj;
        if (baseUrl == null) {
            if (other.baseUrl != null) {
                return false;
            }
        } else if (!baseUrl.equals(other.baseUrl)) {
            return false;
        }
        if (connectorId == null) {
            if (other.connectorId != null) {
                return false;
            }
        } else if (!connectorId.equals(other.connectorId)) {
            return false;
        }
        if (repoName == null) {
            if (other.repoName != null) {
                return false;
            }
        } else if (!repoName.equals(other.repoName)) {
            return false;
        }
        return true;
    }

}
