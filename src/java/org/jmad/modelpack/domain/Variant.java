/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static java.util.Objects.requireNonNull;
import static org.jmad.modelpack.domain.VariantType.BRANCH;
import static org.jmad.modelpack.domain.VariantType.RELEASE;
import static org.jmad.modelpack.domain.VariantType.TAG;

/**
 * A variant of a model package. This can e.g. be branches or tags.
 * 
 * @author kfuchsbe
 */
public class Variant {

    private final Commit commit;
    private final String name;
    private final VariantType type;

    public Variant(String name, Commit commit, VariantType type) {
        this.name = requireNonNull(name, "name must not be null");
        this.type = requireNonNull(type, "variantType must not be null");
        this.commit = requireNonNull(commit, "commit must not be null");
    }

    public static Variant tag(String name, Commit commit) {
        return new Variant(name, commit, TAG);
    }

    public static Variant branch(String name, Commit commit) {
        return new Variant(name, commit, BRANCH);
    }

    public static Variant release(String name, Commit commit) {
        return new Variant(name, commit, RELEASE);
    }

    public Commit commit() {
        return commit;
    }

    public final String fullName() {
        return name() + "-" + commit.id();
    }

    public String name() {
        return this.name;
    }

    public final VariantType type() {
        return this.type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commit == null) ? 0 : commit.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Variant other = (Variant) obj;
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
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Variant [commit=" + commit + ", name=" + name + ", type=" + type + "]";
    }

}
