/**
 *
 */
package com.iconclude.dharma.commons.util;


/**
 * transforms a value of type T into itself.
 *
 * @param <T>
 * @author statu
 */
public final class IdentityTransformer<T> implements ITransformer<T, T> {
    public T transform(T source) {
        return source;
    }
}