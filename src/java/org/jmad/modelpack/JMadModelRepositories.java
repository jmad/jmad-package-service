/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack;

import org.jmad.modelpack.domain.ModelPackageRepository;
import org.jmad.modelpack.service.internal.domain.InternalRepository;

public class JMadModelRepositories {

    public static final String GITLAB_API_V4 = "gitlab-api-v4";
    private static final String CERN_GITLAB = "https://gitlab.cern.ch";

    public static final ModelPackageRepository cernGitlabTesting() {
        return cernGitlabGroup("jmad-modelpacks-testing");
    }

    public static final ModelPackageRepository cernGitlabPro() {
        return cernGitlabGroup("jmad-modelpacks-cern");
    }

    public static final InternalRepository internal() {
        return InternalRepository.INTERNAL;
    }

    private static ModelPackageRepository cernGitlabGroup(String groupName) {
        return new ModelPackageRepository(CERN_GITLAB, groupName, GITLAB_API_V4);
    }

}
