/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab.internals;

import org.jmad.modelpack.domain.Variant;

public class GitlabBranch {
    public String name;
    public GitlabCommit commit;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commit == null) ? 0 : commit.hashCode());
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
        GitlabBranch other = (GitlabBranch) obj;
        if (commit == null) {
            if (other.commit != null) {
                return false;
            }
        } else if (!commit.equals(other.commit)) {
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

    @Override
    public String toString() {
        return "GitlabBranch [name=" + name + ", commit=" + commit + "]";
    }

    public Variant toBranch() {
        return Variant.branch(name, commit.toCommit());
    }

}
