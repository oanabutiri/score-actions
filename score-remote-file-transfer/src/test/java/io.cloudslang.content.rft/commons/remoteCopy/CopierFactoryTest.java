package io.cloudslang.content.rft.commons.remoteCopy;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class CopierFactoryTest {

    /**
     * Tests the getExecutor method
     * Throws exception for incorrect executor name
     */
    @Test(expected = Exception.class)
    public void testGetExecutor() throws Exception {
        // incorrect executor name
        CopierFactory.getExecutor("randomName");

    }

    /**
     * Tests the CopierFactory constructor
     */
    @Test
    public void testCopierFactoryConstructor() {
        CopierFactory cf = new CopierFactory();
        assertNotNull(cf);
    }
}
