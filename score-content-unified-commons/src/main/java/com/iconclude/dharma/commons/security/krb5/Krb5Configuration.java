package com.iconclude.dharma.commons.security.krb5;

import com.iconclude.dharma.commons.util.Dharma;
import com.iconclude.dharma.commons.util.HomeUtil;
import com.iconclude.dharma.commons.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Properties;

/**
 * Helper class to be used for setting KRB5 system properties. Thes props can be used
 * wherever Kerberos authentication is needed, such as in logins and SSH operations.
 * <p/>
 * <p>The class must be configured with a property file (either specified by path or
 * directly as a <code>Properties</code> object reference), from which it attempts to read
 * the following values:
 * <ul>
 * <li>the path to the Kerberos configuration file (property <code>krb5.conf</code>)
 * <li>the kdc host name (property <code>krb5.kdc</code>)
 * <li>the kerberos realm name (property <code>krb5.realm</code>)
 * <li>the paath to the JAAS configuraation file (prooperty <code>jaas.conf</code>)
 * <li>the name of the login context within the JAAS configuration file (property <code>jaas.loginContextName</code>
 * - defaults to DharmaKrb5JAAS.
 * </ul>
 * <p/>
 * <p>These values then are set as system properties and should not be set again.
 * <p/>
 * TODO oflo: the "static nature" of this class, its usage of system properties, etc is a problem
 * and it should be solved at some point. The fact that Kerberos needs system properties for driving
 * its functioning should NOT force us to have this class using system props, static members etc
 *
 * @author statu
 */
public class Krb5Configuration {

    /**
     * System property for Kerberos realm name
     */
    public final static String REALMPROP = "java.security.krb5.realm"; //$NON-NLS-1$
    /**
     * System property for host that servers as Kerberos Key Distribution Centre
     */
    public final static String KDCPROP = "java.security.krb5.kdc"; //$NON-NLS-1$
    /**
     * System property for Kerberos configuration file
     */
    public final static String CONFPROP = "java.security.krb5.conf"; //$NON-NLS-1$
    /**
     * System property for JAAS configuration file (different than the Kerberos configuration file
     */
    public final static String LOGINCONFPROP = "java.security.auth.login.config"; //$NON-NLS-1$
    /**
     * System property for JAAS login context name within the JAAS configuration file
     */
    public static final String LOGINNAMECONFPROP = "dharma.security.auth.login.confName"; //$NON-NLS-1$
    /**
     * Default name of the login configuration entry to be used inside the JAAS configuration file
     */
    public final static String LOGINCONFNAME = "DharmaKrb5JAAS"; //$NON-NLS-1$
    /**
     * default name for the JAAS login context name
     */
    public static final String CENTRAL_DEFAULT_JAAS_LOGIN_CONTEXT_NAME = Krb5Configuration.LOGINCONFNAME;
    /**
     * property name for Kerberos config file
     */
    public static final String CENTRAL_PROP_KRB5_CONF = "krb5.conf"; //$NON-NLS-1$

    /**
     * property name for Kerberos KDC
     */
    public static final String CENTRAL_PROP_KRB5_KDC = "krb5.kdc"; //$NON-NLS-1$

    /**
     * property name for Kerberos realm
     */
    public static final String CENTRAL_PROP_KRB5_REALM = "krb5.realm"; //$NON-NLS-1$

    /**
     * property name for JAAS configuration file
     */
    public static final String CENTRAL_PROP_JAAS_CONF = "jaas.conf"; //$NON-NLS-1$

    /**
     * default name for JAAS configuration file
     */
    public static final String CENTRAL_DEFAULT_JAAS_CONF = "jaasLogin.conf"; //$NON-NLS-1$

    /**
     * property name for the JAAS login context name
     */
    public static final String CENTRAL_PROP_JAAS_LOGIN_CONTEXT_NAME = "jaas.loginContextName"; //$NON-NLS-1$
    /**
     * points to an (if any) LDAP provider for group membership lookup
     */
    public static final String CENTRAL_PROP_KRB5_LDAPLOOKUP = "Krb5Auth.LDAPGroupMappingLookup"; //$NON-NLS-1$
    protected final Log logger = LogFactory.getLog(getClass());
    private final String propFile;
    private final Properties props;

    public Krb5Configuration(Properties props) {
        this.propFile = null;
        this.props = props;
    }

    public Krb5Configuration(String propFile) {
        this.propFile = propFile;
        this.props = null;
    }

    public static String getRealm() {
        return System.getProperty(REALMPROP);
    }

    public static String getKdc() {
        return System.getProperty(KDCPROP);
    }

    public static String getKrbConf() {
        return System.getProperty(CONFPROP);
    }

    public static String getJaasConf() {
        return System.getProperty(LOGINCONFPROP);
    }

