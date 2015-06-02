/*
 * Created on Jul 30, 2005
 *
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.util.Dharma;
import com.iconclude.dharma.commons.util.StringUtils;
import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;

import javax.security.auth.kerberos.KerberosPrincipal;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author octavian
 */
public class DharmaAuthentication implements Serializable, IDharmaAuthentication, Authentication {

    public static final InternalAccount internalAcctToken = new InternalAccount() {
        private static final long serialVersionUID = 355650924821208857L;
    };
    private static final long serialVersionUID = -418279367487142462L;
    private final List<Object> _details = new ArrayList<Object>();
    private Object credentials;
    private Object principal;
    private String username;
    private String qname; // fully qualified name, could be same as username if no domain/realm are used
    private String realm; // this can be a Kerberos realm, a Windows domain or null
    private GrantedAuthority[] authorities;
    private boolean authenticated = false;
    private IGroup[] groups;
    private URL authSource = null; // url of the authentication source

    private String sessionId;

    private boolean internalAcct = false;

    public DharmaAuthentication(Authentication auth, IGroup[] groups) {
        if (null == auth)
            throw new IllegalArgumentException(
                    Dharma.msg("Dharma.security.DharmaAuthentication.NullAuthError")); //$NON-NLS-1$
        Object details = auth.getDetails();
        if (null != details) {
            if (details instanceof Collection<?>) {
                _details.addAll((Collection<?>) details);
            } else {
                _details.add(auth.getDetails());
            }
        }
        this.credentials = auth.getCredentials();
        this.principal = auth.getPrincipal();
        if (null != auth.getAuthorities()) {
            this.authorities = new GrantedAuthority[auth.getAuthorities().length];
            System.arraycopy(auth.getAuthorities(), 0, this.authorities, 0, this.authorities.length);
        }
        this.authenticated = auth.isAuthenticated();
        setGroups(groups);

        InternalAccount ic = getDetail(InternalAccount.class);
        if (ic != null) {
            internalAcct = true;
        }

        _processPrincipal();
    }

    ;

    // package-private
    DharmaAuthentication(Object principal, Object credentials, GrantedAuthority[] authorities,
                         boolean authenticated, IGroup[] groups) {
        this.principal = principal;
        this.credentials = credentials;
        this.authorities = authorities;
        this.authenticated = authenticated;
        setGroups(groups);
        _processPrincipal();
    }

    private DharmaAuthentication() {
    }

    /*
     * ATTENTION! Do NOT use this method, one should get pieces of information from this class
     * through its methods.
     */
    @Override
    public String toString() {
        return super.toString() + ":DO NOT USE!";
    }

    /**
     * We enforce the following rules:
     * - the qualified name is forced to lowercase; it is used everywhere, and especially for identifying
     * the user for repository operations (checkout/checkin, etc)
     * - we leave the username AS TYPED, as it could be used with logged in users credentials against systems
     * that use case sensitive usernames (ssh on top of case sensitive linux accounts?)
     * - we do not touch the realm. The Kerberos realm is case sensitive, even though the convention is to have
     * it uppercase (and AD seems to be enforcing that). The AD domain is not case sensitive...
     */
    private void _processPrincipal() {
        if (this.principal instanceof UserDetails) {
            qname = ((UserDetails) principal).getUsername().toLowerCase();
            username = AuthUtils.getUsername((UserDetails) principal);
            realm = AuthUtils.getRealm((UserDetails) principal);
        } else if (this.principal instanceof KerberosPrincipal) {
            qname = ((KerberosPrincipal) principal).getName().toLowerCase();
            username = AuthUtils.getUsername((KerberosPrincipal) principal);
            realm = AuthUtils.getRealm((KerberosPrincipal) principal);
        } else {
            String principalName = StringUtils.valueOf(this.principal, ""); //$NON-NLS-1$
            qname = principalName.toLowerCase();
            username = AuthUtils.getUsername(principalName);
            realm = AuthUtils.getRealm(principalName);
        }
    }

    /**
     * @return the url of the authentication source (who gave the gree light).
     * It can be null even for a successful authentication. Its purspose is to
     * help client code track authentications (if necessry)
     */
    public URL getAuthSource() {
        return authSource;
    }

    public void setAuthSource(URL url) {
        authSource = url;
    }

