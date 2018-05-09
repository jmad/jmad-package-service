/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.gitlab.domain;

public class Branch extends AbstractGitVariant {

    public Branch(String name, Commit commit) {
        super(name, commit);
    }

}
