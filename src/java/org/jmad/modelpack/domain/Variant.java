/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.domain;

/**
 * A variant of a model package. This can e.g. be branches or tags.
 * 
 * @author kfuchsbe
 */
public interface Variant {

    String name();
    
    String fullName();

}
