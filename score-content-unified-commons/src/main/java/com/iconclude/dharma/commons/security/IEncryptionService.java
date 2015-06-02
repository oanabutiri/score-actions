/*
 * Created on Jan 30, 2006
 *
 */
package com.iconclude.dharma.commons.security;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author octavian
 */
public interface IEncryptionService {
    /**
     * Encrypts a stream.
     *
     * @param in  - the stream to encrypt
     * @param out - the encrypted output stream
     */
    void encrypt(InputStream in, OutputStream out);

    /**
     * Decrypts a stream.
     *
     * @param in  - the stream to decrypt
     * @param out - the decrypted output stream
     */
    void decrypt(InputStream in, OutputStream out);


    /**
     * @param out - output stream
     * @return an encryption output stream which wrapps the passed stream
     */
    OutputStream getOutputStream(OutputStream out);


    /**
     * @param in - input stream
     * @return a decryption input stream which wrapps the passed in stream
     */
    InputStream getInputStream(InputStream in);

    /**
     * Encrypts a byte-array
     *
     * @param val - the byte-array to encrypt
     * @return encrypted byte-array
     */
    byte[] encrypt(byte[] val, int offset, int len);

    /**
     * Decrypts a byte-array
     *
     * @param val - the byte-array to decrypt
     * @return decrypted byte-array
     */
    byte[] decrypt(byte[] val, int offset, int len);

    /**
     * Encrypts a string
     *
     * @param str - the string to encrypt
     * @return encrypted string
     */
    String encrypt(String str);

    /**
     * Decrypts a string
     *
     * @param str - the string to decrypt
     * @return decrypted string
     */
    String decrypt(String str);
}
