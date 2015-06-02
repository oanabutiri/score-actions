package com.iconclude.dharma.commons.security;

import org.acegisecurity.userdetails.UserDetails;

import javax.security.auth.kerberos.KerberosPrincipal;
import java.security.Principal;

/**
 * @author octavian
 */
public class AuthUtils {

    public static String getUsername(String principalName) {
        int index = principalName.indexOf('\\');
        if (-1 != index) {
            // deal with Windows domain name
            return principalName.substring(index + 1);
        }
        index = principalName.indexOf('@');
        if (-1 != index) {
            // deal with Kerberos realm
            return principalName.substring(0, index);
        }
        return principalName;
    }

    public static String getRealm(String principalName) {
        int index = principalName.indexOf('\\');
        if (-1 != index) {
            // deal with Windows domain name
            return principalName.substring(0, index);
        }
        index = principalName.indexOf('@');
        if (-1 != index) {
            // deal with Kerberos realm
            return principalName.substring(index + 1);
        }
        return null;
    }

    public static String getUsername(UserDetails principal) {
        String principalName = principal.getUsername();
        return getUsername(principalName);
    }

    public static String getRealm(UserDetails principal) {
        String principalName = principal.getUsername();
        return getRealm(principalName);
    }

    public static String getUsername(KerberosPrincipal principal) {
        String principalName = principal.getName();
        int index = principalName.indexOf('@');
        if (-1 != index) {
            // deal with Kerberos realm
            return principalName.substring(0, index);
        }
        return principalName;
    }

    public static String getRealm(KerberosPrincipal principal) {
        return principal.getRealm();
    }

    public static UserInfo processPrincipal(Principal principal) {
        String qname, username, realm;
        if (principal instanceof KerberosPrincipal) {
            qname = principal.getName();
            username = AuthUtils.getUsername((KerberosPrincipal) principal);
            realm = AuthUtils.getRealm((KerberosPrincipal) principal);
        } else {
            String principalName = principal.getName();
            qname = principalName;
            username = AuthUtils.getUsername(principalName);
            realm = AuthUtils.getRealm(principalName);
        }
        return new UserInfo(qname, username, realm);
    }

    /**
     * Add information from <code>IDharmaAuthentication</code> to an <code>HttpMethod</code>.  This includes
     * credentials and session info such as cookies.
     *
     * @param auth
     * @param method
     */
//    public static void addSessionInfo(IDharmaAuthentication auth, HttpMethod method) {
//        if (auth == null) {
//            throw new IllegalArgumentException("auth should not be null");
//        }
//        if (method == null) {
//            throw new IllegalArgumentException("method should not be null");
//        }
//
//        method.addRequestHeader(com.iconclude.dharma.commons.security.Constants.HEADER_CREDENTIALS,
//                EncryptionUtils.encodeCredentialsForHttp(auth));
//        SessionInfo info = auth.getDetail(SessionInfo.class);
//        if (info != null) {
//            for (String cookie : info.getCookies()) {
//                method.addRequestHeader("Cookie", cookie);
//            }
//        }
//
//    }

    /**
     * Pull session information out of an <code>HttpURLConnection</code> response and store it in the passed <code>IDharmaAuthentication</code>.
     * <p/>
     * This is to be called after the connection has been sent a request and has received a response.
     */
//    public static void saveSessionInfo(HttpURLConnection connection,
//                                       IDharmaAuthentication auth) {
//        if (connection == null) {
//            throw new IllegalArgumentException("connection should not be null");
//        }
//        if (auth == null) {
//            throw new IllegalArgumentException("auth should not be null");
//        }
//
//        SessionInfo sessionInfo = new SessionInfo();
//        Map<String, List<String>> headerMap = connection.getHeaderFields();
//        if (headerMap != null) {
//            for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
//                if (entry.getKey() != null && entry.getKey().equals("Set-Cookie")) {
//                    List<String> setCookies = entry.getValue();
//                    if (setCookies != null) {
//                        for (String setCookie : setCookies) {
//                            sessionInfo.addCookie(setCookie);
//                        }
//                    }
//                }
//            }
//        }
//
//        auth.addDetails(sessionInfo);
//    }

    public static class UserInfo {
        private final String qname;
        private final String username;
        private final String realm;

        // package private
        UserInfo(String qname, String username, String realm) {
            this.qname = new String(qname);
            this.username = new String(username);
            this.realm = realm != null ? new String(realm) : null;
        }

        /**
         * @return the user fully qualified name. It might be the same as username
         * if there is no realm...
         */
        public final String getQName() {
            return qname;
        }

        /**
         * @return the username. It might be the same as the fully qualified name
         * if there is no realm
         */
        public final String getUsername() {
            return username;
        }

        /**
         * @return the realm. It can be null when there is no realm.
         */
        public final String getRealm() {
            return realm;
        }
    }
}
