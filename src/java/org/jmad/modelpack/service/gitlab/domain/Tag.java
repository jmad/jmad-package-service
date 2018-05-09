/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.gitlab.domain;

public class Tag extends AbstractGitVariant {

    public Tag(String name, Commit commit) {
        super(name, commit);
    }

}
