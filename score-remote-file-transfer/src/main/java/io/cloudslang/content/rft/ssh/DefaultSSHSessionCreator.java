/*
 * Copyright (c) iConclude 2004-2006
 * All rights reserved.
 */
package io.cloudslang.content.rft.ssh;

import io.cloudslang.content.rft.exception.DharmaException;
import io.cloudslang.content.rft.utils.CollectionUtils;
import io.cloudslang.content.rft.utils.Dharma;
import io.cloudslang.content.rft.utils.Pair;
import io.cloudslang.content.rft.utils.StringUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.security.auth.kerberos.KerberosTicket;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author mmerz
 * @author statu
 * @author octavian
 */
public class DefaultSSHSessionCreator implements SSHSessionCreator {

    private static final int SESS_DEFAULT_TIMEOUT = 120000; // 2 minutes
    protected final Log logger = LogFactory.getLog(getClass());

    private static final int SESSION_SOCKET_TIMEOUT;

    static {
        int val = 0;
        try {
            val = Integer.valueOf(System.getProperty("dharma.ssh.session.timeout", "10000")).intValue();
        } catch (Throwable localThrowable) {
        }
        if (val <= 0) {
            val = 10000;
        }

        SESSION_SOCKET_TIMEOUT = val;
    }

    public Session createSession(Map bindings) throws JSchException {
        JSch secureShell = new JSch();

        Pair<String, Integer> hostCfg = getHostConfiguration(bindings);
        Pair<String, String> userCfg = getUsernameAndPassword(bindings);
        Collection<KerberosTicket> krbTickets = getKerberosTickets(bindings);

        String pkfile = getPrivateKeyFile(bindings);

        String username = userCfg.getFirst();
        String password = userCfg.getSecond();
        if (!StringUtils.isEmpty(pkfile)) {
            String passphrase = StringUtils.isEmpty(password) ? null : password;
            secureShell.addIdentity(pkfile, passphrase);
        }

        Session session = null;
        SSHUserInfoProvider userInfo = null;
        try {
            userInfo = createUserInfo(username, password, krbTickets);
            session = secureShell.getSession(username, hostCfg.getFirst(), hostCfg.getSecond());
            session.setUserInfo(userInfo);
            session.setTimeout(SESSION_SOCKET_TIMEOUT);
            this.logger.debug("Setting session socket timeout to: " + SESSION_SOCKET_TIMEOUT);
            session.setTimeout(getSessTimeout(bindings));
            session.connect();
        } finally {
            Dharma.close(userInfo);
        }

        this.logger.info("ServerVersion: " //$NON-NLS-1$
                + session.getServerVersion()
                + ", ClientVersion: " //$NON-NLS-1$
                + session.getClientVersion());

        return session;
    }

    private Collection<KerberosTicket> getKerberosTickets(Map bindings) {
        Collection<KerberosTicket> tickets = (Collection<KerberosTicket>) CollectionUtils.get(bindings, SSHOperation.KRBTICKETS);
        if (tickets == null) {
            return Collections.emptyList();
        }
        return tickets;
    }

    protected SSHUserInfoProvider createUserInfo(String username, String password, Collection<KerberosTicket> tickets) {
        return new DefaultUserInfoProvider(username, password, tickets);
    }

    protected Pair<String, Integer> getHostConfiguration(Map bindings) {
        String hostname = StringUtils.trim((String) bindings.get(SSHOperation.HOST));
        Object portObj = bindings.get("Port");
        int port = 22;
        if (portObj != null) {
            int portInt = (Integer) portObj;
            if (portInt > 0) {
                port = portInt;
            }
        }
        if (StringUtils.isEmpty(hostname)) {
            throw new DharmaException(Dharma.msg("Dharma.security.ssh.DefaultSSHSessionCreator.UnspecifiedHostnameError")); //$NON-NLS-1$
        }
        return new Pair<String, Integer>(hostname, port);
    }

    protected String getPrivateKeyFile(Map bindings) {
        String pkfile = StringUtils.trim((String) bindings.get("PKFile"));
        if (StringUtils.isEmpty(pkfile))
            return pkfile;
        pkfile = StringUtils.trimTrailing(pkfile, '\\');
        pkfile = StringUtils.trimTrailing(pkfile, '/');
        File f = new File(new File(System.getProperty("java.home")), pkfile);
        if (!f.exists())
            f = new File(pkfile);
        return f.getAbsolutePath();
    }

    protected Pair<String, String> getUsernameAndPassword(Map bindings) {

        String username = StringUtils.trim((String) bindings.get(SSHOperation.USERNAME));
        String password = StringUtils.trim((String) bindings.get(SSHOperation.PASSWORD));
        if (StringUtils.isBlank(username))
            throw new DharmaException(Dharma.msg("Dharma.security.ssh.DefaultSSHSessionCreator.UnspecifiedUsernameError")); //$NON-NLS-1$

        // XXX: see if we need any validation around here.        
        return new Pair<String, String>(username, password);
    }

    private int getSessTimeout(Map bindings) {
        int timeout = SESS_DEFAULT_TIMEOUT;
        String timeoutStr = (String) bindings.get(SSHOperation.TIMEOUT);
        if (!StringUtils.isBlank(timeoutStr)) {
            try {
                timeout = Integer.parseInt(timeoutStr);
            } catch (NumberFormatException ignore) {
                timeout = SESS_DEFAULT_TIMEOUT;
            }
        }
        return timeout;
    }
}
