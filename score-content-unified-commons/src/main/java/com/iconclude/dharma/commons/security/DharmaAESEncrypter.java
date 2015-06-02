/*
 * Created on Feb 23, 2006
 *
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.exception.DharmaException;
import com.iconclude.dharma.commons.util.Dharma;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.util.Arrays;

/**
 * This class is doing a PBE (password-based encryption) AES. PBE AES is not
 * supported Sun's cryptograpic providers; bouncy castle's provider supports this
 * but at the moment we do not want to have an extra jar to distribute (easy for
 * product, but more trouble-some for JRASes which would need the bouncy castle jar).
 * On the .NET side (NRAS), we use bouncy castle C# port, so I decided to implement
 * PBE AES in a way that we can just work with on the NRAS side using bouncy castle
 * API. So this implements the PBE that would be achieved with bouncy castle's API doing:
 * <p/>
 * PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator();
 * generator.init(_cheie_bytes, _sare, iterationCount);
 * ParametersWithIV params = (ParametersWithIV)generator.generateDerivedParameters(keyLen, keyLen);
 * KeyParameter keyParam = (KeyParameter) params.getParameters();
 * SecretKeySpec _key = new SecretKeySpec(keyParam.getKey(), "AES");
 *
 * @author octavian
 */
public class DharmaAESEncrypter extends DharmaEncrypterBase {
    private Mac sha1Mac;
    private SecretKeySpec _cheie;

    /**
     * This is using the default pass-phrase.
     */
    DharmaAESEncrypter() {
        super();
    }

    /**
     * @param parola - pass-phrase bytes
     */
    DharmaAESEncrypter(byte[] parola) {
        super(parola);
    }

    /**
     * @param parola - pass-phrase bytes
     * @param sare   - salt bytes (should be at least 8 bytes)
     */
    DharmaAESEncrypter(byte[] parola, byte[] sare) {
        super(parola, sare);
    }

    // ATTENTION if this method is renamed, EncryptionServInstrumentation needs to be changes as well\
    protected static int colecteazaLungimeCheie() {
        // more than 128 bits key requires unlimited strength cryptography...
        return 128;
    }

    private static void intToOctet(byte[] buf, int i) {
        buf[0] = (byte) (i >>> 24);
        buf[1] = (byte) (i >>> 16);
        buf[2] = (byte) (i >>> 8);
        buf[3] = (byte) i;
    }

    protected byte[] colecteazaParola() {
        byte[] parola = {(byte) 0xE5, (byte) 0xD4, (byte) 0x08, (byte) 0x61, (byte) 0x25, (byte) 0xC1, (byte) 0x6F, (byte) 0x07,
                (byte) 0x66, (byte) 0xB6, (byte) 0x34, (byte) 0x7B, (byte) 0x9B, (byte) 0xC9, (byte) 0xFB, (byte) 0x5F,
                (byte) 0x40, (byte) 0xC8, (byte) 0x02, (byte) 0x34, (byte) 0x05, (byte) 0xD1, (byte) 0x67, (byte) 0xD3,
                (byte) 0x7F, (byte) 0x5F, (byte) 0xB5, (byte) 0x72, (byte) 0x7E, (byte) 0xDD, (byte) 0x3F, (byte) 0x61};

        return parola;
    }

    protected byte[] colecteazaSare() {
        // 8-byte Salt
        byte[] sare = {
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03};
        return sare;
    }

