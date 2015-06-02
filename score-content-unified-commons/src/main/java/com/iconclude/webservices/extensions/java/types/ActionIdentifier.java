/*
 * Copyright (c) iConclude 2005, 2006
 * All rights reserved.
 */

package com.iconclude.webservices.extensions.java.types;

@Deprecated
public class ActionIdentifier {

    private String actionName;
    private String archiveName;

    public ActionIdentifier() {
    }

    public ActionIdentifier(String actionName, String archiveName) {
        this.actionName = actionName;
        this.archiveName = archiveName;
    }


    /**
     * Gets the actionName value for this ActionIdentifier.
     *
     * @return actionName
     */
    public String getActionName() {
        return actionName;
    }


    /**
     * Sets the actionName value for this ActionIdentifier.
     *
     * @param actionName
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }


    /**
     * Gets the archiveName value for this ActionIdentifier.
     *
     * @return archiveName
     */
    public String getArchiveName() {
        return archiveName;
    }


    /**
     * Sets the archiveName value for this ActionIdentifier.
     *
     * @param archiveName
     */
    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        // object must be ActionIdentifier at this point
        ActionIdentifier ai = (ActionIdentifier)obj;
        return this.archiveName.equals(ai.archiveName) &&
                this.actionName.equals(ai.actionName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == archiveName ? 0 : archiveName.hashCode());
        hash = 31 * hash + (null == actionName ? 0 : actionName.hashCode());
        return hash;
    }

    public String toString(){
        return archiveName+"/"+actionName;
    }
}