    /**
     * <code>groups</code> is the internal list of groups assigned to a
     * particular user. They could one-to-one to the external granted
     * authorities, or could be the result of some sort of a mapping.
     *
     * @return list of user's groups
     */
    public IGroup[] getGroups() {
        if (groups == null)
            return null;
        IGroup[] ret = new IGroup[groups.length];
        System.arraycopy(groups, 0, ret, 0, groups.length);
        return ret;
    }

    public void setGroups(IGroup[] groups) {
        if (groups != null) {
            this.groups = new IGroup[groups.length];
            System.arraycopy(groups, 0, this.groups, 0, groups.length);
        } else {
            this.groups = null;
        }
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
    }

    public boolean isInternalAcct() {
        return this.internalAcct;
    }

    /*
     * (non-Javadoc) The granted authorities represent the external authorities.
     */
    public GrantedAuthority[] getAuthorities() {
        return this.authorities;
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.model.security.IDharmaAuthentication#getDetails(java.lang.Class)
     */
    public <T> List<T> getDetails(Class<T> cls) {
        if (null == cls)
            throw new IllegalArgumentException(
                    Dharma.msg("Dharma.security.DharmaAuthentication.NullDetailsError")); //$NON-NLS-1$
        List<T> ret = new ArrayList<T>();
        for (Object obj : _details) {
            // returns the first "match" (observes polymorphism)
            if (cls.isAssignableFrom(obj.getClass()))
                ret.add(cls.cast(obj));
        }
        return ret;
    }

    @Override
    public Object getDetails() {
        return _details;
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.model.security.IDharmaAuthentication#getDetail(java.lang.Class)
     */
    public <T> T getDetail(Class<T> cls) {
        if (null == cls)
            throw new IllegalArgumentException(
                    Dharma.msg("Dharma.security.DharmaAuthentication.NullDetailsError")); //$NON-NLS-1$
        for (Object obj : _details) {
            // returns the first "match" (observes polymorphism)
            if (cls.isAssignableFrom(obj.getClass()))
                return cls.cast(obj);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.model.security.IDharmaAuthentication#addDetails(java.lang.Object)
     */
    public void addDetails(Object details) {
        if (null != details)
            _details.add(details);
    }

    public void removeDetails(Object details) {
        Iterator<Object> iter = _details.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj.equals(details)) {
                iter.remove();
            }
        }
    }

    public Object getPrincipal() {
        return this.principal;
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.model.security.IDharmaAuthentication#getName()
     */
    public String getName() {
        return username;
    }

    public String getRealm() {
        return realm;
    }

    public String getQName() {
        return qname;
    }

    public boolean isAdministrator() {
        if (this.groups != null) {
            for (IGroup group : groups) {
                if (GroupTemplate.isAdminGroup(group))
                    return true;
            }
        }
        return false;
    }

    public boolean isPromoter() {
        if (this.groups != null) {
            for (IGroup group : groups) {
                if (GroupTemplate.isPromoterGroup(group))
                    return true;
            }
        }
        return false;
    }

    /**
     * Creates a "safe" copy, where the credentials (password in fact) are not
     * available
     * <p/>
     * TODO this could contain Kerberos tickets/keys...
     *
     * @return
     */
    public DharmaAuthentication getSafeCopy() {
        DharmaAuthentication tmp = new DharmaAuthentication();
        if (this.principal instanceof User) {
            // need to erase the password on principal
            User _principal = (User) this.principal;
            tmp.principal = new User(_principal.getUsername(), "", _principal.isEnabled(),  //$NON-NLS-1$
                    _principal.isAccountNonExpired(), _principal.isCredentialsNonExpired(),
                    _principal.isAccountNonLocked(), _principal.getAuthorities());
        } else {
            tmp.principal = this.principal;
        }
        tmp._details.addAll(this._details);
        if (null != this.authorities) {
            tmp.authorities = new GrantedAuthority[this.authorities.length];
            System.arraycopy(this.authorities, 0, tmp.authorities, 0, this.authorities.length);
        }
        tmp.groups = this.getGroups();
        tmp.setAuthenticated(this.authenticated);
        if (null != this.authSource) {
            // shallow copy of url
            tmp.authSource = this.authSource;
        }
        tmp.sessionId = this.sessionId;
        tmp.username = this.username;
        tmp.qname = this.qname;
        tmp.realm = this.realm;

        return tmp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // tag interface
    public interface InternalAccount extends Serializable {
    }
}
