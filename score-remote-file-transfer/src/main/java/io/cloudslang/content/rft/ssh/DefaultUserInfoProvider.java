package io.cloudslang.content.rft.ssh;

import io.cloudslang.content.rft.utils.Dharma;
import io.cloudslang.content.rft.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import java.util.Collection;

/**
 * The default user information provider, supports all kinds of authentication methods:
 * <ul>
 * <li>username and password - it simply returns the values.
 * <li>keyboard interactive (in a limited way, because it answers with the password to every question).
 * <li>kerberos (gssapi-with-mic.krb5) it obtains a login context based on the username and password, then
 * uses that context's Subject to answer the <code>getSubject()</code> callback.
 * </ul>
 * <p/>
 * <p>This class is not thread safe. Do not share its objects between threads.
 * <p/>
 * <p>The provider must be closed by the client in a <code>finally</code> block, otherwise login contexts
 * may be leaked in case Krb5 authentication is performed.
 *
 * @author statu
 */
public class DefaultUserInfoProvider implements SSHUserInfoProvider {

    protected final Log logger = LogFactory.getLog(getClass());

    private final String username;
    private final String passwd;
    private final Collection<KerberosTicket> tickets;

    // gotta store this reference here so we can logout from the close() method. 
    private LoginContext krbLogin = null;

    public DefaultUserInfoProvider(String username, String passwd, Collection<KerberosTicket> tickets) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.ssh.DefaultUserInfoProvider.NoUsernameError")); //$NON-NLS-1$
        }
        this.username = StringUtils.trim(username);
        // XXX: note we cannot feed a null pwd to Jsch, it will cause NPE's
        this.passwd = StringUtils.valueOf(passwd, ""); //$NON-NLS-1$
        this.tickets = tickets;
    }

    public String getPassword() {
        return passwd;
    }

    public String getPassphrase() {
        return StringUtils.isNull(passwd) ? null : passwd;
    }

    public boolean promptYesNo(String str) {
        logger.debug("DefaultUserInfoImpl::promptYesNo: " + str); //$NON-NLS-1$
        return true;
    }

    public boolean promptPassphrase(String message) {
        logger.debug("DefaultUserInfoImpl::promptPassphrase: " + message); //$NON-NLS-1$
        return true;
    }

    public boolean promptPassword(String message) {
        logger.debug("DefaultUserInfoImpl::promptPassword: " + message); //$NON-NLS-1$
        return true;
    }

    public void showMessage(String message) {
        logger.debug("DefaultUserInfoImpl::showMessage: " + message); //$NON-NLS-1$
    }

    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
                                              boolean[] echo) {

        if (logger.isDebugEnabled()) {
            logger.debug("DefaultUserInfoImpl::promptKeyboardInteractive:(" //$NON-NLS-1$
                    + destination
                    + ", " //$NON-NLS-1$
                    + name
                    + ", " //$NON-NLS-1$
                    + instruction
                    + ", [" //$NON-NLS-1$
                    + StringUtils.arrayToString(prompt)
                    + "], [" //$NON-NLS-1$
                    + StringUtils.arrayToString(echo)
                    + "]"); //$NON-NLS-1$
        }
        int len = Math.min(prompt.length, echo.length);

        // we must always return the same number of responses as there
        // are prompts (or echoes). The problem is that we don't know what
        // to do when multiple prompts come in - sometimes there are 2
        // prompts (one for the pwd, one to confirm it), but we don't know
        // what they mean (that's why it's called *interactive* so some dude
        // reads the instruction and the prompts and makes sense of them).
        // We simply plop the password in each of the responses, with the
        // hope the only use case for us here is the one where the password
        // is asked.
        String[] result = new String[len];
        for (int i = 0; i < len; i++) {
            result[i] = passwd;
        }
        return result;
    }

    public void close() {
        if (krbLogin != null) {
            try {
                krbLogin.logout();
            } catch (Throwable t) {
                logger.error("Could not log out from Kerberos, reason: " + t, t); //$NON-NLS-1$
            }
        }
    }
}
