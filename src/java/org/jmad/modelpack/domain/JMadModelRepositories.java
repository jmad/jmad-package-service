/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import static org.jmad.modelpack.connect.ConnectorIds.GITLAB_GROUP_API_V4;

import org.jmad.modelpack.connect.embedded.domain.InternalRepository;

public class JMadModelRepositories {

    private static final String CERN_GITLAB = "https://gitlab.cern.ch";

    public static final JMadModelPackageRepository cernGitlabTesting() {
        return cernGitlabGroup("jmad-modelpacks-testing");
    }

    public static final JMadModelPackageRepository cernGitlabPro() {
        return cernGitlabGroup("jmad-modelpacks-cern");
    }

    public static final InternalRepository internal() {
        return InternalRepository.INTERNAL;
    }

    private static JMadModelPackageRepository cernGitlabGroup(String groupName) {
        return new JMadModelPackageRepository(CERN_GITLAB, groupName, GITLAB_GROUP_API_V4);
    }

}
