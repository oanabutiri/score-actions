/*
 * Created on Feb 23, 2006
 *
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.exception.DharmaException;
import com.iconclude.dharma.commons.util.Dharma;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Set;

/**
 * @author octavian
 */
abstract class DharmaEncrypterBase implements IEncryptionService {
    transient protected final Logger _log = Logger.getLogger(getClass());

    static {
        Logger log = Logger.getLogger(DharmaEncrypterBase.class);
        if (log.isInfoEnabled()) {
            log.info("Security Providers:"); //$NON-NLS-1$
            Provider[] providers = Security.getProviders();
            for (int i = 0; i < providers.length; ++i) {
                log.info("\t" + providers[i]); //$NON-NLS-1$
            }

            log.info("Security Ciphers:"); //$NON-NLS-1$
            Set ciphers = Security.getAlgorithms("Cipher"); //$NON-NLS-1$
            Object[] cipherNames = ciphers.toArray();
            for (int i = 0; i < cipherNames.length; i++) {
                log.info("\t" + cipherNames[i]); //$NON-NLS-1$
            }
        }
    }

    DharmaEncrypterBase() {
        startupLogging();

        init(colecteazaParola(), colecteazaSare());
    }

    DharmaEncrypterBase(byte[] parola, byte[] sare) {
        startupLogging();

        init(parola, sare);
    }

    DharmaEncrypterBase(byte[] parola) {
        startupLogging();

        init(parola, colecteazaSare());
    }

    protected void startupLogging() {

    }

    protected abstract void init(byte[] parola, byte[] sare);

    /**
     * @return the pass-phrase bytes
     */
    protected abstract byte[] colecteazaParola();

    /**
     * @return the salt bytes
     */
    protected abstract byte[] colecteazaSare();

    /**
     * @return an encryption cipher instance
     */
    protected abstract Cipher encriptor();

    /**
     * @return a decryption cipher instance
     */
    protected abstract Cipher decriptor();

    public void encrypt(InputStream in, OutputStream out) {
        if (null == in || null == out)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaEncrypterBase.EncryptNullStreamError")); //$NON-NLS-1$
        try {
            out = getOutputStream(out);
            crypt(in, out);
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    public void decrypt(InputStream in, OutputStream out) {
        if (null == in || null == out)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaEncrypterBase.DecryptNullStreamError")); //$NON-NLS-1$
        try {
            in = getInputStream(in);
            crypt(in, out);
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    private void crypt(InputStream in, OutputStream out) {
        byte[] buf = new byte[1024];
        // Read in the cleartext bytes and write to out
        int numRead = 0;
        try {
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
        } catch (Exception e) {
            throw new DharmaException(e);
        } finally {
            Dharma.close(out);
            Dharma.close(in);
            Arrays.fill(buf, (byte) 0x0);
        }
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.services.IEncryptionService#encrypt(byte[])
     */
    public byte[] encrypt(byte[] val, int offset, int len) {
        if (val == null)
            return null;
        try {
            return encriptor().doFinal(val, offset, len);
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.services.IEncryptionService#decrypt(byte[])
     */
    public synchronized byte[] decrypt(byte[] val, int offset, int len) {
        if (val == null)
            return null;
        try {
            return decriptor().doFinal(val, offset, len);
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.security.IEncryption#encrypt(java.lang.String)
     */
    public String encrypt(String str) {
        if (str == null)
            return null;
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8"); //$NON-NLS-1$
            // Encrypt
            byte[] enc = encriptor().doFinal(utf8);
            // Encode bytes to base64 to get a string
            return new BASE64Encoder().encode(enc);
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.iconclude.dharma.security.IEncryption#decrypt(java.lang.String)
     */
    public synchronized String decrypt(String str) {
        if (str == null)
            return null;
        try {
            // Decode base64 to get bytes
            byte[] dec = new BASE64Decoder().decodeBuffer(str);
            // Decrypt
            byte[] utf8 = decriptor().doFinal(dec);
            // Decode using utf-8
            return new String(utf8, "UTF8"); //$NON-NLS-1$
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    public InputStream getInputStream(InputStream in) {
        if (in == null)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaEncrypterBase.NullInputStreamError")); //$NON-NLS-1$
        return new CipherInputStream(in, decriptor());
    }

    public OutputStream getOutputStream(OutputStream out) {
        if (out == null)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaEncrypterBase.NullOutputStreamError")); //$NON-NLS-1$
        return new CipherOutputStream(out, encriptor());
    }
}
