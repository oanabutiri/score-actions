package com.opsware.pas.content.commons.util;


/**
 * IAction static String Utility Methods.
 * <p/>
 * This class has been exposed to Content authors (partners and customers).
 * All changes made to this class MUST remain backwards compatible with previous versions.
 * <p/>
 * �Copyright 2008-2012 Hewlett-Packard Development Company, L.P.
 */
public class StringUtils {

    /**
     * Checks if a supplied String is null or empty.
     *
     * @param s String to check
     * @return true if supplied string is null or empty
     */
    public static boolean isNull(String s) {
        return s == null || s.isEmpty();
    }

}
