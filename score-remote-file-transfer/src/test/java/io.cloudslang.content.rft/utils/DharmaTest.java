package io.cloudslang.content.rft.utils;

import com.iconclude.dharma.commons.util.Dharma;
import junit.framework.TestCase;

public class DharmaTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DharmaTest.class);
    }


    public void testTraceContainsException() {
        Exception e1 = new Exception();
        assertTrue(com.iconclude.dharma.commons.util.Dharma.traceContainsException(e1, Throwable.class));
        assertTrue(com.iconclude.dharma.commons.util.Dharma.traceContainsException(e1, Exception.class));
        e1 = new Exception(new IllegalStateException());
        assertTrue(com.iconclude.dharma.commons.util.Dharma.traceContainsException(e1, Throwable.class));
        assertTrue(com.iconclude.dharma.commons.util.Dharma.traceContainsException(e1, Exception.class));
        assertTrue(com.iconclude.dharma.commons.util.Dharma.traceContainsException(e1, IllegalStateException.class));
    }

    public void testGetMessage() {
        Exception e1 = new Exception("Exception 1");
        assertEquals("Exception 1", com.iconclude.dharma.commons.util.Dharma.getMessage(e1));
        e1 = new Exception("Exception 1", new Exception("Exception 2"));
        assertEquals("Exception 2", Dharma.getMessage(e1));
    }
}
