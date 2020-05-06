/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab.domain;

import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.domain.VariantType;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A variant of a model package. This can e.g. be branches or tags.
 *
 * @author kfuchsbe
 */
public class GitlabVariant extends Variant {

    private final Commit commit;

    public GitlabVariant(String name, Commit commit, VariantType type) {
        super(name, type);
        this.commit = requireNonNull(commit, "commit must not be null");
    }

    public Commit commit() {
        return commit;
    }

    @Override
    public final String fullName() {
        return name() + "-" + commit.id();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public final VariantType type() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GitlabVariant that = (GitlabVariant) o;
        return Objects.equals(commit, that.commit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commit);
    }

    @Override
    public String toString() {
        return "GitlabVariant{" +
                "commit=" + commit +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
