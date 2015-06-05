package io.cloudslang.content.rft.commons.remoteCopy;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: bancl
 * Date: 4/24/2015
 */
public class SmbCopierTest {

    private ICopier smbCopier;

    @Before
    public void setUp() throws Exception {
        smbCopier = CopierFactory.getExecutor(CopierFactory.copiers.smb);
    }

    @Test
    public void getProtocolName() {
        assertEquals("smb", smbCopier.getProtocolName());
    }

    @Test
    public void testCopierFactory() throws Exception {
        ICopier toTest = CopierFactory.getExecutor("smb");
        assertNotNull(toTest);
        assertTrue(toTest instanceof SmbCopier);
    }

    @Test
    public void getFormattedPath() throws Exception {
        String result = Whitebox.invokeMethod(smbCopier, "getFormattedPath", "C:\\folder1\\folder2\\file.tmp");
        assertEquals("C$/folder1/folder2/file.tmp", result);
    }

    @Test
    public void inputStreamToOutputStream() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Whitebox.invokeMethod(smbCopier, "inputStreamToOutputStream", new ByteArrayInputStream("testString".getBytes()),
                outputStream);
        assertEquals("testString", outputStream.toString());
    }
}
