package com.iconclude.dharma.commons.security;

import java.util.List;

public interface IDharmaAuthentication {

    public <T> List<T> getDetails(Class<T> cls);

    public <T> T getDetail(Class<T> cls);

    public void addDetails(Object details);

    /**
     * @return the username
     */
    public String getName();

    /**
     * Returns the qualified name (domain\\username for AD, username@realm for Kerberos, etc)
     * If there is no realm (like for the internal accounts or a pure LDAP authentication)
     * the qualified name will be the same as the username.
     *
     * @return qualified name
     */
    public String getQName();

    public Object getCredentials();

    public IGroup[] getGroups();

    public String getSessionId();

    public boolean isAdministrator();

    public boolean isPromoter();

}