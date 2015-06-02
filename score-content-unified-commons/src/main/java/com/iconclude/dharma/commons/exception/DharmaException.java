/*
 * Created on Jan 30, 2006
 *
 */
package com.iconclude.dharma.commons.exception;

/**
 * @author octavian
 */
public class DharmaException extends RuntimeException {

    private static final long serialVersionUID = -5618616312222865825L;

    private boolean _isUserFriendly = false;

    public DharmaException(Throwable root) {
        super(root);
    }

    public DharmaException(String msg, boolean friendly) {
        super(msg);
        this._isUserFriendly = friendly;
    }

    public DharmaException(String string, Throwable root) {
        super(string, root);
    }

    public DharmaException(String msg, Throwable root, boolean friendly) {
        super(msg, root);
        this._isUserFriendly = friendly;
    }


    public DharmaException(String s) {
        super(s);
    }

    public boolean getIsUserFriendlyMessage() {
        return _isUserFriendly;
    }
}
