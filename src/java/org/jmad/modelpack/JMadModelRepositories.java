/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack;

import org.jmad.modelpack.service.gitlab.GitlabModelPackageRepository;

public class JMadModelRepositories {

    private static final String CERN_GITLAB = "https://gitlab.cern.ch";

    public static final GitlabModelPackageRepository cernGitlabTesting() {
        return cernGitlabGroup("jmad-models-cern-testing");
    }

    public static final GitlabModelPackageRepository cernGitlabPro() {
        return cernGitlabGroup("jmad-models-cern");
    }

    private static GitlabModelPackageRepository cernGitlabGroup(String groupName) {
        return new GitlabModelPackageRepository(CERN_GITLAB, groupName);
    }
}
