/*
 * Created on Aug 12, 2005 by xban
 */
package com.iconclude.dharma.commons.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * general utility methods which don't fit anywhere else.
 *
 * @author xban
 */
public final class Dharma {

    public static final long MILLISEC_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * POSIX-compliant UNICODE regex to match a letter
     */
    public static final String MATCH_LETTER = "\\p{L}"; //$NON-NLS-1$

    /**
     * POSIX-compliant UNICODE regex to match a number
     */
    public static final String MATCH_NUMBER = "\\p{N}"; //$NON-NLS-1$

    /**
     */
    public static final String MATCH_CONTROL_WHITESPACE = "\\u0009\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029"; // TAB, LF, Vertical TAB, Form Feed, CR, New Line, Line Sep, Paragraph Sep //$NON-NLS-1$

    /**
     */
    public static final String MATCH_HORIZONTAL_WHITESPACE = "\\u0020\\u00A0\\u1680\\u180E\\u2000\\u2001\\u2002\\u2003\\u2004\\u2005\\u2006\\u2007\\u2008\\u2009\\u200A\\u202F\\u205F\\u3000"; //$NON-NLS-1$
    /**
     * there are 26 Unicode codepoints deemed "white space" defined in the Unicode Character DB
     * (see http://www.unicode.org/Public/UNIDATA/PropList.txt):
     * <p/>
     * <pre>
     * 0009..000D    ; White_Space # Cc   [5] <control-0009>..<control-000D>
     * 0020          ; White_Space # Zs       SPACE
     * 0085          ; White_Space # Cc       <control-0085> - NEL
     * 00A0          ; White_Space # Zs       NO-BREAK SPACE
     * 1680          ; White_Space # Zs       OGHAM SPACE MARK
     * 180E          ; White_Space # Zs       MONGOLIAN VOWEL SEPARATOR
     * 2000..200A    ; White_Space # Zs  [11] EN QUAD..HAIR SPACE
     * 2028          ; White_Space # Zl       LINE SEPARATOR
     * 2029          ; White_Space # Zp       PARAGRAPH SEPARATOR
     * 202F          ; White_Space # Zs       NARROW NO-BREAK SPACE
     * 205F          ; White_Space # Zs       MEDIUM MATHEMATICAL SPACE
     * 3000          ; White_Space # Zs       IDEOGRAPHIC SPACE
     *
     * # Total code points: 26
     * </pre>
     * <p/>
     * The codepoints in the block from 2000 to 200A have the following meaning:
     * <p/>
     * - En Quad (U+2000)
     * - Em Quad (U+2001)
     * - En Space (U+2002)
     * - Em Space (U+2003)
     * - Three-Per-Em Space (U+2004)
     * - Four-Per-Em Space (U+2005)
     * - Six-Per-Em Space (U+2006)
     * - Figure Space (U+2007)
     * - Punctuation Space (U+2008)
     * - Thin Space (U+2009)
     * - Hair Space (U+200A)
     * - Mathematical Space (U+205F)
     * <p/>
     * The Java Pattern machine does not accept \p{Whitespace} as a valid POSIX
     * Unicode expr (although it is, check http://unicode.org/reports/tr18/).
     * So I am hacking this in manually. The classess are split so we can combine
     * them more effectively.
     */
    public static final String MATCH_ALL_WHITESPACE = MATCH_CONTROL_WHITESPACE + MATCH_HORIZONTAL_WHITESPACE;
    /**
     * Unicode line boundaries are also messed in Pattern. From the Unicode site:
     * "To meet this requirement, if an implementation provides for line-boundary testing,
     * it shall recognize not only CRLF, LF, CR, but also NEL (U+0085), PS (U+2029) and LS (U+2028)"
     */
    public static final String MATCH_LINE_BOUNDARIES = "\\u000A\\u000D\\u0085\\u2028\\u2029"; //$NON-NLS-1$

    public static Locale getLocale() {
        return LocaleDetails.locale;
    }

    public static DateFormat getDateTimeFormat() {
        return makeLocalizedDateFormat(LocaleDetails.dateFormatStringNoSeconds, new SimpleDateFormat("MM/yy/dd HH:mm:ss"));
    }

    public static DateFormat getDateTimeFormatNoSeconds() {
        return makeLocalizedDateFormat(LocaleDetails.dateFormatStringNoSeconds, new SimpleDateFormat("MM/yy/dd HH:mm"));
    }

    /**
     * @return a date formatter for use in charts for displaying daily tickmarks
     */
    public static DateFormat getDailyFormat() {
        return makeLocalizedDateFormat(LocaleDetails.dailyFormatString, new SimpleDateFormat("MMM-dd"));
    }

    /**
     * @return a date formatter for use in charts for displaying monthly tickmarks
     */
    public static DateFormat getMonthlyFormat() {
        return makeLocalizedDateFormat(LocaleDetails.monthlyFormatString, new SimpleDateFormat("MMM-yy"));
    }

