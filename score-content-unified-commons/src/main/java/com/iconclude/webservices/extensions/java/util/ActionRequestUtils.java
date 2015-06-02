/*
 * Created on Mar 15, 2006
 *
 */
package com.iconclude.webservices.extensions.java.util;

import com.iconclude.dharma.commons.security.DharmaSecurityToken;
import com.iconclude.dharma.commons.security.SecurityTokenHelper;
import com.iconclude.dharma.commons.security.krb5.Krb5Utils;
import com.iconclude.dharma.commons.util.IEntityResolvable;
import com.iconclude.dharma.commons.util.IEntityResolver;
import com.iconclude.dharma.commons.util.IdentityEntityResolver;
import com.iconclude.webservices.extensions.java.types.ActionRequest;
import com.iconclude.webservices.extensions.java.types.MapEntry;
import org.apache.commons.codec.DecoderException;

import javax.security.auth.kerberos.KerberosTicket;
import java.text.ParseException;
import java.util.*;

public class ActionRequestUtils {
    private static final boolean _initialized = _init();
    /**
     * A holder class that holds a thread local containing the current
     * information grabbed from the SOAP request that comes from a RAS client.
     * For example the information may contain localization details about
     * the RAS client (which is usually a Central server).
     */
    private static final ThreadLocal<com.iconclude.webservices.extensions.java.types.Map> clientRequestInfo = new ThreadLocal<com.iconclude.webservices.extensions.java.types.Map>();
    private static Map<String, IEntityResolver> _resolvers;
    private static IEntityResolver usernameResolver;
    private static IEntityResolver passwordResolver;
    private static IEntityResolver krb5ticketsResolver;
    private static IEntityResolver krb5keysResolver;

    private static boolean _init() {
        // TODO right now mappings for "username", "user", "password" and "pass" are hardcoded; when we extend the RAS API
        // and not only define what parameter types an action takes but also their names, this
        // needs to be revisited and made generic
        _resolvers = new HashMap<String, IEntityResolver>();

        // the first parameter of the IdentityEntityResolver is the key that it used to push
        // a particular value into the security token and they are not controlled by the user.
        // _resolvers map a context key to the resolver; for all purposes, the context key
        // is the name of the input bound to the security token...
        usernameResolver = new IdentityEntityResolver("username"/*this resolves username off the identity*/);
        passwordResolver = new IdentityEntityResolver("password"/*this resolves password off the identity*/);
        krb5ticketsResolver = new IdentityEntityResolver("krb5tickets"/*this resolves Kerberos5 ticket(s) off the identity*/);
        krb5keysResolver = new IdentityEntityResolver("krb5keys"/*this resolves Kerberos5 key(s) off the identity*/);
        // should we have "case insensitive keys"???
        _resolvers.put("username", usernameResolver);
        _resolvers.put("Username", usernameResolver);
        _resolvers.put("user", usernameResolver);
        _resolvers.put("User", usernameResolver);
        _resolvers.put("userName", usernameResolver);

        _resolvers.put("password", passwordResolver);
        _resolvers.put("Password", passwordResolver);
        _resolvers.put("pass", passwordResolver);
        _resolvers.put("Pass", passwordResolver);

        _resolvers.put("krb5tickets", krb5ticketsResolver);
        _resolvers.put("KerberosTickets", krb5ticketsResolver);
        _resolvers.put("KrbTickets", krb5ticketsResolver);
        _resolvers.put("KerberosTicket", krb5ticketsResolver);
        _resolvers.put("KrbTicket", krb5ticketsResolver);

        _resolvers.put("krb5keys", krb5keysResolver);
        _resolvers.put("KerberosKeys", krb5keysResolver);
        _resolvers.put("KrbKeys", krb5keysResolver);
        _resolvers.put("KerberosKey", krb5keysResolver);
        _resolvers.put("KrbKey", krb5keysResolver);

        return true;
    }

    public static void addResolver(String param, IEntityResolver resolver) {
        if (param == null)
            return;
        _resolvers.put(param, resolver);
    }

