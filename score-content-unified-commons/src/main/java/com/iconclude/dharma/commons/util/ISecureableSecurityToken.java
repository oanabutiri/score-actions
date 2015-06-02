package com.iconclude.dharma.commons.util;

import com.iconclude.dharma.commons.security.DharmaAuthentication;

public interface ISecureableSecurityToken {
    public Object getSecurityValue(String _propName, DharmaAuthentication auth);

}
