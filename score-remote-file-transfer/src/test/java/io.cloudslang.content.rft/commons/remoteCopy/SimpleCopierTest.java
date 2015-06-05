package io.cloudslang.content.rft.commons.remoteCopy;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class SimpleCopierTest {
    private SimpleCopier sc;

    @Before
    public void setUp() throws Exception {
        sc = new SimpleCopier() {
            @Override
            protected IReader getFile(String source) throws Exception {
                return null;
            }

            @Override
            protected void getFile(String source, File destination) throws Exception {
            }

            @Override
            protected void putFile(IReader sourceFile, String destination) throws Exception {
            }

            @Override
            public String getProtocolName() {
                return null;
            }
        };
    }

    /**
     * Test the timeout setter
     */
    @Test
    public void testTimeout() {
        int timeout = 5;
        sc.setTimeout(timeout);
        assertEquals(5, sc.timeout);
    }

    /**
     * Tests the credentials setter
     *
     * @throws Exception
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetCredentials() throws Exception {
        // incorrect credentials -> does not allow credentials to be set
        sc.setCredentials("host", 22, "userName", "password");
    }

    /**
     * Tests the credentials overloaded setter
     *
     * @throws Exception
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetCredentialsOverloaded() throws Exception {
        // incorrect credentials -> does not allow credentials to be set
        sc.setCredentials("host", 22, "userName", "password", "privateKey");
    }

    /**
     * Tests the getCustomArgument getter
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetCustomArg() {
        // unsupported getter
        sc.getCustomArgument(ICopier.simpleArgument.type);
    }
}