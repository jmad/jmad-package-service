/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modpack.service.gitlab.domain;

public class Release extends Tag{

    public Release(String name, Commit commit) {
        super(name, commit);
    }

}
