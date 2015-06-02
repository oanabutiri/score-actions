/*
 * Created on Sep 22, 2005
 *
 */
package com.iconclude.dharma.commons.util;

import com.iconclude.dharma.commons.exception.DharmaException;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * @author octavian
 */
public class HomeUtil {
    public static final String ICONCLUDE_HOME = "iconclude.home"; // the name of the java property defining the home //$NON-NLS-1$
    public static final String ICONCLUDE_HOME_ENV = "ICONCLUDE_HOME"; // teh name of the env property defining the home //$NON-NLS-1$
    public static final String ICONCLUDE_PREF_PATH = "/iconclude"; //$NON-NLS-1$
    private final static Logger _log = Logger.getLogger(HomeUtil.class);
    private static Map<String, Properties> propsMap = new HashMap<String, Properties>();

    /**
     * @return the iConclude home location (if defined)
     */
    public static String getIconcludeHome() {
        String val = getIconcludeHomeFromProperty();
        if (null == val)
            val = getIconcludeHomeFromEnv();
        if (null == val)
            val = getIconcludeHomeFromPrefs();
        if (null == val)
            throw new DharmaException(Dharma.msg("Dharma.util.HomeUtil.NoICONCLUDEHOMEError")); //$NON-NLS-1$

        return val;
    }

    private static String getIconcludeHomeFromProperty() {
        String val = System.getProperty(ICONCLUDE_HOME);
        val = StringUtils.trim(val);
        if (null != val && val.length() != 0) {
            _log.debug("ICONCLUDE_HOME=" + val + " from -Diconclude.home jvm arg"); //$NON-NLS-1$ //$NON-NLS-2$
            return val;
        } else
            return null;
    }

    private static String getIconcludeHomeFromEnv() {
        String val = System.getenv(ICONCLUDE_HOME_ENV);
        val = StringUtils.trim(val);
        if (null != val && val.length() != 0) {
            _log.debug("ICONCLUDE_HOME=" + val + " from ICONCLUDE_HOME env variable"); //$NON-NLS-1$ //$NON-NLS-2$
            return val;
        } else
            return null;
    }

    private static String getIconcludeHomeFromPrefs() {
        Preferences prefs = Preferences.systemRoot().node("/com/iconclude"); //$NON-NLS-1$
        if (prefs == null)
            return null;
        String val = prefs.get(ICONCLUDE_HOME, null);
        val = StringUtils.trim(val);
        if (null != val && val.length() != 0) {
            _log.debug("ICONCLUDE_HOME=" //$NON-NLS-1$
                    + val
                    + " from com/iconclude preference (HKLM/SOFTWARE/JavaSoft/Prefs registry key on Windows)"); //$NON-NLS-1$
            return val;
        } else
            return null;
    }

    /**
     * Resolves a resource relative to the iConclude home location.
     *
     * @param name of the resource
     * @return an input stream or null if the resource cannot be located, the home
     * location is not defined or if a null resource name is passed in
     */
    public static InputStream getResourceAsStream(String name) {
        if (null == name)
            return null;
        String path = getIconcludeHome();
        if (null == path || 0 == path.trim().length())
            return null;
        if (!path.endsWith(File.separator) && !name.startsWith(File.separator)) {
            path += File.separator;
        }
        path += name;
        InputStream stream = null;
        try {
            stream = new FileInputStream(path);
        } catch (FileNotFoundException e) {/*empty*/
        }

        return stream;
    }

    /**
     * Resolves a resource relative to the iConclude home location
     *
     * @param name of the resource
     * @return a file instance or null if the resource cannot be located, the home
     * location is not defined or if a null resource name is passed in
     */
    public static File getResourceAsFile(String name) {
        if (null == name)
            return null;
        String path = getIconcludeHome();
        if (null == path || 0 == path.trim().length())
            return null;
        if (!path.endsWith(File.separator) && !name.startsWith(File.separator)) {
            path += File.separator;
        }
        path += name;
        return new File(path);
    }

    /**
     * Tries to find the resource in the specified places.  Returns the path of the first found.
     *
     * @param places
     * @return the full path based on first found or null if not found
     */
//    public static String findResource(String[] places) {
//        for (String place : places) {
//            File f = HomeUtil.getResourceAsFile(place);
//            _log.info("Looking for " + place + " in " + f.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
//            if (f.exists()) {
//                return place;
//            }
//        }
//
//        return null;
//    }

    /**
     * Loads and returns properties (relative to ICONCLUDE_HOME). If reload is true,
     * the properties are reloaded regardless if already cached
     *
     * @param reload
     * @return
     */
    public static synchronized Properties getLocalProperties(String path, boolean reload) {
        Properties props = propsMap.get(path);
        if (null == props || reload) {
            InputStream is = HomeUtil.getResourceAsStream(path);
            if (null == is) {
                String errMsg = Dharma.msg("Dharma.util.HomeUtil.MissingPropertiesFileError") + path; //$NON-NLS-1$
                throw new DharmaException(errMsg);
            }
            props = new Properties();
            try {
                props.load(is);
            } catch (IOException e) {
                String errMsg = Dharma.msg("Dharma.util.HomeUtil.LoadPropertiesFileError") + path; //$NON-NLS-1$
                throw new DharmaException(errMsg, e);
            }
            propsMap.put(path, props);
        }
        return props;
    }

    /**
     * Returns the path to the user's home directory
     *
     * @return
     */
    public static String getUserHome() {
        return System.getProperty("user.home"); //$NON-NLS-1$
    }

    /**
     * attempts to resolve a path to a file:
     * <ul>
     * <li>if file can be found based on the supplied <code>path</code> param,
     * the call is equivalent to <code>return new File(path)</code>
     * <li>if file can be found within ICONCLUDE_HOME, it is resolved and returned
     * <li>if file can be found wihtin the classpath, it is resolved and returned
     * <li>if all the above checks fail, null gets returned.
     * </ul>
     *
     * @param path the suspected path to the file
     * @return a File if path can be resolved, null otherwise.
     */
    public static File resolvePath(String path) {
        File f = new File(path);
        if (!f.exists()) {
            // search within ICONCLUDE_HOME
            f = HomeUtil.getResourceAsFile(path);
            if (!f.exists()) {
                // classpath file
                URL url = Thread.currentThread().getContextClassLoader().getResource(path);
                if (url == null) {
                    return null;
                }


                f = null;
                try {
                    String decodedUrl = URLDecoder.decode(url.getFile(), Constants.CHARSET_UTF8);
                    if (decodedUrl != null) {
                        f = new File(decodedUrl);
                    } else {
                        _log.error("Unable to decode url " + url); //$NON-NLS-1$
                    }
                } catch (UnsupportedEncodingException ucee) {
                    _log.error("Unsupported character format", ucee); //$NON-NLS-1$
                }

                if (f == null || !f.exists()) { // this is really unlikely to happen, unless there's a bug.
                    return null;
                }
            }
        }
        return f;

    }

}
