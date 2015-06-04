/*
 * Copyright (c) iConclude 2004-2006
 * All rights reserved.
 */
package io.cloudslang.content.rft.ssh;

/**
 * @author octavian
 */
public class SSHOperationResult {
    private int codeInt = 0;
    private String error = ""; //$NON-NLS-1$
    private String output = ""; //$NON-NLS-1$
    private String exception = ""; //$NON-NLS-1$
    private boolean timedOut = false;

    public int getCodeInt() {
        return codeInt;
    }

    public void setCodeInt(int code) {
        codeInt = code;
    }

    public void setOutput(String out) {
        output = out;
    }

    public String getError() {
        return error;
    }

    public void setError(String err) {
        error = err;
    }

    public String getException() {
        return exception;
    }

    public void setException(String except) {
        exception = except;
    }

    public boolean isTimedOut() {
        return timedOut;
    }
}
