/*
 * Created on May 23, 2006
 *
 */
package com.iconclude.dharma.commons.util;

import java.io.File;

// TODO this was moved here from Central project because of the need to access the product names, 
// and it is quite Central/Studio specific...
public class Constants {

    public static final String APP_NAME_PREFIX = "Operations Orchestration";
    // Official product name
    public static final String APP_NAME = APP_NAME_PREFIX + " Software";

    // Official Short Product Name
    public static final String SHORT_APP_NAME = "PAS";
    public static final String CENTRAL_SERVICE_URL = "/" + SHORT_APP_NAME;
    // Official authoring application name
    public static final String STUDIO_NAME = "Studio";
    // root directory (relative to ICONCLUDE_HOME) for Studio
    public static final String RSSTUDIO_ROOT_DIR = STUDIO_NAME + File.separatorChar;
    // Official web application name
    public static final String CENTRAL_NAME = "Central";
    // root directory (relative to ICONCLUDE_HOME) for CEntral
    public static final String CENTRAL_ROOT_DIR = CENTRAL_NAME + File.separatorChar;
    public static final String TRIAL_POSTFIX;

    static {
        if (Boolean.parseBoolean(System.getProperty("trial.mode"))) {
            TRIAL_POSTFIX = " Trial";
        } else {
            TRIAL_POSTFIX = "";
        }
    }

    // Official Studio app name
    public static final String STUDIO_APP_NAME = APP_NAME_PREFIX + " " + STUDIO_NAME + TRIAL_POSTFIX;
    // Official Central app name
    public static final String CENTRAL_APP_NAME = APP_NAME_PREFIX + " " + CENTRAL_NAME + TRIAL_POSTFIX;
    public static final String CONF_ROOT = "conf" + File.separatorChar;
    // properties file (relative to ICONCLUDE_HOME) for Central web app
    public final static String RSCENTRAL_PROPERTIES = CENTRAL_ROOT_DIR + CONF_ROOT + "Central.properties";

    public final static String CENTRAL_SECURED_PROPERTIES = CENTRAL_ROOT_DIR + CONF_ROOT + "central-secured.properties";

    // properties file (relative to ICONCLUDE_HOME) for the authoring application
    public static final String RSSTUDIO_PROPERTIES = RSSTUDIO_ROOT_DIR + CONF_ROOT + "Studio.properties";
    public final static String RSSTUDIO_SECURED_PROPERTIES = RSSTUDIO_ROOT_DIR + CONF_ROOT + "studio-secured.properties";
    public static final String TOOLS_ROOT = "tools" + File.separatorChar;
    public static final String TRUSTSTORE_KEY = "dharma.security.ssl.trustStore";
    public static final String TRUSTSTORE_PASS_KEY = "dharma.security.ssl.trustStorePassword";
    public static final String TRUSTSTORE_TYPE_KEY = "dharma.security.ssl.trustStoreType";
    public static final String KEYSTORE_KEY = "dharma.security.ssl.keyStore";
    public static final String KESTORE_PASS_KEY = "dharma.security.ssl.keyStorePassword";
    public static final String KEYSTORE_TYPE_KEY = "dharma.security.ssl.keyStoreType";

    public static final String HEARTBEAT_LISTENING_ENABLE_KEY = "dharma.heartbeat.listening.enable";
    public static final String HEARTBEAT_LISTENING_PORT_KEY = "dharma.heartbeat.listening.port";

    public final static String HEARTBEAT_SENDING_ENABLED_KEY = "dharma.heartbeat.sending.enable";
    public final static String HEARTBEAT_SENDING_INTERVAL_KEY = "dharma.heartbeat.sending.interval";
    public final static String HEARTBEAT_SENDING_ADDR_KEY = "dharma.heartbeat.sending.address";
    public final static String HEARTBEAT_SENDING_PORT_KEY = "dharma.heartbeat.sending.port";

    public final static String CENTRAL_PORT_KEY = "dharma.repaircenter.port";
    public final static String CENTRAL_PROTO_KEY = "dharma.repaircenter.proto";

    public final static String[][] FILE_CONV = new String[][]{{"\\", "%5c"}, {":", "%3a"}, {"*", "%2a"}, {"?", "%3F"},
            {"\"", "%22"}, {"<", "%3C"}, {">", "%3E"}, {"|", "%7C"}, {".", "%2e"}};

    // UTF-8 is recommended by W3C for encoding/decoding URLs
    public static final String CHARSET_UTF8 = "UTF-8";
    public final static String JAVA_SERIALIZATION = "application/java-serialization";

    public static final String REMOTE_DEBUG_COMMAND_KEY = "dharma.studio.remote.debug.command";
}
