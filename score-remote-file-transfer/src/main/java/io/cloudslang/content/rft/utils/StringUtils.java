/*
 * Created on Jun 9, 2005 by xban
 */
package io.cloudslang.content.rft.utils;

import java.lang.reflect.Array;

/**
 * @author xban
 */
public class StringUtils {

    public StringUtils() {
    }

    /**
     * Checks if a supplied String is null or empty.
     *
     * @param s String to check
     * @return true if supplied string is null or empty
     */
    public static boolean isNull(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0);
    }

    public static boolean isBlank(String input) {
        if (input == null) {
            return true;
        }
        int len = input.length();
        for (int i = 0; i < len; i++) {
            if (!isSpace(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tells whether a character is a whitespace according to what we found
     * to be whitespace in the Unicode char database, to which we added some
     * other separators that are understood to be whitespace by Java.
     * <p/>
     * Note that this is different than what Character.isWhitespace(c) would return,
     * in that the standard java function returns what they term "whitespace according to Java",
     * and they explain what that is.
     * <p/>
     * Note that this is also different than what Character.isSpaceChar(c) would return,
     * because that function returns true if the character is a whitespace according
     * to Unicode (26 code points).
     *
     * @param c a character
     * @return true if c is one of the following Unicode thingies:
     * <pre>
     * # PropList-5.1.0.txt
     * # Date: 2008-03-20, 17:55:27 GMT [MD]
     * #
     * # Unicode Character Database
     * # Copyright (c) 1991-2008 Unicode, Inc.
     * # For terms of use, see http://www.unicode.org/terms_of_use.html
     * # For documentation, see UCD.html
     *
     * # ================================================
     *
     * 0009..000D    ; White_Space # Cc   [5] <control-0009>..<control-000D>
     * 0020          ; White_Space # Zs       SPACE
     * 0085          ; White_Space # Cc       <control-0085>
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
     *
     * To these, add 5 extra code points defined by java.lang.Character.isWhitespace()
     * to be whitespace:
     * <ul>
     * <li> <code>'&#92;u001C'</code>, FILE SEPARATOR.
     * <li> <code>'&#92;u001D'</code>, GROUP SEPARATOR.
     * <li> <code>'&#92;u001E'</code>, RECORD SEPARATOR.
     * <li> <code>'&#92;u001F'</code>, UNIT SEPARATOR.
     * <li> <code>'&#92;u200B'</code>, ZERO WIDTH SPACE
     * </ul>
     *
     * </pre>
     * <p/>
     * See also this, if it's still there: http://spreadsheets.google.com/pub?key=pd8dAQyHbdewRsnE5x5GzKQ
     */
    public static boolean isSpace(char c) {
        // calling into Java may be more reliable if new space
        // characters get added by Unicode. The switch ... case version is faster.
        return (Character.isSpaceChar(c) || Character.isWhitespace(c));
        /*
        switch (c) {
        case '\n': // U+000A except you cannot write it like the rest, not even in comments.
        case '\r': // U+000D
        case '\u0009':
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
        case '\u0020':
        case '\u00A0':
        case '\u1680':
        case '\u180E':
        case '\u2000':
        case '\u2001':
        case '\u2002':
        case '\u2003':
        case '\u2004':
        case '\u2005':
        case '\u2006':
        case '\u2007':
        case '\u2008':
        case '\u2009':
        case '\u200A':
        case '\u202F':
        case '\u205F':
        case '\u3000':
        case '\u001C':
        case '\u001D':
        case '\u001E':
        case '\u001F':
        case '\u200B':
            return true;
        }
        return false;
        */
    }

    /**
     * Trims a string consisting of Unicode characters. The method
     * supports surrogate pairs because as far as I can tell there
     * are no white space characters in the astral planes, they
     * seem to all belong to the Basic Multilingual Plane (BMP).
     *
     * @param s the string
     * @return the original string if there are no whitespace chars at either
     * (or both) ends of the string, or a substring that has those whitespaces
     * removed.  Note that the method uses <code>StringUtils.isWhitespace()</code>, not
     * <code>Character.isWhitespace()<code> to determine whitespace!
     */
    public static String trim(String s) {
        if (s == null) {
            return s;
        }
        int len = s.length();
        if (len == 0) {
            return s;
        }

        // NOTE:
        // The reason this works for all characters including surrogate
        // pairs is because we only iterate over space chars, and I assume
        // that whitespace chars are always 2 bytes long and there are no
        // whitespaces in astral planes. So the routine never gets as far
        // as hitting one of those, becaue it will first detect the bytes as
        // not representing a white space.

        // work from the start until sp points to a non-blank char
        int sp = 0;
        char c = 0;
        while (sp < len) {
            c = s.charAt(sp);
            if (c <= ' ' || isSpace(c)) {
                sp++;
            } else {
                break;
            }
        }
        // work from the end until ep-1 points to a non-blank char
        int ep = len;
        while (ep > sp) {
            c = s.charAt(ep - 1);
            if (c <= ' ' || isSpace(c)) {
                ep--;
            } else {
                break;
            }
        }
        return ((sp > 0 || ep < len) ? s.substring(sp, ep) : s);
    }

    /**
     * trims all the trailing characters of a certain type from the end
     * of a string.
     *
     * @param s            the input string, can be null
     * @param trailingChar the character to be trimmed
     * @return the trimmed string if it has such trailing characters,
     * or the original string otherwise.
     */
    public static String trimTrailing(String s, char trailingChar) {
        if (isEmpty(s)) {
            return s;
        }
        int len = s.length();
        int pos = len;
        while (pos > 1 && (s.charAt(pos - 1) == trailingChar)) {
            pos--;
        }
        if (pos == len) {
            return s;
        }
        return s.substring(0, pos);
    }

    /**
     * @return o.toString or defaultValue if o is null
     */
    public static String valueOf(Object o, String defaultValue) {
        return (o == null ? defaultValue : o.toString());
    }

    /**
     * turns an array into a string. If you have well-defined arrays, use Arrays.toString() instead.
     *
     * @param o the object, if null empty string is returned
     * @return stringified object.
     */
    public static String arrayToString(Object o) {
        return arrayToString(o, " ");
    }

    /**
     * turns an array into a string. If you have well-defined arrays, use Arrays.toString() instead.
     *
     * @param arrayObj  the object, if null empty string is returned, if not an array
     *                  then the output of its <code>toString()</code> method is returned.
     * @param separator separator to be used, if null space is used
     * @return stringified object.
     */
    public static String arrayToString(Object arrayObj, String separator) {
        if (arrayObj == null)
            return "";

        if (!arrayObj.getClass().isArray())
            return arrayObj.toString();

        String sep = (separator == null ? " " : separator);
        int len = Array.getLength(arrayObj);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < len; i++) {
            buf.append(String.valueOf(Array.get(arrayObj, i)));
            if (i < len - 1)
                buf.append(sep);
        }
        return buf.toString();
    }
}
