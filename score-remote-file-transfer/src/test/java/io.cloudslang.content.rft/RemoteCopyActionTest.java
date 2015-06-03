package io.cloudslang.content.rft;

import io.cloudslang.content.rft.service.RemoteCopy;
import io.cloudslang.content.rft.utils.RemoteCopyInputs;
import io.cloudslang.content.rft.utils.RemoteCopyOutputs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Created by butiri on 6/2/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RemoteCopy.class})
public class RemoteCopyActionTest {

    private static final String SOURCE_PATH = System.getProperty("java.io.tmpdir") + "\\testFile1";
    private static final String DEST_PATH = System.getProperty("java.io.tmpdir") + "\\testFile2";

    private RemoteCopy toTest;

    @Before
    public void setUp() throws IOException {
        toTest = new RemoteCopy();
        File f = new File(SOURCE_PATH);
        f.createNewFile();
    }

    @After
    public void cleanUp() {
        File f = new File(SOURCE_PATH);
        f.delete();
        f = new File(DEST_PATH);
        f.delete();
    }

    /**
     * This test makes a coppy to a file from local temp using execute method from RemoteCopy.class.
     * The copy will be removed at cleanUp.
     * @throws Exception
     */
    @Test
    public void testExec() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("local");
        remoteCopyInputs.setDestinationProtocol("local");
        remoteCopyInputs.setSourcePath(SOURCE_PATH);
        remoteCopyInputs.setSourceHost("localhost");
        remoteCopyInputs.setDestinationHost("localhost");
        remoteCopyInputs.setDestinationPath(DEST_PATH);

        Map<String, String> result = toTest.execute(remoteCopyInputs);

