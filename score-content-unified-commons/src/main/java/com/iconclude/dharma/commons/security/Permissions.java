/*
 * Created on Apr 15, 2005
 *
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.util.Dharma;

import java.io.Serializable;


/**
 * Class representing integer masked permissions.
 *
 * @author octavian
 */
public class Permissions implements Serializable {
    public static final int ALL_PERMS;

    static {
        int res = 0;
        for (PERMISSION perm : PERMISSION.values()) {
            res |= perm._val;
        }
        ALL_PERMS = res;
    }

    ;
    private static final long serialVersionUID = -7772821517884536335L;
    // update these two if the PERMISSION enum is extended
    private final static char[] rwxl = {'r', 'w', 'x', 'l'};
    private final static int[] RWXL = {PERMISSION.READ._val,
            PERMISSION.WRITE._val,
            PERMISSION.EXECUTE._val,
            PERMISSION.LINK._val};
    private int _perms;

    public Permissions(int permission) {
        if (!isValidMask(permission))
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.Permissions.DetailIllegalPermissionError") + Integer.toString(permission)); //$NON-NLS-1$
        _perms = permission;
    }


    public Permissions(PERMISSION... perms) {
        _perms = PERMISSION.NONE._val;

        if (perms != null) {
            for (PERMISSION perm : perms) {
                _perms |= perm.val();
            }
        }
    }

    public static boolean check(int value, int mask) {
        if (value == mask)
            return true;
        if (mask == PERMISSION.NONE._val)
            return (value == PERMISSION.NONE._val);
        return ((value & mask) == mask);
    }

    /**
     * Returns a string representation of the permission set,
     * rwx, substituting a '-' character if a particular permission
     * is not set, eg r-x for read and execute permissions.
     */
    public static String toString(int val) {
        if (val == PERMISSION.NONE._val)
            return "----"; //$NON-NLS-1$

        int mask = val;
        StringBuffer res = new StringBuffer(4);
        int pos = 1;

        while (pos < PERMISSION.values().length) {
            if ((mask & 1) == 1)
                res.append(rwxl[pos - 1]);
            else
                res.append('-');
            ++pos;
            mask >>>= 1;
        }
        return res.toString();
    }

    /**
     * Perses permissions string representation (like --x-) into Permissions instances
     *
     * @param str
     * @return
     */
    public static Permissions valueOf(String str) {
        if (str.length() != rwxl.length)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.Permissions.InvalidRepresentation") + str); //$NON-NLS-1$
        int res = PERMISSION.NONE._val;
        for (int i = 0; i < rwxl.length; ++i) {
            if (str.charAt(i) == '-')
                continue;
            res |= RWXL[i];
        }
        return new Permissions(res);
    }

    /**
     * Test the passed in permission mask
     *
     * @param mask
     * @return
     */
    public boolean hasPermissions(int mask) {
        return check(_perms, mask);
    }

    public boolean hasPermission(PERMISSION perm) {
        return hasPermissions(perm._val);
    }

    /**
     * Toggles the passed in permission.
     *
     * @param p permission
     * @return the toggled permissions valus
     */
    public void togglePermission(PERMISSION p) {
        _perms ^= p._val;
    }

    public String toString() {
        return toString(this._perms);
    }

    private boolean isValidMask(int mask) {
        if (0 <= mask && mask <= ALL_PERMS)
            return true;
        return false;
    }

    /**
     * @return
     */
    public int getVal() {
        return _perms;
    }

    public Permissions union(Permissions perm) {
        int newPerms = _perms;
        newPerms |= perm.getVal();
        return new Permissions(newPerms);
    }

    /**
     * NOT THREAD SAFE (togglePermission can be concurrently called while this
     * method is executing)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Permissions))
            return false;
        return this._perms == ((Permissions) obj)._perms;
    }

    /**
     * NOT THREAD SAFE (togglePermission can be concurrently called while this
     * method is executing)
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this._perms;
        return result;
    }

    public enum PERMISSION {
        NONE("NONE", 0), //$NON-NLS-1$
        READ("READ", 1), //$NON-NLS-1$
        WRITE("WRITE", 2), //$NON-NLS-1$
        EXECUTE("EXECUTE", 4), //$NON-NLS-1$
        LINK("LINK", 8); //$NON-NLS-1$
        private String _mnemonic;
        private int _val;

        PERMISSION(String mnemonic, int val) {
            this._mnemonic = mnemonic;
            this._val = val;
        }

        public static PERMISSION valueOf(int val) {
            for (PERMISSION perm : PERMISSION.values()) {
                if (perm._val == val)
                    return perm;
            }
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.Permissions.IllegalPermissionError") + Integer.toString(val)); //$NON-NLS-1$
        }

        public String mnemonic() {
            return this._mnemonic;
        }

        public int val() {
            return this._val;
        }
    }
}

