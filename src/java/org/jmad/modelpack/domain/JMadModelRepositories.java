/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

import java.util.Optional;

import static org.jmad.modelpack.connect.ConnectorUriSchemes.GITLAB_HTTPS_SCHEME;
import static org.jmad.modelpack.connect.ConnectorUriSchemes.INTERNAL_SCHEME;
import static org.jmad.modelpack.connect.ConnectorUriSchemes.LOCAL_FILE_SCHEME;

public class JMadModelRepositories {

    private static final String CERN_GITLAB = GITLAB_HTTPS_SCHEME + "://gitlab.cern.ch/";
    private static final String PROP_LOCAL_MODEL_REPO = "cern.jmad.modelpacks.local";

    public static JMadModelPackageRepository cernGitlabTesting() {
        return cernGitlabGroup("jmad-modelpacks-testing");
    }

    public static JMadModelPackageRepository cernGitlabOld() {
        return cernGitlabGroup("jmad-modelpacks-cern");
    }

    public static JMadModelPackageRepository cernGitlabPro() {
        return cernGitlabGroup("acc-models");
    }

    public static JMadModelPackageRepository internal() {
        return JMadModelPackageRepository.fromUri(INTERNAL_SCHEME + ":/");
    }

    public static Optional<JMadModelPackageRepository> defaultLocalFileRepository() {
        String localModelRepo = System.getProperty(PROP_LOCAL_MODEL_REPO);
        if (localModelRepo == null) {
            return Optional.empty();
        } else {
            return Optional.of(JMadModelPackageRepository.fromUri(LOCAL_FILE_SCHEME + ":" + localModelRepo));
        }
    }

    private static JMadModelPackageRepository cernGitlabGroup(String groupName) {
        return JMadModelPackageRepository.fromUri(CERN_GITLAB + groupName);
    }

}
