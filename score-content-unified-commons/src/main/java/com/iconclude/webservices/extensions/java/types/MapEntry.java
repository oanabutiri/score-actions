/*
 * Copyright (c) iConclude 2005, 2006
 * All rights reserved.
 */
package com.iconclude.webservices.extensions.java.types;

@Deprecated
public class MapEntry {

    private String name;
    private Object value;

    public MapEntry() { }

    public MapEntry(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

}