        assertEquals(RemoteCopyOutputs.SUCCESS_RETURN_CODE, result.get("returnCode"));
        assertNull(result.get("exception"));
        assertTrue(result.get(RemoteCopyOutputs.RETURN_RESULT).equals("Copy completed successfully"));
        assertTrue(new File(DEST_PATH).exists());
    }

    /**
     * Tests exec method with a Unsuported protocol added to sourceProtocol paramether.
     * The exec method should not throw an exception. The exception should be added in result.
     * @throws Exception
     */
    @Test
    public void testExecWithUnsuportedSourceProtocol() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("someRandomProtocol");

        Map<String, String> result = toTest.execute(remoteCopyInputs);
        assertTrue(result.get("exception").contains("Protocol somerandomprotocol not supported!"));
        assertTrue(result.get(RemoteCopyOutputs.RETURN_RESULT).equals("Protocol somerandomprotocol not supported!"));
    }

    /**
     * Tests exec method with a Unsuported protocol added to destinationProtocol paramether.
     * The exec method should not throw an exception. The exception should be added in result.
     * @throws Exception
     */
    @Test
    public void testExecWithUnsuportedDestProtocol() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("local"); //if I don't set this a null pointer exception will be thrown before destProtocol verification.
        remoteCopyInputs.setDestinationProtocol("someRandomProtocol");

        Map<String, String> result = toTest.execute(remoteCopyInputs);
        assertTrue(result.get("exception").contains("Protocol somerandomprotocol not supported!"));
        assertTrue(result.get(RemoteCopyOutputs.RETURN_RESULT).equals("Protocol somerandomprotocol not supported!"));
    }

    /**
     * Tests exec method without host paramether specified.
     * The exec method should not throw an exception. The exception should be added in result.
     * @throws Exception
     */
    @Test
    public void testExecWithoutHostSpecified() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("local"); //if I don't set this a null pointer exception will be thrown before destProtocol verification.
        remoteCopyInputs.setDestinationProtocol("local");

        Map<String, String> result = toTest.execute(remoteCopyInputs);
        assertNotNull(result.get("exception"));
        assertTrue(result.get("exception").contains("When the protocol is local, the host must be localhost!"));
        assertEquals(RemoteCopyOutputs.FAILURE_RETURN_CODE, result.get("returnCode"));
    }

    /**
     * Tests exec method with unsuported sourceCharacterSet specified.
     * The exec method should not throw an exception. The exception should be added in result.
     * @throws Exception
     */
    @Test
    public void testExecWithInvalidCharacerSetSpecified() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("sftp"); //if I don't set this a null pointer exception will be thrown before destProtocol verification.
        remoteCopyInputs.setDestinationProtocol("sftp");
        remoteCopyInputs.setSourceHost("localhost");
        remoteCopyInputs.setDestinationHost("localhost");
        remoteCopyInputs.setSourcePort("8080");
        remoteCopyInputs.setDestinationPort("8080");
        remoteCopyInputs.setSourceCharacterSet("unsupportedCharacterSet");

        Map<String, String> result = toTest.execute(remoteCopyInputs);
        assertNotNull(result.get("exception"));
        assertTrue(result.get("exception").contains("sourceCharacterSet input: unsupportedCharacterSet is not a valid character set name"));
        assertEquals(RemoteCopyOutputs.FAILURE_RETURN_CODE, result.get("returnCode"));
    }

    @Test
    public void testExecWithInvalidDestinationCharacerSetSpecified() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("sftp"); //if I don't set this a null pointer exception will be thrown before destProtocol verification.
        remoteCopyInputs.setDestinationProtocol("sftp");
        remoteCopyInputs.setSourceHost("localhost");
        remoteCopyInputs.setDestinationHost("localhost");
        remoteCopyInputs.setSourcePort("8080");
        remoteCopyInputs.setDestinationPort("8080");
        remoteCopyInputs.setDestinationCharacterSet("unsupportedCharacterSet");

        Map<String, String> result = toTest.execute(remoteCopyInputs);
        assertNotNull(result.get("exception"));
        assertTrue(result.get("exception").contains("destinationCharacterSet input: unsupportedCharacterSet is not a valid character set name"));
        assertEquals(RemoteCopyOutputs.FAILURE_RETURN_CODE, result.get("returnCode"));
    }

    /**
     * Tests exec method with Typet specified and local protocol .
     * The exec method should not throw an exception. The exception should be added in result.
     * @throws Exception
     */
    @Test
    public void testExecWithTypeSetted() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("local"); //if I don't set this a null pointer exception will be thrown before destProtocol verification.
        remoteCopyInputs.setDestinationProtocol("local");
        remoteCopyInputs.setSourceHost("localhost");
        remoteCopyInputs.setDestinationHost("localhost");
        remoteCopyInputs.setFileType("txt");

        Map<String, String> result = toTest.execute(remoteCopyInputs);
        assertNotNull(result.get("exception"));
        assertEquals(RemoteCopyOutputs.FAILURE_RETURN_CODE, result.get("returnCode"));
        assertTrue(result.get("exception").contains("The fileType input must be empty when the FTP protocol is not used!"));
    }

    /**
     * Tests exec method with ftp protocol and without Type specified.
     * The exec method should not throw an exception. The exception should be added in result.
     * @throws Exception
     */
    @Test
    public void testExecWithFtpProtocolAndWithoutType() throws Exception {
        RemoteCopyInputs remoteCopyInputs = new RemoteCopyInputs();
        remoteCopyInputs.setSourceProtocol("ftp"); //if I don't set this a null pointer exception will be thrown before destProtocol verification.
        remoteCopyInputs.setDestinationProtocol("ftp");
        remoteCopyInputs.setSourceHost("localhost:11");
        remoteCopyInputs.setDestinationHost("localhost:11");
        remoteCopyInputs.setSourcePort("8080");
        remoteCopyInputs.setDestinationPort("8080");

        Map<String, String>  result = toTest.execute(remoteCopyInputs);
        assertNotNull(result.get("exception"));
        assertEquals(RemoteCopyOutputs.FAILURE_RETURN_CODE, result.get("returnCode"));
        assertTrue(result.get("exception").contains("When the FTP protocol is used, the type must have the value 'ascii' or 'binary'!"));
    }


}
