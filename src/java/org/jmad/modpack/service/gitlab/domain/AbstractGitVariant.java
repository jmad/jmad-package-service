/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.gitlab.domain;

import static java.util.Objects.requireNonNull;

import org.jmad.modpack.domain.AbstractVariant;

public abstract class AbstractGitVariant extends AbstractVariant {

    private final Commit commit;

    public AbstractGitVariant(String name, Commit commit) {
        super(name);
        this.commit = requireNonNull(commit, "commit must not be null");
    }

    public Commit commit() {
        return commit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((commit == null) ? 0 : commit.hashCode());
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
        AbstractGitVariant other = (AbstractGitVariant) obj;
        if (commit == null) {
            if (other.commit != null) {
                return false;
            }
        } else if (!commit.equals(other.commit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [commit=" + commit + ", name()=" + name() + "]";
    }

    @Override
    public final String fullName() {
        return name() + "-" + commit.id();
    }

}
