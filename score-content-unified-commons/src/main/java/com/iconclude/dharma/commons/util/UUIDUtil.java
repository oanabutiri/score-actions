/*
 * Created on Jan 31, 2006
 *
 */
package com.iconclude.dharma.commons.util;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

public class UUIDUtil {
    public static final UUID getNewUUID() {
        return Generators.timeBasedGenerator().generate();
    }

}
