package io.cloudslang.content.rft.commons.remoteCopy.sftp;

import com.opsware.pas.content.commons.remoteCopy.sftp.MyUserInfo;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class MyUserInfoTest {
    private com.opsware.pas.content.commons.remoteCopy.sftp.MyUserInfo myUser;

    @Before
    public void setUp() {
        myUser = new MyUserInfo();
    }

    /**
     * Tests the privateKey setter and getter
     */
    @Test
    public void testPrivateKey() {
        String privateKey = "privateKey";
        myUser.setPrivateKey(privateKey);
        assertEquals(privateKey, myUser.getPrivateKey());
    }

    /**
     * Tests the PromptPassPhrase setter and getter
     */
    @Test
    public void testPromptPassPhrase() {
        boolean prompt = true;
        myUser.setPromptPassphrase(prompt);
        assertEquals(prompt, myUser.isPromptPassphrase());
        assertEquals(prompt, myUser.promptPassphrase(null));
    }

    /**
     * Test the PromptYesNo setter and getter
     */
    @Test
    public void testPromptYesNo() {
        boolean prompt = true;
        myUser.setPromptYesNo(prompt);
        assertEquals(prompt, myUser.isPromptYesNo());
    }

    /**
     * Tests the password setter and getter
     */
    @Test
    public void testPassword() {
        String password = "password";
        myUser.setPasswd(password);
        assertEquals(password, myUser.getPasswd());
    }

    /**
     * Tests the PromptPassword setter and getter
     */
    @Test
    public void testPromptPassword() {
        boolean prompt = true;
        myUser.setPromptPassword(prompt);
        assertEquals(prompt, myUser.isPromptPassword());
    }

    /**
     * tests the PassPhrase getter and setter
     */
    @Test
    public void testPassPhrase() {
        String pass = "PassPhrase";
        myUser.setPassphrase(pass);
        assertEquals(pass, myUser.getPassphrase());
    }

    /**
     * Tests the subject getter
     */
    @Test
    public void testSubject() {
        assertNull(myUser.getSubject());
    }
}
