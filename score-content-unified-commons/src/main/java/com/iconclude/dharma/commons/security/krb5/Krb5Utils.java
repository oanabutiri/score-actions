/*
 * Copyright (c) iConclude 2006
 * All rights reserved.
 */
package com.iconclude.dharma.commons.security.krb5;

import com.iconclude.dharma.commons.util.CollectionUtils;
import com.iconclude.dharma.commons.util.Dharma;
import com.iconclude.dharma.commons.util.StringUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author octavian
 */
public class Krb5Utils {

    private static final int FORWARDABLE_TICKET_FLAG = 1;
    private static final int FORWARDED_TICKET_FLAG = 2;
    private static final int PROXIABLE_TICKET_FLAG = 3;
    private static final int PROXY_TICKET_FLAG = 4;
    private static final int POSTDATED_TICKET_FLAG = 6;
    private static final int RENEWABLE_TICKET_FLAG = 8;
    private static final int INITIAL_TICKET_FLAG = 9;
    private static final int NUM_FLAGS = 32;

    public static String makeString(KerberosTicket ticket) {
        if (null == ticket)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Utils.NullTicketError")); //$NON-NLS-1$

        if (ticket.isDestroyed())
            throw new IllegalStateException(Dharma.msg("Dharma.security.krb5.Krb5Utils.InvalidTicketError")); //$NON-NLS-1$
        StringBuffer caddrBuf = new StringBuffer();
        InetAddress[] clientAddresses = ticket.getClientAddresses();
        if (clientAddresses != null) {
            for (int i = 0; i < clientAddresses.length; i++) {
                caddrBuf.append(clientAddresses[i].toString());
                if (i < clientAddresses.length - 1)
                    caddrBuf.append(" "); //$NON-NLS-1$
            }
        }

        StringBuffer sessBuf = new StringBuffer();
        sessBuf.append("keyType:").append(ticket.getSessionKeyType()) //$NON-NLS-1$
                .append(" keyBytes:").append(Hex.encodeHex(ticket.getSessionKey().getEncoded())); //$NON-NLS-1$

        StringBuffer buff = new StringBuffer(1024);
        buff.append("Ticket = ").append(Hex.encodeHex(ticket.getEncoded())).append('\n') //$NON-NLS-1$
                .append("Client Principal = ").append(ticket.getClient().toString()).append('\n') //$NON-NLS-1$
                .append("Server Principal = ").append(ticket.getServer().toString()).append('\n') //$NON-NLS-1$
                .append("Session Key = ").append(sessBuf.toString()).append('\n') //$NON-NLS-1$
                .append("Forwardable Ticket = ").append(ticket.getFlags()[FORWARDABLE_TICKET_FLAG]).append('\n') //$NON-NLS-1$
                .append("Forwarded Ticket = ").append(ticket.getFlags()[FORWARDED_TICKET_FLAG]).append('\n') //$NON-NLS-1$
                .append("Proxiable Ticket = ").append(ticket.getFlags()[PROXIABLE_TICKET_FLAG]).append('\n') //$NON-NLS-1$
                .append("Proxy Ticket = ").append(ticket.getFlags()[PROXY_TICKET_FLAG]).append('\n') //$NON-NLS-1$
                .append("Postdated Ticket = ").append(ticket.getFlags()[POSTDATED_TICKET_FLAG]).append('\n') //$NON-NLS-1$
                .append("Renewable Ticket = ").append(ticket.getFlags()[RENEWABLE_TICKET_FLAG]).append('\n') //$NON-NLS-1$
                .append("Initial Ticket = ").append(ticket.getFlags()[INITIAL_TICKET_FLAG]).append('\n') //$NON-NLS-1$
                .append("Auth Time = ").append(ticket.getAuthTime().toString()).append('\n') //$NON-NLS-1$
                .append("Start Time = ").append(ticket.getStartTime().toString()).append('\n') //$NON-NLS-1$
                .append("End Time = ").append(ticket.getEndTime().toString()).append('\n') //$NON-NLS-1$
                .append("Renew Till = ").append(StringUtils.valueOf(ticket.getRenewTill(), "")).append('\n') //$NON-NLS-1$ //$NON-NLS-2$
                .append("Client Addresses = ").append(caddrBuf.toString()).append('\n'); //$NON-NLS-1$
        return buff.toString();
    }

    public static String makeTicketsString(Collection<KerberosTicket> tickets) {
        if (null == tickets)
            throw new IllegalArgumentException("null tickets"); //$NON-NLS-1$

        Iterator<KerberosTicket> iter = tickets.iterator();
        StringBuffer buff = new StringBuffer(1024);
        buff.append('[');
        while (iter.hasNext()) {
            KerberosTicket ticket = iter.next();
            buff.append(makeString(ticket));
            if (iter.hasNext())
                buff.append(", "); //$NON-NLS-1$
        }
        buff.append(']');
        return buff.toString();
    }