    @Override
    protected void init(byte[] parola, byte[] sare) {
        try {
            sha1Mac = Mac.getInstance("HmacSHA1"); //$NON-NLS-1$
        } catch (Exception e) {
            throw new DharmaException(e);
        }

        int cicluri = 19; // iteration count for creating a cryptographic key from the pass-phrase
        int lungimeCheie = colecteazaLungimeCheie(); // more than 128 bits key requires unlimited strength cryptography...

        try {
            _cheie = new SecretKeySpec(_creazaCheie(parola, sare, cicluri, lungimeCheie), "AES"); //$NON-NLS-1$

            // clean the key so it does not linger in memory; no defense against
            // decompiling...
            Arrays.fill(parola, (byte) 0x0);
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    @Override
    protected Cipher encriptor() {
        try {
            Cipher cipher = Cipher.getInstance("AES"); //$NON-NLS-1$
            cipher.init(Cipher.ENCRYPT_MODE, _cheie);
            return cipher;
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    @Override
    protected Cipher decriptor() {
        try {
            Cipher cipher = Cipher.getInstance("AES"); //$NON-NLS-1$
            cipher.init(Cipher.DECRYPT_MODE, _cheie);
            return cipher;
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    private byte[] _creazaCheie(byte[] parola, byte[] sare, int cicluri, int lungimeCheie) {
        // use an initialization vector the same size as the key length...
        int dkLen = (2 * lungimeCheie) / 8; // derived key len in bytes (key len is in bits)

        int hLen = sha1Mac.getMacLength();
        int l = (dkLen + hLen - 1) / hLen;
        byte[] iBuf = new byte[4];
        byte[] out = new byte[l * hLen];

        for (int i = 1; i <= l; i++) {
            intToOctet(iBuf, i);
            _fornicate(parola, sare, cicluri, iBuf, out, (i - 1) * hLen);
        }
        // collect keyLen off the out buff
        byte[] res = new byte[lungimeCheie / 8];
        System.arraycopy(out, 0, res, 0, lungimeCheie / 8);
        return res;
    }

    private void _fornicate(byte[] parola, byte[] sare, int lungimeCheie, byte[] iBuf, byte[] out, int outOff) {
        try {
            byte[] state = new byte[sha1Mac.getMacLength()];
            byte[] parola_copy = new byte[parola.length];
            System.arraycopy(parola, 0, parola_copy, 0, parola.length);
            _initMac(parola_copy);

            if (sare != null) {
                sha1Mac.update(sare, 0, sare.length);
            }
            sha1Mac.update(iBuf, 0, iBuf.length);
            sha1Mac.doFinal(state, 0);

            System.arraycopy(state, 0, out, outOff, state.length);

            if (lungimeCheie == 0) {
                throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaAESEncrypter.IterationCountError")); //$NON-NLS-1$
            }

            for (int count = 1; count < lungimeCheie; count++) {
                parola_copy = new byte[parola.length];
                System.arraycopy(parola, 0, parola_copy, 0, parola.length);
                _initMac(parola_copy);
                sha1Mac.update(state, 0, state.length);
                sha1Mac.doFinal(state, 0);

                for (int j = 0; j != state.length; j++) {
                    out[outOff + j] ^= state[j];
                }
            }
        } catch (Exception e) {
            throw new DharmaException(e);
        }
    }

    private void _initMac(final byte[] parola) {
        try {
            sha1Mac.init(new SecretKey() {
                private static final long serialVersionUID = -7331948983828873177L;

                public String getAlgorithm() {
                    return "AES"; //$NON-NLS-1$
                }

                public String getFormat() {
                    return "RAW"; //$NON-NLS-1$
                }

                public byte[] getEncoded() {
                    return parola;
                }

                @Override
                public boolean equals(Object obj) {
                    if (!(obj instanceof byte[]))
                        return false;
                    byte[] other = (byte[]) obj;
                    return Arrays.equals(parola, other);
                }

                @Override
                public int hashCode() {
                    int iConstant = 37;
                    int iTotal = 17;
                    for (int i = 0; i < parola.length; i++)
                        iTotal = iTotal * iConstant + parola[i];
                    return iTotal;
                }
            });
        } catch (InvalidKeyException e) {
            throw new DharmaException(e);
        }
    }

    @Override
    protected void startupLogging() {
        super.startupLogging();
        if (_log.isInfoEnabled())
            _log.info("Instantiating AES encryptor..."); //$NON-NLS-1$
    }

}
