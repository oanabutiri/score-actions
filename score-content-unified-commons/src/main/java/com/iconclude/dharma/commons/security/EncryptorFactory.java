package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.exception.DharmaException;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * This class serves encryptor instances. The default encryptor is the one that
 * is used everywhere. This class can be byte-code manipulated to serve a different
 * default encryptor class (for example AES, for customers that have no export issues).
 * <p/>
 * The encryptor classes can be byte-code manipulated to change the burnt-in pass-phrase.
 * When the burnt-in pass-phrase is changed for DharmaBasicEncryptor, a copy of this class
 * is created (prior to manipulating the byte-codes), saved under the DharmaBuiltInEncryptor
 * class, and getBuiltInEncryptionServ is switched to serve a DharmaBuiltInEncryptor instance.
 * The purpose is that we can still decrypt data that was prior encrypted with the default
 * burnt-in pass-phrase.
 *
 * @author octavian
 */
public final class EncryptorFactory {

    private static Logger _log = Logger.getLogger(EncryptorFactory.class);
    private static Map<ENCRYPTORS, Class<? extends IEncryptionService>> _encryptorClassMap;

    ;
    private static Map<ENCRYPTORS, IEncryptionService> _defEncryptorMap;
    private static IEncryptionService _defEncryptor;
    private static IEncryptionService _builtInEncryptor;

    static {
        _encryptorClassMap = new HashMap<ENCRYPTORS, Class<? extends IEncryptionService>>();
        _encryptorClassMap.put(ENCRYPTORS.DES, DharmaBasicEncrypter.class);
        _encryptorClassMap.put(ENCRYPTORS.AES, DharmaAESEncrypter.class);

        // this could return either a DharmaBasicEncrypter (with or without instrumented
        // pass-phrase) or a DharmaAESEncrypter (with or without instrumented pass-phrase)
        _defEncryptor = instantiateDefEncryptor();

        // this should always return an ecryptor configured with the v1.0 pass-phrase
        // (the byte-code instrumentation should change the instance returned by
        // instantiateBuiltInEncryptor() to DharmaBuiltInEncrypter, which is a clone of
        // DharmaBasicEncrypter BEFORE its pass-phrase gets instrumented
        _builtInEncryptor = instantiateBuiltInEncryptor();

        _defEncryptorMap = new HashMap<ENCRYPTORS, IEncryptionService>();
        _defEncryptorMap.put(ENCRYPTORS.DES, new DharmaBasicEncrypter());
        _defEncryptorMap.put(ENCRYPTORS.AES, new DharmaAESEncrypter());
    }

    // ATTENTION: if you rename this method, EncryptionServInstrumentation needs to
    // be changed as well...
    private static IEncryptionService instantiateDefEncryptor() {
        // this method can be instrumented to return a different encryptor class
        return new DharmaAESEncrypter();
    }

    // ATTENTION: if you rename this method, EncryptionServInstrumentation needs to
    // be changed as well...
    private static IEncryptionService instantiateBuiltInEncryptor() {
        return new DharmaBasicEncrypter();
    }

    // ATTENTION: if you rename this method, EncryptionServInstrumentation needs to
    // be changed as well... The identifier this method returns can be changed by
    // bytecode instrumentation to return a different id (like AES).
    private static ENCRYPTORS getDefaultEncryptorId() {
        return ENCRYPTORS.AES;
    }

    /**
     * This method returns the default encryptor (AES post version 2.2).
     * Note that the returned encryptor might be the built-in (with the
     * default burnt-in pass phrase) OR an instrumented AES encryptor
     * that had its pass phrase instrumented (by EncryptionServInstrumentation class)
     *
     * @return instance of encryption service
     */
    public static IEncryptionService getDefaultEncryptionServ() {
        return _defEncryptor;
    }

    /**
     * This method serves an instance of the default encryption service configured
     * with a particular pass-phrase. Please note that by default DES is used, but
     * (through byte-code instrumentation) this can be switched to use AES
     *
     * @param parola - pass-phrase
     * @return
     */
    public static IEncryptionService getDefaultEncryptionServ(byte[] parola) {
        return getEncryptionServ(getDefaultEncryptorId(), parola);
    }

    /**
     * This method serves an instance of the default encryption service configured
     * with a particular pass-phrase and salt. Please note that by default DES is used, but
     * (through byte-code instrumentation) this can be switched to use AES
     *
     * @param parola - pass-phrase
     * @param sare   - salt
     * @return
     */
    public static IEncryptionService getDefaultEncriptionServ(byte[] parola, byte[] sare) {
        return getEncryptionServ(getDefaultEncryptorId(), parola, sare);
    }

    public static IEncryptionService getBuiltInEncryptionServ() {
        return _builtInEncryptor;
    }

    /**
     * This method should be used only when the returned class is byte-code instrumented
     * to use a stronger pass-phrase
     *
     * @param encryptor
     * @return
     */
    public static IEncryptionService getEncryptionServ(ENCRYPTORS encryptor) {
        return _defEncryptorMap.get(encryptor);
    }

    /**
     * @param encryptor
     * @param parola    - pass-phrase bytes
     * @return
     */
    public static IEncryptionService getEncryptionServ(ENCRYPTORS encryptor, byte[] parola) {
        Class<? extends IEncryptionService> cls = _encryptorClassMap.get(encryptor);
        try {
            Constructor<? extends IEncryptionService> constructor = cls.getDeclaredConstructor(byte[].class);
            return constructor.newInstance(parola);
        } catch (Exception e) {
            _log.error("could not instantiate " + encryptor, e); //$NON-NLS-1$
            throw new DharmaException(e);
        }
    }

    /**
     * @param encryptor
     * @param parola    - pass-phrase bytes
     * @param sare      - salt bytes
     * @return
     */
    public static IEncryptionService getEncryptionServ(ENCRYPTORS encryptor, byte[] parola, byte[] sare) {
        Class<? extends IEncryptionService> cls = _encryptorClassMap.get(encryptor);
        try {
            Constructor<? extends IEncryptionService> constructor =
                    cls.getDeclaredConstructor(byte[].class, byte[].class);
            return constructor.newInstance(parola, sare);
        } catch (Exception e) {
            _log.error("could not instantiate " + encryptor, e); //$NON-NLS-1$
            throw new DharmaException(e);
        }
    }

    public enum ENCRYPTORS {
        DES,
        AES
    }
}
