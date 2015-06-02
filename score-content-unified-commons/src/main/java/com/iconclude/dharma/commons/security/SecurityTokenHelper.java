/*
 * Created on Jan 30, 2006
 *
 */
package com.iconclude.dharma.commons.security;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.iconclude.dharma.commons.exception.DharmaException;
import com.iconclude.dharma.commons.util.Dharma;
import com.iconclude.dharma.commons.util.Pair;

import java.io.*;
import java.util.Set;


/**
 * Helper class for dealing with security tokens.
 *
 * @author octavian
 */
public class SecurityTokenHelper {
    private static final byte BEGIN_TOKEN = 0x72;
    private static final byte END_TOKEN = 0x73;
    private static final byte BEGIN_TOKEN_HEADER = 0x70;
    private static final byte END_TOKEN_HEADER = 0x71;
    private static final byte[] _zero = new byte[]{0, 0, 0, 0};

    @SuppressWarnings(value = {"unchecked"}) //$NON-NLS-1$
    private static final Pair<Long, SecurityTokenSerializer>[] _serializerMap =
            new Pair[]{new Pair<Long, SecurityTokenSerializer>(new Long(2748040957100218321L),
                    new SecurityTokenSerializer() {
                        public void writeTokenOut(ISecurityToken token, OutputStream os) {
                            byte[] int_buff = new byte[4];
                            byte[] sidBuf = UUIDUtil.asByteArray(token.getSID());
                            try {
                                putInt(int_buff, 0, sidBuf.length);
                                os.write(int_buff);
                                os.write(sidBuf);
                                // write the storage
                                byte[] store = writeTokenStorage(token);
                                putInt(int_buff, 0, store.length);
                                os.write(int_buff);
                                os.write(store);
                            } catch (Exception ex) {
                                throw new DharmaException(ex);
                            }
                        }

                        public void readTokenIn(ISecurityToken token, InputStream is) {
                            byte[] int_buff = new byte[4];
                            try {
                                checkRead(is.read(int_buff, 0, 4), 4);
                                int len = getInt(int_buff, 0);
                                byte[] buff = new byte[len];
                                checkRead(is.read(buff, 0, len), len);
                                token.setSID(UUIDUtil.uuid(buff));
                                // read the storage
                                checkRead(is.read(int_buff, 0, 4), 4);
                                len = getInt(int_buff, 0);
                                buff = new byte[len];
                                checkRead(is.read(buff, 0, len), len);
                                readTokenStorage(token, buff);
                            } catch (Exception ex) {
                                throw new DharmaException(ex);
                            }
                        }

                        public byte[] writeTokenStorage(ISecurityToken token) {
                            byte[] int_buff = new byte[4];
                            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024); // preallocate 1k

                            Set<String> keys = null;
                            try {
                                keys = token.getSecurityValueKeys();
                                putInt(int_buff, 0, keys.size());
                                baos.write(int_buff);
                                for (String key : keys) {
                                    // write the key
                                    putInt(int_buff, 0, key.length());
                                    baos.write(int_buff);
                                    baos.write(key.getBytes("UTF8")); //$NON-NLS-1$
                                    // write the value
                                    byte[] val = token.getRawValue(key);
                                    if (val != null) {
                                        putInt(int_buff, 0, val.length);
                                        baos.write(int_buff);
                                        baos.write(val);
                                    } else {
                                        putInt(int_buff, 0, 0); //indicate a null value
                                    }
                                }
                            } catch (SecurityException se) {
                                throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.TokenWriteError"), se); //$NON-NLS-1$
                            } catch (Exception ex) {
                                throw new DharmaException(ex);
                            }
                            return baos.toByteArray();
                        }

                        public void readTokenStorage(ISecurityToken token, byte[] store) {
                            byte[] int_buff = new byte[4];
                            ByteArrayInputStream bais = new ByteArrayInputStream(store);
                            // read the storage
                            try {
                                bais.read(int_buff, 0, 4);
                                int size = getInt(int_buff, 0);
                                for (int i = 0; i < size; ++i) {
                                    // read key
                                    bais.read(int_buff, 0, 4);
                                    int len = getInt(int_buff, 0);
                                    byte[] buff = new byte[len];
                                    bais.read(buff, 0, len);
                                    String key = new String(buff, 0, len, "UTF8"); //$NON-NLS-1$
                                    // read value
                                    bais.read(int_buff, 0, 4);
                                    len = getInt(int_buff, 0);
                                    if (len > 0) {
                                        buff = new byte[len];
                                        bais.read(buff, 0, len);
                                        token.addRawValue(key, buff);
                                    } else {
                                        token.addRawValue(key, null);
                                    }
                                }
                            } catch (Exception ex) {
                                throw new DharmaException(ex);
                            }
                        }

                    })
            };

    public static SecurityTokenSerializer getSerializer(long serVer) {
        for (Pair<Long, SecurityTokenSerializer> pair : _serializerMap) {
            if (pair.getFirst().compareTo(Long.valueOf(serVer)) == 0)
                return pair.getSecond();
        }
        return null;
    }

    public static void addSecurityValue(ISecurityToken token,
                                        String key, String val, boolean encrypt) {
        if (null == token || null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.SecurityTokenHelper.AddSecurityValueNullException")); //$NON-NLS-1$
        if (null == val) {
            try {
                token.addSecurityValue(key, null, encrypt);
            } catch (SecurityException se) {
                throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.AddSecureValueError"), se); //$NON-NLS-1$
            }
            return;
        }
        // Encode the string into bytes using utf-8
        byte[] utf8;
        try {
            utf8 = val.getBytes("UTF8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new DharmaException(e);
        }
        try {
            token.addSecurityValue(key, utf8, encrypt);
        } catch (SecurityException se) {
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.AddSecureValueError"), se); //$NON-NLS-1$
        }
    }

    public static String getSecurityValue(ISecurityToken token, String key) {
        if (null == token || null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.SecurityTokenHelper.GetSecurityValueNullException")); //$NON-NLS-1$
        byte[] val = null;
        try {
            val = token.getSecurityValue(key);
        } catch (SecurityException se) {
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.GetSecureValueError"), se); //$NON-NLS-1$
        }
        if (null == val)
            return null;

        String ret;
        try {
            ret = new String(val, "UTF8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new DharmaException(e);
        }
        return ret;
    }

    public static void writeOut(ISecurityToken token, OutputStream os) throws IOException {
        if (null == token || null == os)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.SecurityTokenHelper.WriteOutNullError")); //$NON-NLS-1$

        byte[] tmp = new byte[8];
        long lVal = token.getSerialVersionUID();
        SecurityTokenSerializer serializer = getSerializer(lVal);
        if (null == serializer)
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.TokenWriteDetailError") + Long.toString(lVal)); //$NON-NLS-1$

        os.write(BEGIN_TOKEN);
        os.write(BEGIN_TOKEN_HEADER);
        // write interface name
        String className = ISecurityToken.class.getName();
        putInt(tmp, 0, className.length());
        os.write(tmp, 0, 4);
        os.write(className.getBytes("UTF8")); //$NON-NLS-1$
        // write the serial #

        tmp[0] = (byte) (lVal >>> 56);
        tmp[1] = (byte) (lVal >>> 48);
        tmp[2] = (byte) (lVal >>> 40);
        tmp[3] = (byte) (lVal >>> 32);
        tmp[4] = (byte) (lVal >>> 24);
        tmp[5] = (byte) (lVal >>> 16);
        tmp[6] = (byte) (lVal >>> 8);
        tmp[7] = (byte) (lVal >>> 0);
        os.write(tmp);
        os.write(END_TOKEN_HEADER);

        serializer.writeTokenOut(token, os);

        os.write(END_TOKEN);
    }

    public static void readIn(ISecurityToken token, InputStream is) throws IOException {
        if (null == token || null == is)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.SecurityTokenHelper.ReadInNullException")); //$NON-NLS-1$
        byte begin_token = (byte) is.read();
        byte begin_header = (byte) is.read();
        if (begin_token != BEGIN_TOKEN || begin_header != BEGIN_TOKEN_HEADER)
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.TokenCorruptedStreamError")); //$NON-NLS-1$

        byte[] tmp = new byte[8];

        // read class name
        checkRead(is.read(tmp, 0, 4), 4);
        int len = getInt(tmp, 0);
        byte[] buff = new byte[len];
        checkRead(is.read(buff, 0, len), len);
        String class_name = new String(buff, 0, len, "UTF8"); //$NON-NLS-1$
        if (!class_name.equals(ISecurityToken.class.getName()))
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.ExpectationCorruptedStreamError")); //$NON-NLS-1$

        // read serial #
        checkRead(is.read(tmp, 0, 8), 8);
        long serVer = (((long) tmp[0] << 56) + ((long) (tmp[1] & 255) << 48) +
                ((long) (tmp[2] & 255) << 40) + ((long) (tmp[3] & 255) << 32) +
                ((long) (tmp[4] & 255) << 24) + ((tmp[5] & 255) << 16) +
                ((tmp[6] & 255) << 8) + ((tmp[7] & 255) << 0));
        SecurityTokenSerializer serializer = getSerializer(serVer);
        if (null == serializer)
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.TokenReadDetailError") + Long.toString(serVer)); //$NON-NLS-1$

        if (is.read() != END_TOKEN_HEADER)
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.TokenCorruptedStreamError")); //$NON-NLS-1$

        serializer.readTokenIn(token, is);

        if (is.read() != END_TOKEN)
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.TokenCorruptedStreamError")); //$NON-NLS-1$
    }

    // utility methods packing/unpacking integers in/out of byte arrays
    // using big-endian byte ordering.
    private static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF) << 0) +
                ((b[off + 2] & 0xFF) << 8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off + 0] & 0xFF) << 24);
    }

    private static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val >>> 0);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off + 0] = (byte) (val >>> 24);
    }

    /**
     * a simple method to ensure reads return the amount of bytes we excpect, if not, it throws an exception
     *
     * @param bytesRead
     * @param bytesExpected
     */
    private static void checkRead(int bytesRead, int bytesExpected) {
        if (bytesRead != bytesExpected) {
            throw new DharmaException(Dharma.msg("Dharma.security.SecurityTokenHelper.ExpectedError", bytesExpected, bytesRead)); //$NON-NLS-1$
        }
    }
}
