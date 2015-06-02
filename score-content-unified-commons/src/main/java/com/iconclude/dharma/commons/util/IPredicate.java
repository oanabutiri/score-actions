package com.iconclude.dharma.commons.util;

/**
 * given an object of type T, it evaluates it and returns a boolean value. This
 * is useful in building filters for collections, etc.
 * <p/>
 * <p>This is the same as the jakarta commons Predicate, but it's templatized
 * and it's used by our collection utils and such in order not to create a
 * dependency with jakarta commons.
 *
 * @param <T>
 * @author statu
 */
public interface IPredicate<T> {
    boolean evaluate(T source);
}
