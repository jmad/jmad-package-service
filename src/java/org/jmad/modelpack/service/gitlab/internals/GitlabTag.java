/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab.internals;

import org.jmad.modelpack.domain.Variant;

public class GitlabTag {

    public String name;
    public String message;
    public GitlabCommit commit;
    public GitlabRelease release;

    @Override
    public String toString() {
        return "GitlabTag [name=" + name + ", message=" + message + ", commit=" + commit + ", release=" + release + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commit == null) ? 0 : commit.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((release == null) ? 0 : release.hashCode());
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
        GitlabTag other = (GitlabTag) obj;
        if (commit == null) {
            if (other.commit != null) {
                return false;
            }
        } else if (!commit.equals(other.commit)) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (release == null) {
            if (other.release != null) {
                return false;
            }
        } else if (!release.equals(other.release)) {
            return false;
        }
        return true;
    }

    public Variant toTag() {
        if (release == null) {
            return Variant.tag(name, commit.toCommit());
        } else {
            return Variant.release(name, commit.toCommit());
        }
    }

}
