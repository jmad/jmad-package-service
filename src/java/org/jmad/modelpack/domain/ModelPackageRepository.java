/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

public class ModelPackageRepository {

    private final String baseUrl;
    private final String groupName;
    private final String connectorId;

    public ModelPackageRepository(String baseUrl, String groupName, String connectorId) {
        this.baseUrl = baseUrl;
        this.groupName = groupName;
        this.connectorId = connectorId;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String groupName() {
        return groupName;
    }

    public String connectorId() {
        return connectorId;
    }

    @Override
    public String toString() {
        return "ModelPackageRepository [baseUrl=" + baseUrl + ", groupName=" + groupName + ", connectorId="
                + connectorId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseUrl == null) ? 0 : baseUrl.hashCode());
        result = prime * result + ((connectorId == null) ? 0 : connectorId.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
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
        ModelPackageRepository other = (ModelPackageRepository) obj;
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
        if (groupName == null) {
            if (other.groupName != null) {
                return false;
            }
        } else if (!groupName.equals(other.groupName)) {
            return false;
        }
        return true;
    }

}
