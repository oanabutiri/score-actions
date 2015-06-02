package com.iconclude.dharma.commons.util;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<E> implements Iterator<E> {

    private Object _array;
    private int _index = 0;
    private int _len = 0;

    public ArrayIterator(Object array) {
        if (array != null) {
            if (!array.getClass().isArray()) {
                throw new IllegalArgumentException(Dharma.msg("Dharma.util.ArrayIterator.NonArrayError")); //$NON-NLS-1$
            }
            _array = array;
            _len = Array.getLength(_array);
        }
    }

    public boolean hasNext() {
        if (_array == null) {
            return false;
        }
        return (_index < _len);
    }

    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException(Dharma.msg("Dharma.util.ArrayIterator.NoSuchElementError")); //$NON-NLS-1$
        }
        return (E) Array.get(_array, _index++);
    }

    public void remove() {
        throw new UnsupportedOperationException(Dharma.msg("Dharma.util.ArrayIterator.RemoveElementError")); //$NON-NLS-1$
    }

}
