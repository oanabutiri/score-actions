package com.iconclude.dharma.commons.security;

import java.io.Serializable;


/**
 * @author octavian
 */
public interface IGroup extends Serializable {

    /**
     * @return the name for this group
     */
    String getName();

    /**
     * @return the description (if any) of this group
     */
    String getAnnotation();

    /**
     * @return unique identifier of this group
     */
    String getUuid();

    /**
     * @return true if this is a built-in group. Built-in groups receive special treatment
     */
    boolean isBuiltIn();

    /**
     * @return this group's assigned capabilities (if any)
     */
    public Capabilities getCapabilities();

}
