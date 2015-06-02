/*
 * Created on Feb 17, 2006
 *
 */
package com.iconclude.dharma.commons.util;

import com.iconclude.dharma.commons.security.DharmaAuthentication;

/**
 * This class is used when the bean utils cannot be used (for different reasons)
 * to get an object's properties
 */
public interface IEntityResolver {

    /**
     * @param resolvable - the resolvable having the property
     * @return the stringified property value
     */
    Object resolve(IEntityResolvable resolvable, DharmaAuthentication auth);
}
