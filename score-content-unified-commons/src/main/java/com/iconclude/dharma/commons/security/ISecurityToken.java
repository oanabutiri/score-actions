/*
 * Created on Jan 30, 2006
 *
 */
package com.iconclude.dharma.commons.security;


import java.util.Set;
import java.util.UUID;


/**
 * This interface represents a security token. A security token
 * carries a name, a description and a "bag" of security values.
 * A security value is a pair of a key and a (optionally encrypted)
 * binary value. The security token maintains the information
 * about the encryption state of a value (in the first posistion of the raw value).
 * Calling getSecurityValue() will return the un-encrypted value; calling
 * getRawValue, will return the raw value, where the first position contains
 * the encryption byte (0x1 - encrypted, 0x0 - not encrypted).
 *
 * @author octavian
 */
public interface ISecurityToken {


    /**
     * @return the serial version ID
     */
    long getSerialVersionUID();

    /**
     * The SID uniquely identifies a token
     *
     * @return token's SID
     */
    UUID getSID();

    void setSID(UUID sid);

    /**
     * Sets token's encryption service. The encryption service can be null, in which
     * case the request to deal with an encrypted security value will throw an exception
     *
     * @param es - encryption service reference
     */
    void setEncryptionService(IEncryptionService es);

    /**
     * Adds a security value to the token. If encrypt is true, but the token
     * has a null encryption service, this method will throw IllegalArgumentException.
     *
     * @param key
     * @param value
     * @param encrypt
     */
    void addSecurityValue(String key, byte[] value, boolean encrypt) throws SecurityException;

    /**
     * Attempts to delete a security value. If the token does not have
     * such a security value, the method does nothing.
     *
     * @param key
     */
    void deleteSecurityValue(String key) throws SecurityException;

    /**
     * Returns a security value. If the value is encrypted, but the token
     * has a null encryption service, this method will throw IllegalArgumentException.
     * If the token does not have such a security value, null is returned
     *
     * @param key
     * @return
     */
    byte[] getSecurityValue(String key) throws SecurityException;

    /**
     * Adds a "raw" security value; the token does not touch it, it only stores
     * and persist it.
     *
     * @param key
     * @param value
     */
    void addRawValue(String key, byte[] value) throws SecurityException;

    /**
     * Retrieves a "raw" value. The value is processed in any way by the token.
     * If such value does not exist, null is returned
     *
     * @param key
     * @return
     */
    byte[] getRawValue(String key) throws SecurityException;

    /**
     * @return the set of security keys
     */
    Set<String> getSecurityValueKeys() throws SecurityException;


}