    public static DateFormat makeLocalizedDateFormat(String fmt, DateFormat defaultFormat) {
        if (fmt == null) {
            return null;
        }
        // need to return a new object every time because formats are not thread-safe.
        DateFormat result = null;
        try {
            result = new SimpleDateFormat(fmt, LocaleDetails.locale);
        } catch (Exception e) {
            // if there is _any_ failure to get the format specified in the localized messages, use default
            Log _log = LogFactory.getLog(Dharma.class);
            _log.warn("Failed to parse date/time format " + fmt, e);
            result = (defaultFormat == null ? new SimpleDateFormat() : defaultFormat);
        }
        return result;
    }

    /**
     * makes the current thread sleep for a number of milliseconds, without
     * regard to interrupted exceptions. If they happen, then the sleep is
     * interrupted.
     *
     * @param millis how many milliseconds to sleep for.
     * @return how many millis were actually slept. This may be more than millis
     * passed in due to thread scheduling. It can also be less in case the sleep
     * was interrupted.
     */
    public static long sleep(long millis) {
        long start = System.nanoTime();
        long end = start;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
            // make sure not to eat the interruption so that the callers
            // of the method actually have a chance to check it.
            Thread.currentThread().interrupt();
        } finally {
            end = System.nanoTime();
        }
        return Math.round((end - start) / (1000d * 1000d));
    }

    /**
     * closes any IO object that supports the Closeable interface.
     *
     * @param <T>   the object type
     * @param ioObj the object reference, it can be null.
     */
    public static final <T extends Closeable> void close(T ioObj) {
        if (ioObj != null) {
            try {
                ioObj.close();
            } catch (Throwable ignore) {
            }
        }
    }

    /**
     * closes any IO objects that supports the Closeable interface.
     *
     * @param <T> the object type
     */
    public static final <T extends Closeable> void close(T... ioObjects) {
        for (T ioObj : ioObjects) {
            close(ioObj);
        }
    }

    /**
     * walks a trace stack and applies the predicate to each throwable.
     *
     * @param t       the throwable
     * @param checker the predicate
     * @return true if the throwable is not null and the checker is not null
     * and the checker returns true for any of the throwables in the trace.
     */
    public static final boolean walkThrowable(Throwable t, IPredicate<Throwable> checker) {
        if (t == null) {
            return false;
        }
        if (checker == null) {
            return false;
        }
        Map<Object, Object> visitedObjects = new IdentityHashMap<Object, Object>();
        while (t != null) {
            // check to see if we've seen this throwable before.
            if (visitedObjects.containsKey(t)) {
                return false;
            }
            visitedObjects.put(t, null);

            // have not seen it, check if it's our class.
            if (checker.evaluate(t)) {
                return true;
            }

            // move on to the next one.
            t = t.getCause();
        }
        return false;
    }

    public static final boolean traceContainsException(final Throwable t, final Class<?> exceptionClass) {
        if (exceptionClass == null) {
            return false;
        }
        return walkThrowable(t, new IPredicate<Throwable>() {
            public boolean evaluate(Throwable source) {
                return exceptionClass.isAssignableFrom(source.getClass());
            }
        });
    }

    public static final String getMessage(Throwable t) {
        String message = t.getMessage();
        while ((t = t.getCause()) != null) {
            message = t.getMessage();
        }
        return message;
    }

    /**
     * @param idate    [in] - installation date
     * @param validity [in] - nof days the product is valid; -1 means it never expires
     * @param msg      [out] - returns an expiration message; this is an empty string if the
     *                 product never expires
     * @return true if the product is considered expired, false otherwise
     */
    public static boolean getExpirationInfo(Date idate, Long validity, String[] msg) {
        if (null == msg || msg.length == 0)
            throw new IllegalArgumentException(Dharma.msg("Dharma.util.Dharma.NullMessageError")); //$NON-NLS-1$
        if (null == validity || null == idate) {
            // the expiration info was tampered with; consider the product expired
            msg[0] = Dharma.msg("Dharma.util.Dharma.ProductExpiredMessage"); //$NON-NLS-1$
            return true;
        }
        if (validity.longValue() == -1L) {
            msg[0] = ""; // -1 means it never expires //$NON-NLS-1$
            return false;
        }
        long currTime = System.currentTimeMillis();
        long expTime = idate.getTime() + validity.longValue() * MILLISEC_PER_DAY;
        if (currTime >= expTime) {
            msg[0] = Dharma.msg("Dharma.util.Dharma.ProductExpiredMessage"); //$NON-NLS-1$
            return true;
        } else {
            long remaining = validity.longValue() - (currTime - idate.getTime()) / MILLISEC_PER_DAY;
            if (remaining <= 0)
                remaining = 1; // when less than a day, display one day...
            msg[0] = Dharma.msg("Dharma.util.Dharma.DaytillExpiredExpiredMessage") + Long.toString(remaining) //$NON-NLS-1$
                    + (remaining == 1 ? Dharma.msg("Dharma.util.Dharma.Day") : Dharma.msg("Dharma.util.Dharma.Days")); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
    }

    public static String msg(String key) {
        return LocalizedStringLookup.lookupMsg(key);
    }

    public static String msg(String key, Object... params) {
        return LocalizedStringLookup.lookupMsg(key, params);
    }

    public static String msg(String key, Locale loc) {
        return LocalizedStringLookup.lookupMsg(key, loc);
    }

    public static String msg(String key, Locale loc, Object... params) {
        return LocalizedStringLookup.lookupMsg(key, params, loc);
    }

    public static Map<String, String> getMessages(String bundleName) {
        Map<String, String> result = new HashMap<String, String>();
        ResourceBundle bundle = LocalizedStringLookup.getMessages(bundleName, null);
        if (bundle != null) {
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                result.put(key, bundle.getString(key));
            }
        }
        return result;
    }

    public static final boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    private static void enforcePropRetrievalInvariants(final Properties props, final String key) {
        if (null == props) {
            throw new IllegalArgumentException(
                    "cannot get value from <null> proeprties"); //$NON-NLS-1$
        }
        if (null == key) {
            throw new IllegalArgumentException(
                    "cannot get value for <null> key"); //$NON-NLS-1$
        }
    }

    /**
     * Given a properties object and a key, retrieves the value of the key as a
     * long. If the key is not found or the value is not a valid long (e.g.
     * 'abc') then returns the defaultValue. This method will throw an
     * unchecked exception (IllegalArgumentException) if props or the key are null.
     *
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static long getLongProperty(Properties props, String key, long defaultValue) {
        enforcePropRetrievalInvariants(props, key);
        long longVal = defaultValue;
        String val = props.getProperty(key);
        if (null == val) {
            return longVal;
        }
        try {
            longVal = Long.parseLong(val);
        } catch (NumberFormatException ex) { /* empty */
        }
        return longVal;
    }

    /**
     * Given a properties object and a key, retrieves the value of the key as an
     * int. If the key is not found or the value is not a valid int (e.g. 'abc')
     * then returns the defaultValue. This method will throw an
     * unchecked exception (IllegalArgumentException) if props or the key are null.
     *
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getIntProperty(Properties props, String key, int defaultValue) {
        enforcePropRetrievalInvariants(props, key);
        int intVal = defaultValue;
        String val = props.getProperty(key);
        if (null == val) {
            return intVal;
        }
        try {
            intVal = Integer.parseInt(val);
        } catch (NumberFormatException ex) { /* empty */
        }
        return intVal;
    }

    /**
     * This call (or equivalently <code>System.nanoTime()/1000000</code>) should be used when
     * computing intervals, because it's based upon hardware timers exposed by the OS, whereas
     * <code>System.currentTimeMillis()</code> is based upon reading the system clock and thus
     * is subject to clock adjustments (either by the user or by the machine if it does NTP
     * or some other time synchronization).
     * <p/>
     * <p>NOTE: if you don't care about the system clock being adjusted from underneath your
     * code, then use <code>System.currentTimeMillis()</code> because it's a lot faster.
     * <p/>
     * <p>See: http://blogs.sun.com/dholmes/entry/inside_the_hotspot_vm_clocks
     *
     * @return the result of <code>System.nanoTime()</code> divided by one million.
     */
    public static long intervalTimeMillis() {
        return System.nanoTime() / 1000000;
    }

    public static final class LocaleDetails {
        static final Locale locale;
        static final String dateFormatString;
        static final String dateFormatStringNoSeconds;
        static final String dailyFormatString;
        static final String monthlyFormatString;

        static {
            Locale loco = Locale.getDefault();
            /*
            try {
                String userLang = System.getProperty("user.language", loco.getLanguage());
                String userRegion = System.getProperty("user.region", loco.getCountry());
                if ("test".equals(userLang)) {
                    if (userRegion.startsWith("JP")) {
                        loco = Locale.JAPAN;
                    } else if (userRegion.startsWith("CH")) {
                        loco = Locale.CHINA;
                    } else if (userRegion.startsWith("KR")) {
                        loco = Locale.KOREA;
                    }
                } else {
                    loco = new Locale(userLang, userRegion);
                }
            } catch (Throwable ignore) {
            }*/
            locale = loco;
            dateFormatString = LocalizedStringLookup.lookupMsg("Dharma.util.Dharma.date_time_format", loco);
            dateFormatStringNoSeconds = LocalizedStringLookup.lookupMsg("Dharma.util.Dharma.date_time_format_no_seconds", loco);
            dailyFormatString = LocalizedStringLookup.lookupMsg("Dharma.util.Dharma.chart.date_time_format_daily", loco);
            monthlyFormatString = LocalizedStringLookup.lookupMsg("Dharma.util.Dharma.chart.date_time_format_monthly", loco);
        }
    }
}
