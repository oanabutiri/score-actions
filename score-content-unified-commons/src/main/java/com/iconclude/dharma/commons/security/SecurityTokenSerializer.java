/*
 * Created on Jan 31, 2006
 *
 */
package com.iconclude.dharma.commons.security;

import java.io.InputStream;
import java.io.OutputStream;

public interface SecurityTokenSerializer {
    /**
     * Serializes out a token.
     *
     * @param token
     * @param os
     */
    void writeTokenOut(ISecurityToken token, OutputStream os);

    /**
     * Searializes in a token.
     *
     * @param token
     * @param is
     */
    void readTokenIn(ISecurityToken token, InputStream is);

    /**
     * Writes out a token's storage (security values)
     *
     * @param token
     * @return
     */
    byte[] writeTokenStorage(ISecurityToken token);

    /**
     * Reads in a token's storage (security values)
     *
     * @param token
     * @param store
     */
    void readTokenStorage(ISecurityToken token, byte[] store);
}
