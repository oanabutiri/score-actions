/*
 * Created on Jan 30, 2006
 *
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.exception.DharmaException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * @author octavian
 *         This class cannot be changed anymore, as there is legacy data that is using
 *         this weak encryption.
 *         <p/>
 *         This could be used when export issues are important (the
 *         encryption key cannot be longer than 56 bits), otherwise DharmaAESEncrypter.
 * @deprecated
 */
public class DharmaBasicEncrypter extends DharmaEncrypterBase {
    private AlgorithmParameterSpec _paramSpec;
    private Key _cheie;

    /**
     * This is using the default pass-phrase (weak). Should not use
     * this constructor, unless:
     * - the default pass-phrase is good enough
     * - the class is byte-code transformed and the default pass-phrase is
     * changed to something better
     */
    DharmaBasicEncrypter() {
        super();
    }

    /**
     * @param parola - pass-phrase bytes
     */
    DharmaBasicEncrypter(byte[] parola) {
        super(parola);
    }

    /**
     * @param parola - pass-phrase bytes
     * @param sare   - salt bytes (should be at least 8 bytes)
     */
    DharmaBasicEncrypter(byte[] parola, byte[] sare) {
        super(parola, sare);
    }

    protected byte[] colecteazaParola() {
        // this is a weak pass-phrase...
        byte[] parola = {0x69, 0x43, 0x6F, 0x6E, 0x63, 0x6C,
                0x75, 0x64, 0x65, 0x63, 0x6F, 0x6D,
                0x52, 0x65, 0x70, 0x61, 0x69, 0x72,
                0x43, 0x65, 0x6E, 0x74, 0x65, 0x72};
        return parola;
    }

    protected byte[] colecteazaSare() {
        // 8-byte Salt
        byte[] sare = {
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03};
        return sare;
    }

    protected void init(byte[] parola, byte[] sare) {
        String reteta = "PBEWITHMD5ANDDES";         //$NON-NLS-1$
        int cicluri = 19;

        _paramSpec = new PBEParameterSpec(sare, cicluri);
        try {
            String _parola = new String(parola);
            KeySpec keySpec = new PBEKeySpec(_parola.toCharArray(), sare, cicluri);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(reteta);
            _cheie = keyFactory.generateSecret(keySpec);

            // clean the key so it does not linger in memory; no defense against
            // de-compiling...
            Arrays.fill(parola, (byte) 0x0);
        } catch (Exception e) {
            throw new DharmaException(e);
        }

    }

    @Override
    protected void startupLogging() {
        super.startupLogging();
        if (_log.isInfoEnabled())
            _log.info("Instantiating DES encryptor..."); //$NON-NLS-1$
    }

    @Override
    protected Cipher encriptor() {
        try {
            Cipher cipher = Cipher.getInstance("PBEWITHMD5ANDDES"); //$NON-NLS-1$
            cipher.init(Cipher.ENCRYPT_MODE, _cheie, _paramSpec);
            return cipher;
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    @Override
    protected Cipher decriptor() {
        try {
            Cipher cipher = Cipher.getInstance("PBEWITHMD5ANDDES"); //$NON-NLS-1$
            cipher.init(Cipher.DECRYPT_MODE, _cheie, _paramSpec);
            return cipher;
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }
}
