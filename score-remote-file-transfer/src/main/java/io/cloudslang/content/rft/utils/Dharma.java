/*
 * Created on Aug 12, 2005 by xban
 */
package io.cloudslang.content.rft.utils;

import java.io.Closeable;

/**
 * general utility methods which don't fit anywhere else.
 *
 * @author xban
 */
public final class Dharma {

    /**
     * closes any IO object that supports the Closeable interface.
     *
     * @param <T>   the object type
     * @param ioObj the object reference, it can be null.
     */
    public static final <T extends Closeable> void close(T ioObj) {
        if (ioObj != null) {
            try {
                ioObj.close();
            } catch (Throwable ignore) {
            }
        }
    }

    public static String msg(String key) {
        return LocalizedStringLookup.lookupMsg(key);
    }
}
