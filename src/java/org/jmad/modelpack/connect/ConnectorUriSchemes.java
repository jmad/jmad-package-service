/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.connect;

public final class ConnectorUriSchemes {

    public static final String GITLAB_HTTP_SCHEME = "gitlab+http";
    public static final String GITLAB_HTTPS_SCHEME = "gitlab+https";
    public static final String INTERNAL_SCHEME = "classpath";
    public static final String LOCAL_FILE_SCHEME = "file";

    private ConnectorUriSchemes() {
        throw new UnsupportedOperationException("static only");
    }

}