    public static String getJaasLoginContextName() {
        return System.getProperty(LOGINNAMECONFPROP);
    }

    /**
     * Checks the validity of the following system properties:
     * <ul>
     * <li>java.security.krb5.realm
     * <li>java.security.krb5.kdc
     * <li>java.security.krb5.conf
     * </ul>
     */
    public static void validate() {
        validate(getRealm(), getKdc(), getKrbConf());
    }

    public static void validate(String realm, String kdc, String krb5ConfFile) {
        boolean haveRealm = !StringUtils.isBlank(realm);
        boolean haveKdc = !StringUtils.isBlank(kdc);
        boolean haveConf = !StringUtils.isBlank(krb5ConfFile);

        if (haveRealm && !haveKdc)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Configuration.UnassignedRealmError")); //$NON-NLS-1$
        if (haveKdc && !haveRealm)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Configuration.UnassignedDefaultRealmError")); //$NON-NLS-1$
        if (!haveRealm && !haveKdc && !haveConf)
            throw new IllegalArgumentException(
                    Dharma.msg("Dharma.security.krb5.Krb5Configuration.InvalidConfOrRealmError")); //$NON-NLS-1$
    }

    public void init() {
        init(true);
    }

    public void init(boolean initJaas) {

        Properties props = getProperties();

        String realm = props.getProperty(CENTRAL_PROP_KRB5_REALM);
        if (!StringUtils.isBlank(realm)) {
            System.setProperty(Krb5Configuration.REALMPROP, realm.trim());
        } else {
            System.getProperties().remove(Krb5Configuration.REALMPROP);
        }

        String kdc = props.getProperty(CENTRAL_PROP_KRB5_KDC);
        if (!StringUtils.isBlank(kdc)) {
            System.setProperty(Krb5Configuration.KDCPROP, kdc.trim());
        } else {
            System.getProperties().remove(Krb5Configuration.KDCPROP);
        }

        String conf = props.getProperty(CENTRAL_PROP_KRB5_CONF);
        String confPath = null;
        if (!StringUtils.isBlank(conf)) {
            File f = HomeUtil.resolvePath(conf);
            if (f != null) {
                confPath = f.getAbsolutePath();
            } else {
                logger.error("could not resolve Krb5 Conf file at: " + conf); //$NON-NLS-1$
            }
        }
        if (!StringUtils.isBlank(confPath)) {
            System.setProperty(Krb5Configuration.CONFPROP, confPath);
        } else {
            System.getProperties().remove(Krb5Configuration.CONFPROP);
        }

        logger.info("realm=" + realm + ", kdc=" + kdc + ", krb5conf=" + conf); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (initJaas) {
            initJaasForKerberos();
        }
    }

    /**
     * attempt to use a JAAS configuration file, which can be specified in the supplied property file,
     * or can be searched through the classpath. If the file can be found and it is readable, then the
     * system property <code>java.security.auth.login.config</code> is set to the value of this file.
     */
    public void initJaasForKerberos() {

        String configFile = props.getProperty(CENTRAL_PROP_JAAS_CONF);

        // no property specified, look through the classpath
        if (StringUtils.isBlank(configFile)) {
            configFile = CENTRAL_DEFAULT_JAAS_CONF;
        }

        // check config file exists.
        File f = HomeUtil.resolvePath(configFile);
        if (f == null) {
            logger.error("JAAS config file cannot be resolved: " + configFile); //$NON-NLS-1$
            return;
        }
        if (!f.canRead()) {
            logger.error("JAAS config file is not readable: " + f); //$NON-NLS-1$
            return;
        }

        String loginContextName = props.getProperty(CENTRAL_PROP_JAAS_LOGIN_CONTEXT_NAME);
        if (StringUtils.isBlank(loginContextName)) {
            loginContextName = CENTRAL_DEFAULT_JAAS_LOGIN_CONTEXT_NAME;
        }

        String loginConf = f.getAbsolutePath();
        setJaasLoginConfigFileProperty(loginConf, loginContextName);

        logger.info("jaasConf=" + loginConf + ", jaasLoginContextName=" + loginContextName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param jaasConf the system property <code>java.security.auth.login.config</code> is set to this value.
     */
    public void setJaasLoginConfigFileProperty(String jaasConf, String loginContextName) {
        System.setProperty(Krb5Configuration.LOGINCONFPROP, StringUtils.trim(jaasConf));
        System.setProperty(Krb5Configuration.LOGINNAMECONFPROP, StringUtils.trim(loginContextName));
    }

    public Properties getProperties() {

        if (props == null && StringUtils.isEmpty(propFile)) {
            throw new IllegalStateException(Dharma.msg("Dharma.security.krb5.Krb5Configuration.NullConfigurationError")); //$NON-NLS-1$
        }

        if (props != null) {
            return props;
        }

        return HomeUtil.getLocalProperties(propFile, false);
    }
}
