/*
 * Copyright (c) iConclude 2005, 2006
 * All rights reserved.
 */
package com.iconclude.webservices.extensions.java.types;

import java.util.Iterator;


@Deprecated
public class Map {

    private MapEntry[] entries = new MapEntry[0];

    public Map() { }

    public Map(MapEntry[] entries) {
        if (entries != null) {
            this.entries = entries;
        } else {
            this.entries = new MapEntry[0];
        }
    }

    public Map(java.util.Map javaMap) {
        entries = new MapEntry[javaMap.size()];
        int counter = 0;
        for (Iterator i = javaMap.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            entries[counter++] = new MapEntry(key, javaMap.get(key));
        }
    }

    public Object add(String name, Object value) {
        MapEntry entry = null;
        for (int i = 0; i < this.entries.length; i++) {
            if (this.entries[i].getName().equals(name)) {
                entry = this.entries[i];
                this.entries[i] = new MapEntry(name, value);
                break;
            }
        }
        if (null == entry) {
            MapEntry[] entries = new MapEntry[this.entries.length + 1];
            for (int i = 0; i < this.entries.length; i++) {
                entries[i] = this.entries[i];
                this.entries[i] = null;
            }
            entries[this.entries.length] = new MapEntry(name, value);
            this.entries = entries;
        }
        return (null == entry) ? null : entry.getValue();
    }

    public Object map(String name) {
        MapEntry entry = null;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getName().equals(name)) {
                entry = entries[i];
                break;
            }
        }
        return (null == entry) ? null : entry.getValue();
    }

    public Object remove(String name) {
        MapEntry entry = null;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getName().equals(name)) {
                entry = entries[i];
                MapEntry[] _entries = new MapEntry[entries.length - 1];
                System.arraycopy(entries, 0, _entries, 0, i);
                System.arraycopy(entries, i + 1, _entries, i, (entries.length - 1) - i);
                clear();
                entries = _entries;
                break;
            }
        }
        return (null == entry) ? null : entry.getValue();
    }

    public void clear() {
        for (int i = 0; i < entries.length; i++) {
            entries[i] = null;
        }
        entries = new MapEntry[0];
    }

    public MapEntry[] getEntries() {
        return entries;
    }

    public void setEntries(MapEntry[] entries) {
        this.entries = entries;
    }

}