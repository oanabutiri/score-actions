/*
 * Created on Jul 26, 2005 by xban
 */
package com.iconclude.dharma.commons.util;

import java.util.*;

public class CollectionUtils {

    /**
     * a safe way to get an object from a map without throwing exceptions.
     *
     * @param m   the map
     * @param key the key
     * @return the value for the key as stored in the map, or null if the map is null,
     * the key is null, or the map is empty.
     */
    public static final <K, V> V get(Map<K, V> m, K key) {
        if (m == null || key == null) {
            return null;
        }
        return m.get(key);
    }
}
