/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab.domain;

import static java.util.Objects.requireNonNull;

public class Commit {

    private final String id;
    private final String author;

    public Commit(String id, String author) {
        this.id = requireNonNull(id, "id must not be null");
        this.author = requireNonNull(author, "author must not be null");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((author == null) ? 0 : author.hashCode());
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
        Commit other = (Commit) obj;
        if (author == null) {
            if (other.author != null) {
                return false;
            }
        } else if (!author.equals(other.author)) {
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
        return "Commit [id=" + id + ", author=" + author + "]";
    }

    public String id() {
        return id;
    }

    public String author() {
        return author;
    }

}