    public static String makeString(KerberosKey key) {
        if (null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Utils.NullKeyError")); //$NON-NLS-1$
        if (key.isDestroyed())
            throw new IllegalStateException(Dharma.msg("Dharma.security.krb5.Krb5Utils.InvalidKeyError")); //$NON-NLS-1$

        StringBuffer buff = new StringBuffer(1024);
        buff.append("Kerberos Principal = ").append(key.getPrincipal().getName()).append('\n') //$NON-NLS-1$
                .append("Key Version = ").append(key.getVersionNumber()).append('\n') //$NON-NLS-1$
                .append("Key Type = ").append(key.getKeyType()).append('\n') //$NON-NLS-1$
                .append("Encryption Key = ").append(Hex.encodeHex(key.getEncoded())).append('\n'); //$NON-NLS-1$

        return buff.toString();
    }

    public static String makeKeysString(Collection<KerberosKey> keys) {
        if (null == keys)
            throw new IllegalArgumentException("null keys"); //$NON-NLS-1$

        Iterator<KerberosKey> iter = keys.iterator();
        StringBuffer buff = new StringBuffer(1024);
        buff.append('[');
        while (iter.hasNext()) {
            KerberosKey key = iter.next();
            buff.append(makeString(key));
            if (iter.hasNext())
                buff.append(", "); //$NON-NLS-1$
        }
        buff.append(']');
        return buff.toString();
    }

    /**
     * Parses the string representation obtained from makeString
     *
     * @param ticketsStr
     * @return
     * @throws org.apache.commons.codec.DecoderException
     * @throws ParseException
     */
    public static Set<KerberosTicket> parseKerberosTickets(String ticketsStr) throws DecoderException, ParseException {
        Set<KerberosTicket> ret = new HashSet<KerberosTicket>();
        if (null == ticketsStr)
            return ret;

        // eliminate the starting and ending []
        ticketsStr = ticketsStr.substring(1, ticketsStr.length() - 1);
        if (ticketsStr.length() == 0)
            return ret;
        // we use ", " as separator
        String[] segments = ticketsStr.split(", "); //$NON-NLS-1$
        for (String segment : segments) {
            KerberosTicket ticket = parseKerberosTicket(segment);
            if (null != ticket)
                ret.add(ticket);
        }
        return ret;
    }

    /**
     * Parses the string representation obtained from makeString
     *
     * @param ticketStr
     * @return
     * @throws org.apache.commons.codec.DecoderException
     * @throws ParseException
     */
    public static KerberosTicket parseKerberosTicket(String ticketStr) throws DecoderException, ParseException {
        if (null == ticketStr)
            return null;
        String[] segments = ticketStr.split("\n"); //$NON-NLS-1$
        if (null == segments || segments.length == 0)
            return null;
        byte[] asn1Encoding = null, keyBytes = null;
        KerberosPrincipal client = null, server = null;
        Date authTime = null, startTime = null, endTime = null, renewTill = null;
        InetAddress[] clientAddresses = null;
        int keyType = -1;
        boolean[] flags = new boolean[NUM_FLAGS];
        for (int i = 0; i < flags.length; ++i)
            flags[i] = false;

        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); //$NON-NLS-1$

        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].startsWith("Ticket = ")) { //$NON-NLS-1$
                asn1Encoding = Hex.decodeHex(segments[i].substring("Ticket = ".length()).toCharArray()); //$NON-NLS-1$
            } else if (segments[i].startsWith("Client Principal = ")) { //$NON-NLS-1$
                client = new KerberosPrincipal(segments[i].substring("Client Principal = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Server Principal = ")) { //$NON-NLS-1$
                server = new KerberosPrincipal(segments[i].substring("Server Principal = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Forwardable Ticket = ")) { //$NON-NLS-1$
                flags[FORWARDABLE_TICKET_FLAG] =
                        Boolean.parseBoolean(segments[i].substring("Forwardable Ticket = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Forwarded Ticket = ")) { //$NON-NLS-1$
                flags[FORWARDED_TICKET_FLAG] =
                        Boolean.parseBoolean(segments[i].substring("Forwarded Ticket = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Proxiable Ticket = ")) { //$NON-NLS-1$
                flags[PROXIABLE_TICKET_FLAG] =
                        Boolean.parseBoolean(segments[i].substring("Proxiable Ticket = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Proxy Ticket = ")) { //$NON-NLS-1$
                flags[PROXY_TICKET_FLAG] =
                        Boolean.parseBoolean(segments[i].substring("Proxy Ticket = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Postdated Ticket = ")) { //$NON-NLS-1$
                flags[POSTDATED_TICKET_FLAG] =
                        Boolean.parseBoolean(segments[i].substring("Postdated Ticket = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Renewable Ticket = ")) { //$NON-NLS-1$
                flags[RENEWABLE_TICKET_FLAG] =
                        Boolean.parseBoolean(segments[i].substring("Renewable Ticket = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Initial Ticket = ")) { //$NON-NLS-1$
                flags[INITIAL_TICKET_FLAG] =
                        Boolean.parseBoolean(segments[i].substring("Initial Ticket = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Auth Time = ")) { //$NON-NLS-1$
                authTime = df.parse(segments[i].substring("Auth Time = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Start Time = ")) { //$NON-NLS-1$
                startTime = df.parse(segments[i].substring("Start Time = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("End Time = ")) { //$NON-NLS-1$
                endTime = df.parse(segments[i].substring("End Time = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Renew Till = ")) { //$NON-NLS-1$
                String renew = segments[i].substring("Renew Till = ".length()).trim(); //$NON-NLS-1$
                if (renew.length() != 0)
                    renewTill = df.parse(renew);
            } else if (segments[i].startsWith("Client Addresses = ")) { //$NON-NLS-1$
                String addrStr = segments[i].substring("Client Addresses = ".length()); //$NON-NLS-1$
                if (addrStr.length() != 0) {
                    String[] addrSeg = addrStr.split(" "); //$NON-NLS-1$
                    if (addrSeg != null && addrSeg.length != 0) {
                        clientAddresses = new InetAddress[addrSeg.length];
                        for (int j = 0; j < addrSeg.length; ++j) {
                            if (addrSeg[j].length() == 0)
                                continue;
                            try {
                                clientAddresses[j] = InetAddress.getByName(addrSeg[j]);
                            } catch (UnknownHostException ignore) {
                            }
                        }
                    }
                }
            } else if (segments[i].startsWith("Session Key = ")) { //$NON-NLS-1$
                String sessStr = segments[i].substring("Session Key = ".length()); //$NON-NLS-1$
                if (sessStr.length() != 0) {
                    String[] sessSeg = sessStr.split(" "); //$NON-NLS-1$
                    for (String sess : sessSeg) {
                        if (sess.startsWith("keyType:")) //$NON-NLS-1$
                            keyType = Integer.parseInt(sess.substring("keyType:".length())); //$NON-NLS-1$
                        else if (sess.startsWith("keyBytes:")) //$NON-NLS-1$
                            keyBytes = Hex.decodeHex(sess.substring("keyBytes:".length()).toCharArray()); //$NON-NLS-1$
                    }
                }
            }
        }
        return new KerberosTicket(asn1Encoding, client, server, keyBytes, keyType,
                flags, authTime, startTime, endTime, renewTill, clientAddresses);
    }

    public static Set<KerberosKey> parseKerberosKeys(String keysStr) throws DecoderException {
        Set<KerberosKey> ret = new HashSet<KerberosKey>();
        if (null == keysStr)
            return ret;

        // eliminate the starting and ending []
        keysStr = keysStr.substring(1, keysStr.length() - 1);
        if (keysStr.length() == 0)
            return ret;
        // we use ", " as separator
        String[] segments = keysStr.split(", "); //$NON-NLS-1$
        for (String segment : segments) {
            KerberosKey key = parseKerberosKey(segment);
            if (null != key)
                ret.add(key);
        }
        return ret;
    }

    public static KerberosKey parseKerberosKey(String keyStr) throws DecoderException {
        if (null == keyStr)
            return null;
        String[] segments = keyStr.split("\n"); //$NON-NLS-1$
        if (null == segments || segments.length == 0)
            return null;

        KerberosPrincipal principal = null;
        int keyVersion = -1, keyType = -1;
        byte[] keyBytes = null;
        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].startsWith("Kerberos Principal = ")) { //$NON-NLS-1$
                principal = new KerberosPrincipal(segments[i].substring("Kerberos Principal = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Key Version = ")) { //$NON-NLS-1$
                keyVersion = Integer.parseInt(segments[i].substring("Key Version = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Key Type = ")) { //$NON-NLS-1$
                keyType = Integer.parseInt(segments[i].substring("Key Type = ".length())); //$NON-NLS-1$
            } else if (segments[i].startsWith("Encryption Key = ")) { //$NON-NLS-1$
                keyBytes = Hex.decodeHex(segments[i].substring("Encryption Key = ".length()).toCharArray()); //$NON-NLS-1$
            }
        }
        return new KerberosKey(principal, keyBytes, keyType, keyVersion);
    }

    /**
     * Creates a Subject given a user name and a bunch of Kerberos tickets. There is no verification done to see
     * if the username and the tickets make sense together; it simply creates principals for the username and
     * each of the realms in the ticket, and makes a subject using the principals and the collection of tickets
     * as private credentials.
     *
     * @param username the username cannot be null or blank
     * @param tickets  collection of tickets
     * @return null if collection of tickets is empty
     * @throws IllegalArgumentException if username is null or blank
     */
    public static Subject getSubjectForTickets(String username, Collection<KerberosTicket> tickets) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.krb5.Krb5Utils.BlankUsernameError")); //$NON-NLS-1$
        }
        if (CollectionUtils.isEmpty(tickets)) {
            return null;
        }
        Set<KerberosPrincipal> principals = new HashSet<KerberosPrincipal>();
        for (KerberosTicket ticket : tickets) {
            if (ticket != null) {
                KerberosPrincipal srvPrincipal = ticket.getServer();
                principals.add(new KerberosPrincipal(username + "@" + srvPrincipal.getRealm())); //$NON-NLS-1$
            }
        }
        Set<?> pubCredentials = new HashSet();
        Set<KerberosTicket> privCredentials = new HashSet<KerberosTicket>(tickets);
        Subject subj = new Subject(false, principals, pubCredentials, privCredentials);
        return subj;
    }
}
