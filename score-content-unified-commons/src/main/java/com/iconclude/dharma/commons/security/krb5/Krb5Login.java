/*
 * Copyright (c) iConclude 2006
 * All rights reserved.
 */

package com.iconclude.dharma.commons.security.krb5;

import com.iconclude.dharma.commons.security.DharmaSecurityToken;
import com.iconclude.dharma.commons.security.SecurityTokenHelper;
import com.iconclude.dharma.commons.util.Dharma;
import com.iconclude.dharma.commons.util.StringUtils;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * We could have used DharmaKrb5AuthenticationProvider, but that one is trying
 * to map authorities; here, we need to get the tickets and keys only...
 *
 * @author octavian
 */
public class Krb5Login {

    public final static String USERNAME_KEY = "username"; //$NON-NLS-1$

    public final static String PASSWORD_KEY = "password"; //$NON-NLS-1$

    public final static String KRB5TICKETS_KEY = "krb5tickets"; //$NON-NLS-1$

    public final static String KRB5KEYS_KEY = "krb5keys"; //$NON-NLS-1$
    private static final Configuration DEFAULT_CONFIGURATION = new Configuration() {
        AppConfigurationEntry[] entries = new AppConfigurationEntry[]{new AppConfigurationEntry(
                "com.sun.security.auth.module.Krb5LoginModule", LoginModuleControlFlag.REQUIRED, //$NON-NLS-1$
                new HashMap<String, Object>()/* no options */)};

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            if (!name.equals(Krb5Configuration.LOGINCONFNAME))
                return null;
            return entries;
        }

        @Override
        public void refresh() {
        }
    };

    public Krb5Login() {
        Krb5Configuration.validate();
    }

    public static LoginContext login(String user, String password) throws LoginException {
        /*
        String realm = Krb5Configuration.getRealm();
        if (StringUtils.isBlank(realm)) {
            throw new IllegalArgumentException("No system property defined for Kerberos realm");
        }*/

        DharmaSecurityToken token = new DharmaSecurityToken();
        SecurityTokenHelper.addSecurityValue(token, Krb5Login.USERNAME_KEY, user, false);
        SecurityTokenHelper.addSecurityValue(token, Krb5Login.PASSWORD_KEY, password, false);

        // have a system property defined, use it
        String loginConf = Krb5Configuration.getJaasConf();
        if (!StringUtils.isBlank(loginConf)) {
            return new Krb5Login().login(token, loginConf, Krb5Configuration.getJaasLoginContextName());
        }

        // don't have a system property defined, make up a default configuration and use it.
        return new Krb5Login().login(token, DEFAULT_CONFIGURATION, Krb5Configuration.LOGINCONFNAME);
    }

    /**
     * @param token
     * @return - login context. The caller should logout the context when not
     * needed anymore
     * @throws LoginException
     */
    public LoginContext login(DharmaSecurityToken token)
            throws LoginException {
        return login(token, Krb5Configuration.getJaasConf(), Krb5Configuration.getJaasLoginContextName(), false);
    }

    /**
     * Not supported.
     *
     * @param token
     * @return - login context. The caller should logout the context when not
     * needed anymore
     * @throws LoginException
     */
    public LoginContext login(DharmaSecurityToken token, Configuration config, String confName)
            throws LoginException {
        if (null == token)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Login.NullSecurityTokenError")); //$NON-NLS-1$
        if (null == config)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Login.NullLoginConfError")); //$NON-NLS-1$

        LoginContext loginContext = new LoginContext(confName, null,
                new InternalCallbackHandler(token), config);

        loginContext.login();

        Subject subject = loginContext.getSubject();

        setupSubject(token, subject);

        return loginContext;
    }

    /**
     * Not supported as a public interface, because the conf file and context name should be set once and
     * for all by Krb5Configuration. When we figure out how to use the API that allows us to dynamically pass
     * in context files and context names, we rely on the Krb5Configuration class to set the proper system
     * properties for us and make use of them.
     *
     * @param token
     * @param loginConfFile
     * @param confName
     * @return a login context.
     * @throws LoginException
     */
    private LoginContext login(DharmaSecurityToken token, String loginConfFile, String confName)
            throws LoginException {

        return login(token, loginConfFile, confName, false);
    }

    // we normally don't allow setting of the system property here, because Krb5Configuration should have
    // set it once and for all. That's why the only public method is the one that calls us with a false
    // parameter, so the system property never gets set.
    private LoginContext login(DharmaSecurityToken token, String loginConfFile, String confName, boolean setSystemProperty) throws LoginException {
        if (null == token)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Login.NullSecurityTokenError")); //$NON-NLS-1$

        if (setSystemProperty) {
            System.setProperty(Krb5Configuration.LOGINCONFPROP, loginConfFile);
        }

        LoginContext loginContext = new LoginContext(confName, new InternalCallbackHandler(token));

        loginContext.login();

        Subject subject = loginContext.getSubject();

        setupSubject(token, subject);

        return loginContext;
    }

    private void setupSubject(DharmaSecurityToken token, Subject subject) {
        Set<KerberosTicket> tickets = subject
                .getPrivateCredentials(KerberosTicket.class);
        if (null != tickets && tickets.size() != 0) {
            try {
                SecurityTokenHelper.addSecurityValue(token, KRB5TICKETS_KEY,
                        Krb5Utils.makeTicketsString(tickets), false);
            } catch (IllegalStateException hasExpired) {
            }
        }
        Set<KerberosKey> keys = subject
                .getPrivateCredentials(KerberosKey.class);
        if (null != keys && keys.size() != 0) {
            SecurityTokenHelper.addSecurityValue(token, KRB5KEYS_KEY,
                    keys.toString(), false);
        }
    }


    private interface _CallbackHandler {
        void handle(Callback callback, DharmaSecurityToken auth);
    }

    private class NameCallbackHandler implements _CallbackHandler {
        public void handle(Callback callback, DharmaSecurityToken auth) {
            if (callback instanceof NameCallback) {
                NameCallback ncb = (NameCallback) callback;
                Object name = SecurityTokenHelper.getSecurityValue(auth, USERNAME_KEY);
                ncb.setName(StringUtils.valueOf(name, "")); //$NON-NLS-1$
            }
        }
    }

    ;

    private class PasswordCallbackHandler implements _CallbackHandler {
        public void handle(Callback callback, DharmaSecurityToken auth) {
            if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;

                Object pass = SecurityTokenHelper.getSecurityValue(auth, PASSWORD_KEY);
                //XXX: what if the password is actually empty?????
                pc.setPassword(StringUtils.valueOf(pass, "").toCharArray()); //$NON-NLS-1$
            }
        }
    }

    ;

    private class InternalCallbackHandler implements CallbackHandler {
        private DharmaSecurityToken auth;

        private _CallbackHandler[] handlers = new _CallbackHandler[]{
                new NameCallbackHandler(), new PasswordCallbackHandler()};

        InternalCallbackHandler(DharmaSecurityToken auth) {
            this.auth = auth;
        }

        public void handle(Callback[] callbacks) throws IOException,
                UnsupportedCallbackException {
            for (_CallbackHandler handler : handlers) {
                for (Callback callback : callbacks) {
                    handler.handle(callback, auth);
                }
            }
        }
    }

    ;
}
