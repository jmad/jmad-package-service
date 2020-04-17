/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect.gitlab;

import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.domain.ModelPackage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class GitlabModelPackage extends ModelPackage {
    protected final String projectId;
    protected final String description;

    public GitlabModelPackage(String name, JMadModelPackageRepository repository, String projectId, String description) {
        super(name, repository, buildGitlabUri(repository, projectId));
        this.projectId = requireNonNull(projectId, "project must not be null");
        this.description = requireNonNull(description, "description must not be null");
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

    public String description() {
        return description;
    }

    public JMadModelPackageRepository repository() {
        return repository;
    }
}
