/*
 * Created on Feb 17, 2006
 *
 */
package com.iconclude.dharma.commons.util;

import com.iconclude.dharma.commons.security.DharmaAuthentication;
import com.iconclude.dharma.commons.security.ISecurityToken;
import com.iconclude.dharma.commons.security.SecurityTokenHelper;

public class IdentityEntityResolver implements IEntityResolver {

    private String _propName;

    public IdentityEntityResolver(String propName) {
        if (propName == null)
            throw new IllegalArgumentException(Dharma.msg("Dharma.util.IdentityEntityResolver.NullNameError")); //$NON-NLS-1$

        _propName = propName;
    }


    public Object resolve(IEntityResolvable resolvable, DharmaAuthentication auth) {
        if (null == resolvable)
            return null;
        if (resolvable instanceof ISecurityToken) {
            ISecurityToken st = (ISecurityToken) resolvable;
            return SecurityTokenHelper.getSecurityValue(st, _propName);
        }
        if (resolvable instanceof ISecureableSecurityToken) {
            ISecureableSecurityToken st = (ISecureableSecurityToken) resolvable;
            return st.getSecurityValue(_propName, auth);
        } else return null;
    }

}
