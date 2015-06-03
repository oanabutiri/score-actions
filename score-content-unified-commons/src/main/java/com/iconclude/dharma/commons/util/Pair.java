/*
 * Created on Oct 18, 2005 by xban
 */
package com.iconclude.dharma.commons.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

// Should be Pair<F extends Serializable, S extends Serializable> but that would
// break tons of s#!t.
public class Pair<F, S> implements Serializable {

    private static final long serialVersionUID = 1714561839873465953L;

    private F first;

    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Pair) {
            Pair other = (Pair) obj;
            return new EqualsBuilder().append(this.first, other.first).append(
                    this.second, other.second).isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.first).append(this.second)
                .toHashCode();
    }

    public String toString() {
        return new StringBuilder().append("Pair@").append(
                System.identityHashCode(this)).append("{first=").append(first)
                .append(", second=").append(second).append('}').toString();
    }
}
