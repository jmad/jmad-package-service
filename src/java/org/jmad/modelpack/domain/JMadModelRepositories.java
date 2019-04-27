/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import org.jmad.modelpack.connect.embedded.domain.InternalRepository;

import java.util.Optional;

import static org.jmad.modelpack.connect.ConnectorIds.GITLAB_GROUP_API_V4;
import static org.jmad.modelpack.connect.ConnectorIds.LOCAL_FILE_CONNECTOR_ID;

public class JMadModelRepositories {

    private static final String CERN_GITLAB = "https://gitlab.cern.ch";
    public static final String PROP_LOCAL_MODEL_REPO = "cern.jmad.modelpacks.local";

    public static JMadModelPackageRepository cernGitlabTesting() {
        return cernGitlabGroup("jmad-modelpacks-testing");
    }

    public static JMadModelPackageRepository cernGitlabPro() {
        return cernGitlabGroup("jmad-modelpacks-cern");
    }

    public static InternalRepository internal() {
        return InternalRepository.INTERNAL;
    }

    public static Optional<JMadModelPackageRepository> defaultLocalFileRepository() {
        String localModelRepo = System.getProperty(PROP_LOCAL_MODEL_REPO);
        if (localModelRepo == null) {
            return Optional.empty();
        } else {

            return Optional.of(new JMadModelPackageRepository(localModelRepo, "LOCAL", LOCAL_FILE_CONNECTOR_ID));
        }
    }

    private static JMadModelPackageRepository cernGitlabGroup(String groupName) {
        return new JMadModelPackageRepository(CERN_GITLAB, groupName, GITLAB_GROUP_API_V4);
    }

}