    /**
     * Resolves a string parameter from the request
     *
     * @param request
     * @param param
     * @return the parameter value or null, if non existent
     */
    public static Object resolveParam(ActionRequest request, String param) {
        if (null == request || null == param)
            throw new IllegalArgumentException("null passed to ActionRequestUtils.resolveParam");
        if (!_initialized)
            throw new IllegalStateException("Internal error (ActionRequestUtils could not be initialized)");

        Object paramObj = request.getParameters().map(param);

        if (paramObj instanceof IEntityResolvable) {
            IEntityResolver resolver = _resolvers.get(param);
            if (null == resolver) {
                if (param.toLowerCase().contains("pass"))
                    resolver = passwordResolver;
                if (param.toLowerCase().contains("user") || param.toLowerCase().contains("login"))
                    resolver = usernameResolver;
                else if (param.toLowerCase().contains("key"))
                    resolver = krb5keysResolver;
                else if (param.toLowerCase().contains("ticket"))
                    resolver = krb5ticketsResolver;
            }

            if (null != resolver)
                return resolver.resolve((IEntityResolvable) paramObj, null);
        }
        return paramObj;
    }

    public static String resolveStringParam(ActionRequest request, String param) {
        Object paramObj = resolveParam(request, param);
        if (null == paramObj)
            return null;
        return String.valueOf(paramObj);
    }

    /**
     * attempts to find kerberos tickets hidden inside a "password" field in the request. If it finds a password
     * entry and that password entry is a security token and the security token contains tickets, then it unwraps
     * them, parses them and puts them in a collection.
     *
     * @param request the action request, cannot be null
     * @return a collection of KerberosTicket objects, or empty collection if nothing is found.
     */
    public static Collection<KerberosTicket> collectKerberosTicketsFromPassword(ActionRequest request) {
        Object pwd = request.getParameters().map("password");
        Set<KerberosTicket> tickets = null;
        if (pwd instanceof DharmaSecurityToken) {
            DharmaSecurityToken tok = (DharmaSecurityToken) pwd;
            String stringifiedTickets = SecurityTokenHelper.getSecurityValue(tok, "krb5tickets");
            if (stringifiedTickets != null) {
                try {
                    tickets = Krb5Utils.parseKerberosTickets(stringifiedTickets);
                } catch (DecoderException e) {
                    // TODO: log this
                } catch (ParseException e) {
                    // TODO: log this
                }
            }
        }
        if (tickets == null) {
            tickets = Collections.emptySet();
        }
        return tickets;
    }

    /**
     * @return the Locale transmitted by the client of the current SOAP request (if any), null if no locale could be extracted.
     * <p/>
     * <p>WARNING: this call makes no sense unless it's on the stack of a SOAP request. It employs thread local storage
     * to store the information carried by a SOAP request, so using it from somewhere else does not make much sense.
     */
    public static Locale getClientRequestLocale() {
        com.iconclude.webservices.extensions.java.types.Map requestInfo = getClientRequestInfo();
        if (requestInfo == null) {
            return null;
        }
        MapEntry[] entries = requestInfo.getEntries();
        if (entries == null || entries.length == 0) {
            return null;
        }
        String lang = null;
        String country = "";
        String variant = "";
        for (MapEntry entry : requestInfo.getEntries()) {
            if ("Locale.language".equals(entry.getName())) {
                Object tmp = entry.getValue();
                if (tmp != null) {
                    lang = tmp.toString();
                }
            } else if ("Locale.country".equals(entry.getName())) {
                Object tmp = entry.getValue();
                if (tmp != null) {
                    country = tmp.toString();
                }
            } else if ("Locale.variant".equals(entry.getName())) {
                Object tmp = entry.getValue();
                if (tmp != null) {
                    variant = tmp.toString();
                }
            }
        }
        return lang == null ? null : new Locale(lang, country, variant);
    }

    /**
     * <p/>
     * WARNING: this call makes no sense unless it's on the stack of a SOAP request. It employs thread local storage
     * to store the information carried by a SOAP request, so using it from somewhere else does not make much sense.
     *
     * @return information about the current SOAP request.
     */
    public static com.iconclude.webservices.extensions.java.types.Map getClientRequestInfo() {
        return clientRequestInfo.get();
    }

    /**
     * establish the client information for the current request by storing it into a thread local.
     */
    public static com.iconclude.webservices.extensions.java.types.Map setClientRequestInfo(com.iconclude.webservices.extensions.java.types.Map info) {
        com.iconclude.webservices.extensions.java.types.Map oldInfo = clientRequestInfo.get();
        clientRequestInfo.set(info);
        return oldInfo;
    }

    /**
     * clears the thread local storage of the current request's client information.
     */
    public static void clearClientRequestInfo() {
        clientRequestInfo.set(null);
    }

}
