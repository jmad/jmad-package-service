/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.gitlab.internals;

import org.jmad.modpack.service.gitlab.domain.Commit;

public class GitlabCommit {

    public String id;
    public String author_name;
    public String author_email;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((author_email == null) ? 0 : author_email.hashCode());
        result = prime * result + ((author_name == null) ? 0 : author_name.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        GitlabCommit other = (GitlabCommit) obj;
        if (author_email == null) {
            if (other.author_email != null) {
                return false;
            }
        } else if (!author_email.equals(other.author_email)) {
            return false;
        }
        if (author_name == null) {
            if (other.author_name != null) {
                return false;
            }
        } else if (!author_name.equals(other.author_name)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GitlabCommit [id=" + id + ", author_name=" + author_name + ", author_email=" + author_email + "]";
    }

    public Commit toCommit() {
        return new Commit(id, author_name);
    }

}
