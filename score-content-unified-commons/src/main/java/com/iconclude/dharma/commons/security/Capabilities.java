/*
 * Copyright (c) iConclude 2004-2006
 * All rights reserved.
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.util.Dharma;

import java.io.Serializable;


/**
 * EnumSet has about the same semantics as this class, but there is no access
 * to the internal (bitmask) representation of it, and we need that value to
 * persist...
 *
 * @author octavian
 */
public final class Capabilities implements Serializable {
    public static final long ALL_CAPS;

    static {
        long res = 0;
        for (CAPABILITY cap : CAPABILITY.ALL_CAPS) {
            res |= cap._val;
        }
        ALL_CAPS = res;
    }

    private static final long serialVersionUID = -634946293634539150L;
    private static final String capsKey = "Dharma.group.caps.";

    ;
    private static final String capsDescriptionKey = "Dharma.group.capsDescription.";
    private long _caps;

    public Capabilities(long val) {
        if (!isValidMask(val))
            throw new IllegalArgumentException("Illegal capabilities:" + Long.toString(val));
        _caps = val;
    }

    public Capabilities() {
        _caps = CAPABILITY.NONE._val;
    }

    static boolean isValidMask(long mask) {
        if (CAPABILITY.NONE._val <= mask && mask <= ALL_CAPS)
            return true;
        return false;
    }

    /**
     * NOT THREAD SAFE (toggleCpability can be concurrently called while this
     * method is executing)
     *
     * @param cap
     * @return true if the tested capability is set
     */
    public boolean hasCapability(CAPABILITY cap) {
        if (_caps == cap._val)
            return true;
        if (cap == CAPABILITY.NONE)
            return (_caps == CAPABILITY.NONE._val);
        return ((_caps & cap._val) == cap._val);
    }

    /**
     * NOT THREAD SAFE (toggleCpability can be concurrently called while this
     * method is executing)
     */
    public long getVal() {
        return _caps;
    }

    /**
     * Returns the description string of this capabilities set.
     * NOT THREAD SAFE (toggleCpability can be concurrently called while this
     * method is executing)
     */
    public String getDescription() {
        return format(false, false);
    }

    public String getLocalizedDescription() {
        return format(false, true);
    }

    /**
     * NOT THREAD SAFE (toggleCpability can be concurrently called while this
     * method is executing)
     */
    @Override
    public String toString() {
        return format(true, false);
    }

    public String toLocalizedString() {
        return format(true, true);
    }

    private String format(boolean mnemonic, boolean localized) {
        String cap;
        String locCap;
        String key;
        String capsName;

        key = mnemonic ? capsKey : capsDescriptionKey;
        if (_caps == CAPABILITY.NONE._val) {
            capsName = CAPABILITY.ALL_CAPS[0]._mnemonic;
            cap = (mnemonic ? capsName : CAPABILITY.ALL_CAPS[0]._description);
            if (localized) {
                locCap = Dharma.msg(key + capsName);
                // if there is no localized string return the capability
                if (!locCap.startsWith(key)) {
                    cap = locCap;
                }
            }
            return cap;
        }

        long mask = _caps;
        StringBuffer res = new StringBuffer();
        int pos = 1;

        while (mask != 0 && pos < CAPABILITY.ALL_CAPS.length) {
            boolean appended = false;
            if ((mask & 1) == 1) {
                capsName = CAPABILITY.ALL_CAPS[pos]._mnemonic;
                cap = mnemonic ? capsName : CAPABILITY.ALL_CAPS[pos]._description;
                if (localized) {
                    locCap = Dharma.msg(key + capsName);
                    // if there is no localized string return the capability
                    if (!locCap.startsWith(key)) {
                        cap = locCap;
                    }
                }
                res.append(cap);
                appended = true;
            }

            ++pos;
            mask >>>= 1;
            if (appended && mask != 0)
                res.append(',');
        }
        return res.toString();
    }

    /**
     * Toggles the passed in capability
     * NOT THREAD SAFE
     *
     * @param cap
     */
    public void toggleCapability(CAPABILITY cap) {
        _caps ^= cap._val;
    }

    /**
     * NOT THREAD SAFE (toggleCpability can be concurrently called while this
     * method is executing)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Capabilities))
            return false;
        return this._caps == ((Capabilities) obj)._caps;
    }

    /**
     * NOT THREAD SAFE (toggleCpability can be concurrently called while this
     * method is executing)
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (int) (_caps ^ (_caps >>> 32));
        return result;
    }

    public enum CAPABILITY {
        NONE("NONE", 0, "No capabilities"),
        MANAGE_USERS("MANAGE_USERS", 1, "Allows user management"),
        MANAGE_GROUPS("MANAGE_GROUPS", 2, "Allows group management"),
        AUTHOR("AUTHOR", 4, "Allows authoring"),
        SCHEDULE("SCHEDULE", 8, "Allows scheduling flows"),
        MANAGE_RUNS("MANAGE_RUNS", 16, "Allows managing others' runs"),
        RUN_REPORTS("RUN_REPORTS", 32, "Allows reporting and viewing the dashboard"),
        MANAGE_CONF("MANAGE_CONF", 64, "Allows system configuration management"),
        VIEW_SCHEDULES("VIEW_SCHEDULES", 128, "Allows viewing scheduled flows"),
        HEADLESS_FLOWS("HEADLESS_FLOWS", 256, "Allows running headless flows");
        // if a new capability is added above, this needs to be updated
        public static final CAPABILITY[] ALL_CAPS = {
                NONE,
                MANAGE_USERS,
                MANAGE_GROUPS,
                AUTHOR,
                SCHEDULE,
                MANAGE_RUNS,
                RUN_REPORTS,
                MANAGE_CONF,
                VIEW_SCHEDULES,
                HEADLESS_FLOWS
        };
        private String _mnemonic;
        private String _description;
        private long _val;

        CAPABILITY(String mnemonic, long val, String descr) {
            this._mnemonic = mnemonic;
            this._val = val;
            this._description = descr;
        }

        public static CAPABILITY valueOf(long val) {
            if (val < ALL_CAPS[0]._val || val > ALL_CAPS[ALL_CAPS.length - 1]._val)
                throw new IllegalArgumentException("No enum const CAPABILITY for value " + Long.toString(val));
            CAPABILITY res = null;
            for (CAPABILITY cap : ALL_CAPS) {
                if (cap._val == val) {
                    res = cap;
                    break;
                }
            }
            if (null == res)
                throw new IllegalArgumentException("No enum const CAPABILITY for value " + Long.toString(val));
            return res;
        }

        public String mnemonic() {
            return this._mnemonic;
        }

        public String localizedMnemonic() {
            String locMnemonic = Dharma.msg(capsKey + this._mnemonic);
            // if there is no localized string return the mnemonic
            if (locMnemonic.startsWith(capsKey)) {
                locMnemonic = this._mnemonic;
            }
            return locMnemonic;
        }

        public long val() {
            return this._val;
        }

        public String description() {
            return this._description;
        }

        public String localizedDescription() {
            String locDescription = Dharma.msg(capsDescriptionKey + this._mnemonic);
            // if there is no localized string return the description
            if (locDescription.startsWith(capsDescriptionKey)) {
                locDescription = this._description;
            }
            return locDescription;
        }
    }
}
