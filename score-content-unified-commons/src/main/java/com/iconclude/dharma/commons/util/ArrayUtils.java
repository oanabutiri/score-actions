package com.iconclude.dharma.commons.util;

import com.google.common.base.Preconditions;

import java.lang.reflect.Array;
import java.util.Iterator;

public class ArrayUtils {

    public static final boolean isArray(Object a) {
        return (a != null && a.getClass().isArray());
    }

    public static final Class<?> getComponentType(Object a) {
        if (a == null) {
            return null;
        }
        return a.getClass().getComponentType();
    }

    public static final int size(Object array) {
        if (array == null || !array.getClass().isArray()) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static final boolean isEmpty(Object ar) {
        return size(ar) == 0;
    }

    public static final boolean isEmpty(Object[] ar) {
        return ar == null || ar.length == 0;
    }

    @SuppressWarnings("unchecked")
    public static final <T, V> V[] transform(T[] input, ITransformer<T, V> xfrm, Class<? extends V[]> newType) {
        if (input == null) {
            return null;
        }

        Preconditions.checkNotNull(xfrm, "null transformer");
        Preconditions.checkNotNull(newType, "new type was null");

        V[] result = (((Object) newType == (Object) Object[].class)
                ? (V[]) new Object[input.length]
                : (V[]) Array.newInstance(newType.getComponentType(), input.length));

        for (int i = 0; i < input.length; i++) {
            result[i] = xfrm.transform(input[i]);
        }

        return result;
    }

    /**
     * a safe way to get an object from an array without throwing exceptions
     *
     * @param <T>      type of array
     * @param arrayOfT the array
     * @param index    the index
     * @return null if array is null or empty or index is out of bounds, element
     * at index otherwise.
     */
    public static final <T> T getElement(T[] arrayOfT, int index) {
        if (arrayOfT == null || arrayOfT.length == 0 || index < 0 || index >= arrayOfT.length) {
            return null;
        }
        return arrayOfT[index];
    }

    /**
     * returns an element inside an array of objects, does not throw any exceptions.
     *
     * @param array the array, can be null
     * @param index the index, can be out of bounds
     * @return the element at index. If array is not an array, but the index is 0,
     * it returns the element itself. If the array is empty, it returns null. If
     * the index is out of bounds, it returns null.
     */
    public static final Object get(Object array, int index) {
        if (array == null) {
            return null;
        }
        if (array.getClass().isArray()) {
            int len = Array.getLength(array);
            if (index >= 0 && index < len) {
                return Array.get(array, index);
            }
        } else if (index == 0) {
            return array;
        }
        return null;
    }

    /**
     * sets a value in an array, similar to <code>Array.set(Object, int, Object)<code>
     * but without throwing any exceptions.
     *
     * @param array the array, can be null
     * @param index the index in the array, can be out of bounds
     * @param value the value to set at index
     * @return true if the element was successfully set. It returns false
     * if array is null or not an array, or if the index is out of bounds.
     */
    public static final boolean set(Object array, int index, Object value) {
        if (array == null) {
            return false;
        }
        if (!array.getClass().isArray()) {
            return false;
        }
        int len = Array.getLength(array);
        if (index >= 0 && index < len) {
            Array.set(array, index, value);
            return true;
        }
        return false;
    }

    /**
     * produces an iterator over an array. Useful when interchanging collections
     * and arrays, iteration stays the same.
     *
     * @param <T>   type
     * @param array the array, can be null.
     * @return an iterator, never null.
     * @throws IllegalArgumentException if array is not an array object.
     */
    public static final <T> Iterator<T> iterator(Object array) {
        return new ArrayIterator<T>(array);
    }

    /**
     * looks at an object and returns the array structure (if it's array), for
     * instance "Array[Array[Array[String]]]" if you have a String[][][] array.
     *
     * @param array the assumed array.
     * @return the structure representation; if array is null, it returns the string "null",
     * if array is not an array it returns the type of the object (for instance String).
     */
    public static final String showComponents(Object array) {
        if (!isArray(array)) {
            if (array == null) {
                return "null"; //$NON-NLS-1$
            }
            return array.getClass().getName();
        }
        StringBuilder buf = new StringBuilder();
        doShowComponents(array.getClass(), buf);
        return buf.toString();
    }

    private static void doShowComponents(Class<?> arrayClass, StringBuilder buf) {
        if (arrayClass == null) {
            return;
        }
        Class<?> componentType = arrayClass.getComponentType();
        if (componentType == null) {
            buf.append(arrayClass.getName());
            return;
        }
        buf.append("Array["); //$NON-NLS-1$
        doShowComponents(componentType, buf);
        buf.append("]"); //$NON-NLS-1$
    }

    public static void bzero(byte[] bz) {
        if (bz == null) {
            return;
        }
        for (int i = 0; i < bz.length; i++) {
            bz[i] = 0;
        }
    }

    public static void czero(char[] cz) {
        if (cz == null) {
            return;
        }
        for (int i = 0; i < cz.length; i++) {
            cz[i] = 0;
        }
    }

}
