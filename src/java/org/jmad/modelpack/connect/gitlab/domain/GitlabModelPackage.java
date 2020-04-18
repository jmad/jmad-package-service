/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab.domain;

import org.jmad.modelpack.connect.gitlab.internals.GitlabProject;
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackage;

import java.net.URI;

public class GitlabModelPackage extends ModelPackage {
    private final String projectId;

    public GitlabModelPackage(GitlabProject project, JMadModelPackageRepository repository) {
        super(project.name, repository, buildGitlabUri(repository, project.name));
        this.projectId = project.id;
    }

    private static URI buildGitlabUri(JMadModelPackageRepository repository, String projectId) {
        return repository.repoUri().resolve(projectId);
    }

    public String name() {
        return name;
    }

    public String id() {
        return projectId;
    }

    public JMadModelPackageRepository repository() {
        return repository;
    }
}
