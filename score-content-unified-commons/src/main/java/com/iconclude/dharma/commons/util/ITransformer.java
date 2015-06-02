package com.iconclude.dharma.commons.util;


/**
 * given a value of type T, it transforms it into a value of type V. Just like
 * the Jakarta Commons one, the only reason not to use that one is to not create
 * a dependency between our commons and the jakarta ones. Also, this version is
 * templatized, while the jakarta commons is not.
 *
 * @param <T> source type
 * @param <V> value type.
 * @see CollectionUtils for example usage in pivoting maps and building histograms.
 */
public interface ITransformer<T, V> {
    V transform(T source);
}