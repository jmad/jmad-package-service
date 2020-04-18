/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab.internals;

import org.jmad.modelpack.connect.gitlab.domain.GitlabModelPackage;
import org.jmad.modelpack.connect.gitlab.domain.GitlabVariant;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.Variant;

import java.util.List;
import java.util.Objects;

public class GitlabProject {

    public String id;
    public String name;
    public String description;
    public List<String> tag_list;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitlabProject that = (GitlabProject) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(tag_list, that.tag_list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, tag_list);
    }

    @Override
    public String toString() {
        return "GitlabProject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tag_list=" + tag_list +
                '}';
    }
}
