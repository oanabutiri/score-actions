/*
 * Copyright (c) iConclude 2005, 2006
 * All rights reserved.
 */

package com.iconclude.webservices.extensions.java.types;

@Deprecated
public class ActionRequest {

    private String sessionId;
    private ActionIdentifier actionId;
    private Map parameters;

    public ActionRequest() {
    }

    public ActionRequest(String sessionId, ActionIdentifier actionId, Map parameters) {
        this.sessionId = sessionId;
        this.actionId = actionId;
        this.parameters = parameters;
    }

    /**
     * Gets the sessionId value for this ActionRequest.
     *
     * @return sessionId
     */
    public String getSessionId() {
        return sessionId;
    }


    /**
     * Sets the sessionId value for this ActionRequest.
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    /**
     * Gets the actionId value for this ActionRequest.
     *
     * @return actionId
     */
    public ActionIdentifier getActionId() {
        return actionId;
    }


    /**
     * Sets the actionId value for this ActionRequest.
     *
     * @param actionId
     */
    public void setActionId(ActionIdentifier actionId) {
        this.actionId = actionId;
    }


    /**
     * Gets the parameters value for this ActionRequest.
     *
     * @return parameters
     */
    public Map getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters value for this ActionRequest.
     *
     * @param parameters
     */
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

}

