/*
 * Created on Jul 26, 2005 by xban
 */
package com.iconclude.dharma.commons.util;

import java.util.*;

public class CollectionUtils {

    public static final boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static final boolean isEmpty(Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

    public static void clear(Collection<?> coll) {
        if (coll != null) {
            coll.clear();
        }
    }

    public static void clear(Map<?, ?> m) {
        if (m != null) {
            m.clear();
        }
    }

    public static final <T> T get(List<T> l, int index, boolean wrap) {
        if (wrap) {
            if (isEmpty(l))
                return null;
            if (l.size() == 1)
                return l.get(0);
            if (index < 0) {
                index *= -1;
                index = index % l.size();
                return l.get(l.size() - index);
            } else
                return l.get(index % l.size());
        } else {
            return get(l, index);
        }
    }

    /**
     * Turns a map of (String,Object) pairs into a map of (String,Object) pair where
     * the values are in fact Strings. The signature of the returned type is so hoakey
     * because it was probably added in a haste with the move to Java 5 generics, without
     * much thinking about the implications.
     * <p/>
     * <p>Anyway, this ridiculous function converts the values by looking to see if they
     * are in turn: Collections, Maps, Arrays, then regular objects and applies the appropriate
     * StringUtils.xxxToString() methods to each. Note that Collections of Maps or Maps of Arrays
     * or combinations like that will not be converted further, because the implementation cannot
     * handle circular references.
     *
     * @param map a map of string keys and object values
     * @return a map of string keys and object values, but the values are strings (go figure).
     */
    public static Map<String, Object> getStringifiedMap(Map<String, Object> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> r = new HashMap<String, Object>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                value = "";
            } else if (value instanceof String == false) {
                if (value instanceof Collection) {
                    value = StringUtils.collectionToString((Collection<?>) value);
                } else if (value instanceof Map) {
                    value = StringUtils.mapToString((Map<?, ?>) value);
                } else {
                    // this takes care of arrays and regular objects as well.
                    value = StringUtils.arrayToString(value);
                }
            }
            r.put(entry.getKey(), value);
        }
        return r;
    }

    /**
     * a safe way to get an object from a list without throwing exceptions
     *
     * @param l     the list
     * @param index the index
     * @return the object at index, or null if the list is null or empty or
     * index is out of bounds.
     */
    public static final <T> T get(List<T> l, int index) {
        if (isEmpty(l) || index < 0 || index >= l.size()) {
            return null;
        }
        return l.get(index);
    }

    /**
     * a safe way to remove an object from a list without throwing exception.
     *
     * @param <T>   type of element stored in the list
     * @param l     the list
     * @param index the index
     * @return the object removed or null if list is null or empty or index
     * is out of bounds.
     */
    public static final <T> T remove(List<T> l, int index) {
        if (isEmpty(l) || index < 0 || index >= l.size()) {
            return null;
        }
        return l.remove(index);
    }

    /**
     * a safe way to remove the last object from a list without throwing exception.
     *
     * @param <T> type of element stored in the list
     * @param l   the list
     * @return the object removed or null if list is null or empty
     */
    public static final <T> T pop(List<T> l) {
        if (l == null || l.size() == 0) {
            return null;
        }
        return remove(l, l.size() - 1);
    }

    /**
     * removes an element by key from a map.
     *
     * @param <V> value type
     * @param map map, can be null
     * @param key key value
     * @return value removed, or nothing if map is nul or empty or does not have the key.
     */
    public static final <V> V remove(Map<?, V> map, Object key) {
        if (isEmpty(map)) {
            return null;
        }
        try {
            return map.remove(key);
        } catch (NullPointerException ex) {
            if (key != null) {
                NullPointerException npe = new NullPointerException();
                npe.initCause(ex);
                throw npe;
            }
        }
        return null;
    }

    /**
     * a safe way to get an iterator from a collection without throwing exceptions.
     *
     * @param c the collection
     * @return the iterator over the collection if the collection is not null, or
     * an iterator over an empty list if the collection is null.
     */
    public static final <T> Iterator<T> iterator(Iterable<T> c) {
        if (c == null) {
            Collection<T> empty = Collections.emptyList();
            return empty.iterator();
        }
        return c.iterator();
    }

    /**
     * a safe way to get a list iterator from a list without throwing exceptions.
     *
     * @param list    the list
     * @param startAt if next() is called it returns the element at position startAt,
     *                if previous() is called it returns the element at position startAt-1.
     * @return the list iterator over the list if the list is not null, or
     * an iterator over an empty list if the list is null.
     */
    public static final <T> ListIterator<T> listIterator(List<T> list, int startAt) {
        if (list == null) {
            List<T> empty = Collections.emptyList();
            return empty.listIterator();
        }
        return list.listIterator(startAt);
    }

    /**
     * returns a non-null iterator over they keys in a map
     *
     * @param <K> the key type
     * @param map the map, can be null
     * @return an iterator over the key set of the map, or over the empty set if
     * the map is null
     */
    public static final <K> Iterator<K> keyIterator(Map<K, ?> map) {
        if (map == null) {
            Set<K> empty = Collections.emptySet();
            return empty.iterator();
        }
        return map.keySet().iterator();
    }

    /**
     * tells if a map contains a  key.
     *
     * @param map map
     * @param key key value
     * @return true if map contains such a key, false if map is null or empty or does not contain it.
     * @throws ClassCastException   if the key is of an inappropriate type for this map (optional).
     * @throws NullPointerException if the key is <tt>null</tt> and this map does not permit <tt>null</tt> keys (optional).
     */
    public static final <K> boolean containsKey(Map<K, ?> map, K key) {
        if (isEmpty(map)) {
            return false;
        }
        try {
            return map.containsKey(key);
        } catch (NullPointerException ex) {
            if (key != null) {
                NullPointerException npe = new NullPointerException();
                npe.initCause(ex);
                throw npe;
            }
        }
        return false;
    }

    /**
     * tells if a map contains a  value.
     *
     * @param map   map
     * @param value value
     * @return true if map contains such a value, false if map is null or empty or does not contain it.
     * @throws ClassCastException   if the value is of an inappropriate type for this map (optional).
     * @throws NullPointerException if the value is <tt>null</tt> and this map does not permit <tt>null</tt> values (optional).
     */
    public static final <V> boolean containsValue(Map<?, V> map, V value) {
        if (isEmpty(map)) {
            return false;
        }
        try {
            return map.containsValue(value);
        } catch (NullPointerException ex) {
            if (value != null) {
                NullPointerException npe = new NullPointerException();
                npe.initCause(ex);
                throw npe;
            }
        }
        return false;
    }

    /**
     * returns a non null iterator over the values in a map
     *
     * @param <V> the value type
     * @param map the map, can be null
     * @return an iterator over the collection of values in the map, or over the
     * empty set if the map is null
     */
    public static final <V> Iterator<V> valuesIterator(Map<?, V> map) {
        if (map == null) {
            Set<V> empty = Collections.emptySet();
            return empty.iterator();
        }
        return map.values().iterator();
    }

    /**
     * returns a non-null iterator over the entries in a map.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map, can be null
     * @return an iterator over the entry set, or over the empty map entry set
     * if the map is null.
     */
    public static final <K, V> Iterator<Map.Entry<K, V>> entryIterator(Map<K, V> map) {
        if (map == null) {
            Map<K, V> empty = Collections.emptyMap();
            return empty.entrySet().iterator();
        }
        return map.entrySet().iterator();
    }

    /**
     * a safe way to get a reverse list iterator, positioned at the end of the
     * list, ready to call hasPrevious() on it.
     *
     * @param list the list
     * @return the list iterator over the list if the list is not null, or
     * an iterator over an empty list if the list is null.
     */
    public static final <T> ListIterator<T> reverseIterator(List<T> list) {
        return listIterator(list, size(list));
    }

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

    /**
     * a safe version of subList, which checks the indexes and adjusts them
     * properly so that no exceptions are thrown.
     *
     * @param list      original list, if null or empty, it gets returned back.
     * @param fromIndex start index, if it's outside the list bounds, the original
     *                  list gets returned back.
     * @param toIndex   end index, if it's greater than the list size, it gets adjusted
     *                  to the list size.
     * @return the sublist.
     */
    public static <T> List<T> subList(List<T> list, int fromIndex, int toIndex) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        if (fromIndex < 0 || fromIndex >= list.size()) {
            return list;
        }
        if (toIndex > list.size()) {
            toIndex = list.size();
        }
        return list.subList(fromIndex, toIndex);
    }

    /**
     * turns a map into an array of Pair[K,V] objects
     */
    public static <K, V> Pair<K, V>[] map2pairs(Map<K, V> m) {
        if (m == null) {
            return null;
        }
        Set<Map.Entry<K, V>> entries = m.entrySet();
        // we're the only ones putting stuff in the array and we know it's only
        // pairs of K,V so we are type safe here.
        @SuppressWarnings("unchecked") Pair<K, V>[] result = new Pair[entries.size()];
        int i = 0;
        for (Map.Entry<K, V> entry : entries) {
            result[i++] = new Pair<K, V>(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * turns an array of Pair[K,V] into a map
     */
    public static <K, V> Map<K, V> pairs2map(Pair<K, V>[] p) {
        return pairs2map(p, null);
    }

    /**
     * populates a map with the values in the array of Pair[K,V] object.
     *
     * @param p array of pairs, can be null.
     * @param m the map to populate, if null, a HashMap will be created
     * @return the map passed in or the one created.
     */
    public static <K, V> Map<K, V> pairs2map(Pair<K, V>[] p, Map<K, V> m) {
        if (p == null) {
            return m;
        }
        if (m == null) {
            m = new HashMap<K, V>(p.length);
        }
        for (Pair<K, V> elem : p) {
            if (elem != null && elem.getFirst() != null) {
                m.put(elem.getFirst(), elem.getSecond());
            }
        }
        return m;
    }

    /**
     * safe way to get a collection size.
     *
     * @param c collection, can be null
     * @return 0 if collection null or empty, size otherwise.
     */
    public static final int size(Collection<?> c) {
        return (c == null ? 0 : c.size());
    }

    /**
     * safe way to get a map size.
     *
     * @param m a map, can be null
     * @return 0 if map is null or empty, size otherwise.
     */
    public static final int size(Map<?, ?> m) {
        return (m == null ? 0 : m.size());
    }

    public static final <T> int indexOf(List<T> l, T candidate) {
        if (isEmpty(l)) {
            return -1;
        }
        return l.indexOf(candidate);
    }

    /**
     * linear search for a candidate inside a sorted set.
     */
    public static final <T> int indexOf(SortedSet<? extends Comparable<? super T>> set, T candidate) {
        if (isEmpty(set)) {
            return -1;
        }
        int index = 0;
        for (Iterator<? extends Comparable<? super T>> it = set.iterator(); it.hasNext(); ) {
            Comparable<? super T> element = it.next();
            if (candidate == null && element == null) {
                return index;
            }
            if (element != null && element.compareTo(candidate) == 0) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * finds duplicate values in a map.
     *
     * @param <K> key type
     * @param <V> value type
     * @param map the map
     * @return a pivoted map where the keys are the values which have duplicates,
     * and the values are the keys which have the same value.
     */
    public static <K, V> Map<V, Set<K>> findDuplicateValues(Map<K, V> map) {
        return findDuplicateValues(map, new IdentityTransformer<V>());
    }

    /**
     * finds duplicate values in a map.
     *
     * @param <K>   they key type
     * @param <V>   the value type
     * @param <W>   the type of property in the value type which we want to find duplicates for
     *              (e.g. V is UniqueNamedModelObject and W is UUID because we want to see duplicated UUIDs)
     * @param map   the source map
     * @param xform an object which returns a W given a V
     * @return a pivoted map where the keys are of type W and the values are a set of
     * keys which denote values having the same property value (of type W).
     */
    public static <K, V, W> Map<W, Set<K>> findDuplicateValues(Map<K, V> map, ITransformer<V, W> xform) {
        if (isEmpty(map) || xform == null) {
            return Collections.emptyMap();
        }

        // first get a histogram based on the value.
        Map<W, Set<K>> result = buildKeyClusters(map, xform);

        // then eliminate those clusters of size less than 2
        for (Iterator<Map.Entry<W, Set<K>>> it = result.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<W, Set<K>> entry = it.next();
            if (size(entry.getValue()) < 2) {
                it.remove();
            }
        }

        return result;
    }

    /**
     * builds a key histogram - it's like a SQL statement doing this:
     * <code>SELECT value, COALESCE(key, ',') FROM map GROUP BY value</code>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the source map
     * @return a pivoted map where keys are values and values are sets of keys
     */
    public static <K, V> Map<V, Set<K>> buildKeyClusters(Map<K, V> map) {
        if (isEmpty(map)) {
            return Collections.emptyMap();
        }
        return buildKeyClusters(map, new IdentityTransformer<V>());
    }

    /**
     * builds a key histogram - it's like a SQL statement doing this:
     * <code>SELECT value, COALESCE(key, ',') FROM map GROUP BY value</code>
     *
     * @param <K>   the key type
     * @param <V>   the value type
     * @param <W>   the property type inside the value that we want the histogram to be built for
     * @param map   the initial map
     * @param xform the object that can dig out a W from a V
     * @return a pivoted map where the keys are the W values and the values are the set of
     * keys which exhibit such a value.
     */
    public static <K, V, W> Map<W, Set<K>> buildKeyClusters(Map<K, V> map, ITransformer<V, W> xform) {
        if (isEmpty(map) || xform == null) {
            return Collections.emptyMap();
        }

        Map<W, Set<K>> histogram = new HashMap<W, Set<K>>();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            W value = xform.transform(entry.getValue());
            if (value != null) {
                Set<K> cluster = histogram.get(value);
                if (cluster == null) {
                    cluster = new HashSet<K>();
                    histogram.put(value, cluster);
                }
                cluster.add(entry.getKey());
            }
        }

        return histogram;
    }

    /**
     * given a non-empty collection of T's, it applies the selector predicate
     * to each element and if the evaluation returns true, it places the element
     * in the destination collection.
     *
     * @param <T>         the collection element type
     * @param source      the source collection, if null or empty then nothing happens
     * @param selector    the selection predicate, if null then nothing happens
     * @param destination the destination collection, if null then nothing happens.
     * @return the destination collection.
     */
    public static <T> Collection<T> select(Collection<T> source, IPredicate<T> selector, Collection<T> destination) {
        if (isEmpty(source) || destination == null || selector == null) {
            return destination;
        }
        for (T element : source) {
            if (selector.evaluate(element)) {
                destination.add(element);
            }
        }
        return destination;
    }

    /**
     * given a collection, finds the first occurrence of an element for which
     * the predicate evaluates to true.
     *
     * @param <T>      type of elements in collection
     * @param source   the collection
     * @param selector the predicate
     * @return the first element found, or null if collection is empty or
     * predicate is null or no element satisfies the predicate.
     */
    public static <T> T findFirst(Collection<T> source, IPredicate<T> selector) {
        if (!isEmpty(source) && selector != null) {
            for (T element : source) {
                if (selector.evaluate(element)) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * given a non-empty collection of T's, it applies the transformer functor
     * to each element and deposits the result in the destination collection.
     * Nulls are not allowed in the destination collection.
     *
     * @param <T>         the collection element type
     * @param source      the source collection, if null or empty then nothing happens
     * @param destination the destination collection, if null then nothing happens.
     * @return the destination collection.
     */
    public static <T, V> Collection<V> transform(Collection<T> source, ITransformer<T, V> transformer,
                                                 Collection<V> destination) {
        return transform(source, transformer, destination, false);
    }

    /**
     * given a non-empty collection of T's, it applies the transformer functor
     * to each element and deposits the result in the destination collection.
     *
     * @param <T>         the collection element type
     * @param source      the source collection, if null or empty then nothing happens
     * @param destination the destination collection, if null then nothing happens.
     * @param allowNulls  if true, nulls are allowed in the destination collection.
     * @return the destination collection.
     */
    public static <T, V> Collection<V> transform(Collection<T> source, ITransformer<T, V> transformer,
                                                 Collection<V> destination, boolean allowNulls) {
        if (isEmpty(source) || destination == null || transformer == null) {
            return destination;
        }
        for (T element : source) {
            V transformed = transformer.transform(element);
            if (allowNulls || transformed != null) {
                destination.add(transformed);
            }
        }
        return destination;
    }

    /**
     * boxes an array of integers into a collection of Integers
     *
     * @param src source array, if null or empty nothing happens
     * @return an ArrayList containing the values.
     */
    public static List<Integer> toList(int[] src) {
        return toList(src, null);
    }

    /**
     * transforms an array of integers into a collection of Integers
     *
     * @param src  source array, if null or empty nothing happens
     * @param dest destination collection, if null an ArrayList gets created
     * @return destination (may be created by this function).
     */
    public static List<Integer> toList(int[] src, List<Integer> dest) {
        if (ArrayUtils.isEmpty(src)) {
            return dest;
        }
        if (dest == null) {
            dest = new ArrayList<Integer>(src.length);
        }
        for (int i : src) {
            dest.add(i);
        }
        return dest;
    }

    /**
     * transforms an array into a list, with no exceptions thrown.
     *
     * @param <T>        type of the array
     * @param src        array to transform, if empty, an empty collection is returned
     * @param dest       destination list, if null a new ArrayList is created.
     * @param allowNulls if true, nulls from the source list can go in the
     *                   destination list, otherwise they get skipped.
     * @return a list of T's
     */
    public static <T> List<T> toList(T[] src, List<T> dest, boolean allowNulls) {
        if (ArrayUtils.isEmpty(src)) {
            return new ArrayList<T>(0);
        }
        if (dest == null) {
            dest = new ArrayList<T>(src.length);
        }
        for (int i = 0; i < src.length; i++) {
            if (allowNulls || src[i] != null) {
                dest.add(src[i]);
            }
        }
        return dest;
    }

    /**
     * makes an ArrayList out of an Array. Similar to Arrays.asList(), but does
     * not get upset about nulls and such.
     *
     * @param <T>
     * @param src
     * @return
     */
    public static <T> List<T> toList(T... src) {
        return toList(src, null, true);
    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static double[] unbox(Collection<Double> coll) {
//        if (coll == null) {
//            return null;
//        }
//        double[] result = new double[coll.size()];
//        int index = 0;
//        for (Double b : coll) {
//            result[index++] = b.doubleValue();
//        }
//        return result;
//    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static float[] unbox(Collection<Float> coll) {
//        if (coll == null) {
//            return null;
//        }
//        float[] result = new float[coll.size()];
//        int index = 0;
//        for (Float b : coll) {
//            result[index++] = b.floatValue();
//        }
//        return result;
//    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static long[] unbox(Collection<Long> coll) {
//        if (coll == null) {
//            return null;
//        }
//        long[] result = new long[coll.size()];
//        int index = 0;
//        for (Long b : coll) {
//            result[index++] = b.longValue();
//        }
//        return result;
//    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static int[] unbox(Collection<Integer> coll) {
//        if (coll == null) {
//            return null;
//        }
//        int[] result = new int[coll.size()];
//        int index = 0;
//        for (Integer b : coll) {
//            result[index++] = b.intValue();
//        }
//        return result;
//    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static short[] unbox(Collection<Short> coll) {
//        if (coll == null) {
//            return null;
//        }
//        short[] result = new short[coll.size()];
//        int index = 0;
//        for (Short b : coll) {
//            result[index++] = b.shortValue();
//        }
//        return result;
//    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static char[] unbox(Collection<Character> coll) {
//        if (coll == null) {
//            return null;
//        }
//        char[] result = new char[coll.size()];
//        int index = 0;
//        for (Character b : coll) {
//            result[index++] = b.charValue();
//        }
//        return result;
//    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static byte[] unbox(Collection<Byte> coll) {
//        if (coll == null) {
//            return null;
//        }
//        byte[] result = new byte[coll.size()];
//        int index = 0;
//        for (Byte b : coll) {
//            result[index++] = b.byteValue();
//        }
//        return result;
//    }

    /** unboxes collection into array of primitive type.
     *
     * @param coll the collection of objects
     * @return array of primitive types: null array if coll is null, empty array
     * if coll is empty, unboxed array otherwise.
     */
//    public static boolean[] unbox(Collection<Boolean> coll) {
//        if (coll == null) {
//            return null;
//        }
//        boolean[] result = new boolean[coll.size()];
//        int index = 0;
//        for (Boolean b : coll) {
//            result[index++] = b.booleanValue();
//        }
//        return result;
//    }

    /**
     * typing saver.
     *
     * @param coll collection of whatever
     * @return null if coll is null, the result of <code>coll.toArray(new Object[coll.size()]</code> otherwise.
     */
    public static Object[] toArray(Collection<?> coll) {
        if (coll == null) {
            return null;
        }
        return coll.toArray(new Object[coll.size()]);
    }

    /**
     * converts a collection to an array of typed values. Unlike toArray() with no type
     * args, this one makes an array of the type T[] as opposed to an array of Object[].
     *
     * @param <T>
     * @param coll
     * @param elementType
     * @return
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public static <T> T[] toArray(Collection<T> coll, Class<T> elementType) {
        if (coll == null || elementType == null) {
            return null;
        }
        // stupid java generics, you cannot create generic arrays...
        T[] array = (T[]) java.lang.reflect.Array.newInstance(elementType, coll.size());
        return coll.toArray(array);
    }

    /**
     * adds the elements of an array to a collection, which avoids calling
     * <code>Arrays.asList(arr)</code>.
     *
     * @param <X>  the type of the collection
     * @param <T>  the type of the array, must assignable to X
     * @param coll a collection of X values, if null nothing happens.
     * @param arr  an array of types assignable to X, if null or empty nothing happens.
     */
    public static <X, T extends X> void addAll(Collection<X> coll, T[] arr) {
        if (ArrayUtils.isEmpty(arr)) {
            return;
        }
        if (coll == null) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            coll.add(arr[i]);
        }
    }

    /**
     * sorts a list without complaints if the list is null
     */
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        if (isEmpty(list)) {
            return;
        }
        Collections.sort(list);
    }

    /**
     * sorts a list without complaints if the list is null or comparator is null
     */
    public static <T> void sort(List<T> list, Comparator<? super T> c) {
        if (isEmpty(list) || c == null) {
            return;
        }
        Collections.sort(list, c);
    }

    /**
     * returns a comparator which allows nulls. Care must be taken when this thing
     * is used as some of the invariants may not hold too well for nulls (the invariant
     * should be that <code>if o1.equals(o2) then compare(o1,o2) == 0</code>, but
     * for nulls this is not all that well defined).
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable> Comparator<T> defaultComparator() {
        return new Comparator<T>() {
            public int compare(T o1, T o2) {
                if (o1 == null) {
                    return (o2 == null ? 0 : -1);
                }
                return (o2 == null ? 1 : o1.compareTo(o2));
            }

        };
    }

    /**
     * creates a hash index from a collection, by applying the indexing transformer
     * to each of the values in the collection.
     *
     * @param <K>     type of the key
     * @param <V>     type of the value
     * @param values  collection of values
     * @param indexer a transforming functor, takes a value and produces a key. Value is placed in the
     *                hash index only if the key is not null.
     * @return a hash map. If input collection is null or empty, an empty map is returned
     * @throws IllegalArgumentException if indexer is null.
     */
    public static <K, V> HashMap<K, V> createHashIndex(Collection<V> values, ITransformer<V, K> indexer) {
        if (indexer == null) {
            throw new IllegalArgumentException("null indexer"); //$NON-NLS-1$
        }
        if (isEmpty(values)) {
            return new HashMap<K, V>();
        }
        HashMap<K, V> index = new HashMap<K, V>(values.size());
        for (V value : values) {
            K key = indexer.transform(value);
            if (key != null) {
                index.put(key, value);
            }
        }
        return index;
    }

    public static <K, V> Map<K, V> filterMap(Map<K, V> attrs, K[] allowedKeys) {
        Map<K, V> keepers = new HashMap<K, V>();
        for (K key : allowedKeys) {
            V v = attrs.get(key);
            if (v != null) {
                keepers.put(key, v);
            }
        }
        attrs.clear();
        for (Map.Entry<K, V> entry : keepers.entrySet()) {
            attrs.put(entry.getKey(), entry.getValue());
        }

        return attrs;
    }

    /**
     * Returns the last element of the list. It tolerates null and empty lists
     * and allows to look at any list as if it were a stack.
     *
     * @return the object at the top of this stack. If stack is empty or null, a null is returned.
     */
    public static <V> V peek(List<V> list) {
        int len = size(list);
        if (len == 0) {
            return null;
        }
        // optimize for LinkedList
        if (list instanceof LinkedList) {
            return ((LinkedList<V>) list).getLast();
        }
        // the other standard lists (Stack, Vector and ArrayList) are all RandomAccess so they're cool.
        return list.get(len - 1);
    }

    public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> c) {
        return c == null ? null : Collections.unmodifiableCollection(c);
    }

    public static <T> Set<T> unmodifiableSet(Set<? extends T> s) {
        return s == null ? null : Collections.unmodifiableSet(s);
    }

    public static <T> SortedSet<T> unmodifiableSortedSet(SortedSet<T> s) {
        return s == null ? null : Collections.unmodifiableSortedSet(s);
    }

    public static <T> List<T> unmodifiableList(List<? extends T> list) {
        return list == null ? null : Collections.unmodifiableList(list);
    }

    public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> m) {
        return m == null ? null : Collections.unmodifiableMap(m);
    }

    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> m) {
        return m == null ? null : Collections.unmodifiableSortedMap(m);
    }

    public static <K, V> Set<K> keySet(Map<K, V> m) {
        if (m == null || m.isEmpty()) {
            return Collections.emptySet();
        }
        return m.keySet();
    }

    public static <K, V> Collection<V> values(Map<K, V> m) {
        if (m == null || m.isEmpty()) {
            return Collections.emptyList();
        }
        return m.values();
    }

}
