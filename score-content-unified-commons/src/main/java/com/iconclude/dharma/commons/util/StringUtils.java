/*
 * Created on Jun 9, 2005 by xban
 */
package com.iconclude.dharma.commons.util;

import com.iconclude.dharma.commons.exception.DharmaException;
import org.apache.commons.lang.WordUtils;

import javax.swing.text.NumberFormatter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xban
 */
public class StringUtils {

    public static final Comparator<String> DEFAULT_COMPARATOR = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return StringUtils.compare(o1, o2);
        }
    };
    /**
     * Entities for HTML escaping. Using these instead of the character itself
     * allows us to only use ASCII to output HTML, although it may be a bit
     * of paranoia, since we should encode using UTF-8. There are more than
     * just these entities (which include the basic + ISO8859 entities), but
     * those extra ones will be converted to &#UNICODE anyway.
     * <p>See http://www.w3.org/TR/REC-html40/sgml/entities.html for further
     * reference.
     * <p/>
     * I stole these form Apache Commons. I would have used their routine
     * StringEscapeUtils, were it not for its flaws in handling surrogate pairs.
     */
    @SuppressWarnings("serial")
    public static final Map<Integer, String> NAMED_HTML_ENTITIES = new HashMap<Integer, String>() {
        {
            put(34, "quot"); // " - double-quote
            put(38, "amp"); // & - ampersand
            put(60, "lt"); // < - less-than
            put(62, "gt"); // > - greater-than
            put(160, "nbsp"); // non-breaking space
            put(161, "iexcl"); //inverted exclamation mark
            put(162, "cent"); //cent sign
            put(163, "pound"); //pound sign
            put(164, "curren"); //currency sign
            put(165, "yen"); //yen sign = yuan sign
            put(166, "brvbar"); //broken bar = broken vertical bar
            put(167, "sect"); //section sign
            put(168, "uml"); //diaeresis = spacing diaeresis
            put(169, "copy"); //  copyright sign
            put(170, "ordf"); //feminine ordinal indicator
            put(171, "laquo"); //left-pointing double angle quotation mark = left pointing guillemet
            put(172, "not"); //not sign
            put(173, "shy"); //soft hyphen = discretionary hyphen
            put(174, "reg"); // registered trademark sign
            put(175, "macr"); //macron = spacing macron = overline = APL overbar
            put(176, "deg"); //degree sign
            put(177, "plusmn"); //plus-minus sign = plus-or-minus sign
            put(178, "sup2"); //superscript two = superscript digit two = squared
            put(179, "sup3"); //superscript three = superscript digit three = cubed
            put(180, "acute"); //acute accent = spacing acute
            put(181, "micro"); //micro sign
            put(182, "para"); //pilcrow sign = paragraph sign
            put(183, "middot"); //middle dot = Georgian comma = Greek middle dot
            put(184, "cedil"); //cedilla = spacing cedilla
            put(185, "sup1"); //superscript one = superscript digit one
            put(186, "ordm"); //masculine ordinal indicator
            put(187, "raquo"); //right-pointing double angle quotation mark = right pointing guillemet
            put(188, "frac14"); //vulgar fraction one quarter = fraction one quarter
            put(189, "frac12"); //vulgar fraction one half = fraction one half
            put(190, "frac34"); //vulgar fraction three quarters = fraction three quarters
            put(191, "iquest"); //inverted question mark = turned question mark
            put(192, "Agrave"); // uppercase A, grave accent
            put(193, "Aacute"); //  uppercase A, acute accent
            put(194, "Acirc"); //  uppercase A, circumflex accent
            put(195, "Atilde"); //  uppercase A, tilde
            put(196, "Auml"); //  uppercase A, umlaut
            put(197, "Aring"); //  uppercase A, ring
            put(198, "AElig"); //  uppercase AE
            put(199, "Ccedil"); //  uppercase C, cedilla
            put(200, "Egrave"); //  uppercase E, grave accent
            put(201, "Eacute"); //  uppercase E, acute accent
            put(202, "Ecirc"); //  uppercase E, circumflex accent
            put(203, "Euml"); //  uppercase E, umlaut
            put(204, "Igrave"); //  uppercase I, grave accent
            put(205, "Iacute"); //  uppercase I, acute accent
            put(206, "Icirc"); //  uppercase I, circumflex accent
            put(207, "Iuml"); //  uppercase I, umlaut
            put(208, "ETH"); //  uppercase Eth, Icelandic
            put(209, "Ntilde"); //  uppercase N, tilde
            put(210, "Ograve"); //  uppercase O, grave accent
            put(211, "Oacute"); //  uppercase O, acute accent
            put(212, "Ocirc"); //  uppercase O, circumflex accent
            put(213, "Otilde"); //  uppercase O, tilde
            put(214, "Ouml"); //  uppercase O, umlaut
            put(215, "times"); //multiplication sign
            put(216, "Oslash"); //  uppercase O, slash
            put(217, "Ugrave"); //  uppercase U, grave accent
            put(218, "Uacute"); //  uppercase U, acute accent
            put(219, "Ucirc"); //  uppercase U, circumflex accent
            put(220, "Uuml"); //  uppercase U, umlaut
            put(221, "Yacute"); //  uppercase Y, acute accent
            put(222, "THORN"); //  uppercase THORN, Icelandic
            put(223, "szlig"); //  lowercase sharps, German
            put(224, "agrave"); //  lowercase a, grave accent
            put(225, "aacute"); //  lowercase a, acute accent
            put(226, "acirc"); //  lowercase a, circumflex accent
            put(227, "atilde"); //  lowercase a, tilde
            put(228, "auml"); //  lowercase a, umlaut
            put(229, "aring"); //  lowercase a, ring
            put(230, "aelig"); //  lowercase ae
            put(231, "ccedil"); //  lowercase c, cedilla
            put(232, "egrave"); //  lowercase e, grave accent
            put(233, "eacute"); //  lowercase e, acute accent
            put(234, "ecirc"); //  lowercase e, circumflex accent
            put(235, "euml"); //  lowercase e, umlaut
            put(236, "igrave"); //  lowercase i, grave accent
            put(237, "iacute"); //  lowercase i, acute accent
            put(238, "icirc"); //  lowercase i, circumflex accent
            put(239, "iuml"); //  lowercase i, umlaut
            put(240, "eth"); //  lowercase eth, Icelandic
            put(241, "ntilde"); //  lowercase n, tilde
            put(242, "ograve"); //  lowercase o, grave accent
            put(243, "oacute"); //  lowercase o, acute accent
            put(244, "ocirc"); //  lowercase o, circumflex accent
            put(245, "otilde"); //  lowercase o, tilde
            put(246, "ouml"); //  lowercase o, umlaut
            put(247, "divide"); // division sign
            put(248, "oslash"); //  lowercase o, slash
            put(249, "ugrave"); //  lowercase u, grave accent
            put(250, "uacute"); //  lowercase u, acute accent
            put(251, "ucirc"); //  lowercase u, circumflex accent
            put(252, "uuml"); //  lowercase u, umlaut
            put(253, "yacute"); //  lowercase y, acute accent
            put(254, "thorn"); //  lowercase thorn, Icelandic
            put(255, "yuml"); //  lowercase y, umlaut
        }
    };
    public static final Pattern titlePattern = Pattern.compile("^(.+)\\((\\p{N}+)\\)$");

    public StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0);
    }

    /**
     * like isEmpty(), but it also checks for the word "null" - this is useful
     * in case there was a stringified null.
     */
    public static boolean isEmpty(String s, boolean checkForNullWord) {
        return isEmpty(s) || "null".equals(s);
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

    public static int length(String s) {
        return s == null ? 0 : s.length();
    }

    public static boolean equals(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        }
        return s1.equals(s2);
    }

    public static String substring(String s, int beginIndex) {
        if (isEmpty(s)) {
            return s;
        }
        if (beginIndex < 0 || beginIndex >= s.length()) {
            return "";
        }
        return s.substring(beginIndex);
    }

    public static int compare(String s1, String s2) {
        if (s1 == null) {
            return (s2 == null ? 0 : -1);
        }
        return (s2 == null ? 1 : s1.compareTo(s2));
    }

    public static int compare(String s1, String s2, boolean ignoreCase) {
        if (ignoreCase) {
            if (s1 == null) {
                return (s2 == null ? 0 : -1);
            }
            return (s2 == null ? 1 : compare(s1.toLowerCase(), s2.toLowerCase()));
        }
        return compare(s1, s2);
    }

    public static String substring(String s, int beginIndex, int endIndex) {
        if (isEmpty(s)) {
            return s;
        }
        if (endIndex > s.length()) {
            endIndex = s.length();
        }
        if (beginIndex < 0 || beginIndex > endIndex) {
            return "";
        }
        return s.substring(beginIndex, endIndex);
    }

    /**
     * Get the index for the first occurance of a search string
     * within the source string starting at begin.
     *
     * @param strToSearch is the string to search within
     * @param strToFind   is the string to find
     * @param ignoreCase  is true to ignore the case of the strToFind
     * @param fromIndex   is the index to start searching from
     * @return the start index of the first occurance or -1 if it wasn't found
     */
    public static int indexOf(String strToSearch, String strToFind, boolean ignoreCase, int fromIndex) {
        if (isEmpty(strToSearch) || isEmpty(strToFind)) {
            return -1;
        }
        if (ignoreCase) {
            strToSearch = strToSearch.toUpperCase();
            strToFind = strToFind.toUpperCase();
        }
        return strToSearch.indexOf(strToFind, fromIndex);
    }

    /**
     * Get the index for the last occurance of a search string
     * within the source string starting at the fromIndex.
     *
     * @param strToSearch is the string to search within
     * @param strToFind   is the string to find
     * @param ignoreCase  is true to ignore the case of the strToFind
     * @param fromIndex   is the index to start searching backwards
     * @return the start index of the last occurance or -1 if it wasn't found
     */
    public static int lastIndexOf(String strToSearch, String strToFind, boolean ignoreCase, int fromIndex) {
        if (isEmpty(strToSearch) || isEmpty(strToFind)) {
            return -1;
        }
        if (ignoreCase) {
            strToSearch = strToSearch.toUpperCase();
            strToFind = strToFind.toUpperCase();
        }
        return strToSearch.lastIndexOf(strToFind, fromIndex);
    }

    public static Number parseNumber(String s) {
        if (s == null)
            return null;
        try {
            Object v = new NumberFormatter(NumberFormat.getNumberInstance()).stringToValue(s);
            if (v instanceof Number) {
                return (Number) v;
            }
        } catch (ParseException pe) {
            return null;
        }
        return null;
    }

    public static List<String> getArgs(String args) {
        boolean inQuote = false;
        boolean inToken = false;
        boolean escaped = false;
        int beginToken = 0;
        List<String> tokens = new ArrayList<String>();

        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);
            if (!escaped && c == '\"') {
                inQuote = !inQuote;
            }
            if (c == '\\')
                escaped = true;
            if (!inToken) {
                if (Character.isWhitespace(c)) {
                    continue;
                } else {
                    inToken = true;
                    beginToken = i;
                }
            } else {
                if (!Character.isWhitespace(c) || inQuote) {
                    continue;
                } else if (Character.isWhitespace(c) && !inQuote) {
                    tokens.add(args.substring(beginToken, i));
                    inToken = false;
                }
            }
            if (c != '\\')
                escaped = false;
        }
        if (inToken) {
            tokens.add(args.substring(beginToken));
        }
        return tokens;
    }

    public static Number parseNumber(Object s) {
        return parseNumber(String.valueOf(s));
    }

    public static String replace(String src, int start, int length, String replaceString) {
        if (isEmpty(src)) {
            return src;
        }
        return src.substring(0, start) + replaceString + src.substring(start + length, src.length());
    }

    public static boolean startsWith(String src, String s, boolean ignoreCase) {
        if (isEmpty(src)) {
            return isEmpty(s);
        }
        return src.regionMatches(ignoreCase, 0, s, 0, s.length());
    }

    public static boolean endsWith(String src, String s, boolean ignoreCase) {
        if (isEmpty(src)) {
            return isEmpty(s);
        }
        return src.regionMatches(ignoreCase, src.length() - s.length(), s, 0, s.length());
    }

    public static boolean equals(String src, String s, boolean ignoreCase) {
        if (src == null) {
            return s == null;
        }
        if (ignoreCase) {
            return src.equalsIgnoreCase(s);
        } else {
            return src.equals(s);
        }
    }

    public static String stripUpTo(String s, String ss, boolean including) {
        if (isEmpty(s) || isEmpty(ss))
            return s;
        int idx = s.indexOf(ss);
        if (idx < 0)
            return s;
        if (including)
            idx += ss.length();
        return s.substring(idx);
    }

    public static String stripAfter(String s, String ss, boolean including) {
        if (isEmpty(s) || isEmpty(ss))
            return s;
        int idx = s.indexOf(ss);
        if (idx < 0)
            return s;
        if (!including)
            idx += ss.length();
        return s.substring(0, idx);
    }

    /**
     * Truncates a string to a specified length.
     *
     * @param s      string to truncate
     * @param length length to truncate to.
     * @return the truncated string. Half surrogates are removed from both ends (see <code>StringUtils.trimHalfSurrogates()</code>).
     */
    public static String truncate(String s, int length) {
        return truncate(s, length, true);
    }

    /**
     * Truncates a string to a specified length.
     *
     * @param s                  string to truncate
     * @param length             length to truncate to.
     * @param trimHalfSurrogates if true, half surrogates are removed from both the ends of the string.
     * @return the truncated string.
     */
    public static String truncate(String s, int length, boolean trimHalfSurrogates) {
        if (s == null || s.length() <= length) {
            return s;
        }
        String chopped = s.substring(0, length);
        return (trimHalfSurrogates ? trimHalfSurrogates(chopped) : chopped);
    }

    public static String getFirstPathComponent(String path) {
        while (path.startsWith("/"))
            path = path.substring(1);
        String[] elements = path.split("/");
        return elements[0];
    }

    public static String truncateForUI(String str, int length) {
        return truncateForUI(str, length, true);
    }

    /**
     * Truncates a string by putting elipsis (three dots) characters at the beginning or at the end
     * of the truncated part.
     *
     * @param str          the input string. If the string does not need truncation, it is returned as is (no new string is alloc-ed).
     * @param length       the length to truncate to. If length is less than 3 then no elipsis are placed.
     * @param truncateTail if true it chops off from the tail, otherwise it chops off from the head of the string
     * @return the same string if no truncation needed or a new truncated string. The length of the truncated string,
     * including elipses cannot exceed the value of the {@code length} parameter.
     */
    public static String truncateForUI(String str, int length, boolean truncateTail) {
        return truncateForUI(str, length, truncateTail, false);
    }

    /**
     * Truncates a string by putting elipsis (three dots) characters at the beginning or at the end
     * of the truncated part.
     *
     * @param str                the input string. If the string does not need truncation, it is returned as is (no new string is alloc-ed).
     * @param length             the length to truncate to. If length is less than 3 then no elipsis are placed.
     * @param truncateTail       if true it chops off from the tail, otherwise it chops off from the head of the string
     * @param trimHalfSurrogates if true it calls trimHalfSurrogates on the result to make sure we get back a valid string.
     * @return the same string if no truncation needed or a new truncated string. The length of the truncated string,
     * including elipses cannot exceed the value of the {@code length} parameter.
     */
    public static String truncateForUI(String str, int length, boolean truncateTail, boolean trimHalfSurrogates) {
        if (str == null || length <= 0) {
            return "";
        }
        int len = str.length();
        if (str.length() <= length) {
            return (trimHalfSurrogates ? trimHalfSurrogates(str) : str);
        }
        String result = str;
        // no elipsis needed ?
        if (length <= 3) {
            result = (truncateTail ? str.substring(0, length) : str.substring(len - length));
        } else if (truncateTail) {
            // elipsis are needed, make sure the resulting string is not longer than required length!
            result = new StringBuilder(str.substring(0, length - 3)).append("...").toString();
        } else {
            result = new StringBuilder("...").append(str.substring(len - length + 3)).toString();
        }
        return (trimHalfSurrogates ? trimHalfSurrogates(result) : result);
    }

    /**
     * Removes halves of surrogate pairs from the ends of a string:
     * <ul>
     * <li>If the string begins with a low surrogate, it returns the substring starting at position 1
     * <li>If the string ends with a high surrogate, it returns the substring that ends at position <code>str.length() - 2</code>
     * </ul>
     *
     * @param str the string to trim
     * @return trimmed string if necessary.
     */
    public static String trimHalfSurrogates(String str) {
        return trimHalfSurrogates(str, true, true);
    }

    /**
     * Removes halves of surrogate pairs from the ends of a string:
     * <ul>
     * <li>If <code>atHead==true</code> and the string begins with a low surrogate, it returns the substring starting at position 1
     * <li>If <code>atTail==true</code> and the string ends with a high surrogate, it returns the substring that ends at position <code>str.length() - 2</code>
     * </ul>
     *
     * @param str the string to trim
     * @return trimmed string if necessary.
     */
    public static String trimHalfSurrogates(String str, boolean atHead, boolean atTail) {
        if (str == null || str.isEmpty() || (atHead == false && atTail == false)) {
            return str;
        }
        int len;
        len = str.length();
        String result = str;

        // the string cannot begin with a low surrogate (second/trailing half of the pair)
        if (atHead) {
            char ch = str.charAt(0);
            if (Character.isLowSurrogate(ch)) {
                if (len > 1) {
                    result = str.substring(1);
                } else {
                    result = "";
                }
            }
        }

        if (atTail) {
            // the string cannot end with a high surrogate (first/leading half of the pair)
            char ch = str.charAt(len - 1);
            if (Character.isHighSurrogate(ch)) {
                if (len > 1) {
                    result = str.substring(0, len - 1);
                } else {
                    result = "";
                }
            }
        }
        return result;
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
     * same as isSpace(char c) except it handles surrogates.
     *
     * @param codePoint code point
     * @return true if this is a space in both Unicode and Java sense
     */
    public static boolean isSpace(int codePoint) {
        return Character.isSpaceChar(codePoint) || Character.isWhitespace(codePoint);
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
     * trims all the trailing codepoints of a certain type from the end
     * of a string.
     *
     * @param s                 the input string, can be null
     * @param trailingCodePoint the codePoint to be trimmed
     * @return the trimmed string if it has such trailing characters,
     * or the original string otherwise.
     */
    public static String trimTrailing(String s, int trailingCodePoint) {
        if (isEmpty(s)) {
            return s;
        }
        int len = s.codePointCount(0, s.length());
        int pos = len;
        while (pos > 1 && (s.codePointAt(s.offsetByCodePoints(pos, -1)) == trailingCodePoint)) {
            pos = s.offsetByCodePoints(pos, -1);
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
     * @return o.toString(), or empty string if o is null
     */
    public static String valueOf(Object o) {
        return (o == null ? "" : o.toString());
    }

    /**
     * turns a collection into a string.
     *
     * @param c the collection, if null empty string is returned.
     * @return stringified collection.
     */
    public static String collectionToString(Collection<?> c) {
        return collectionToString(c, " ");
    }

    /**
     * turns a collection into a string.
     *
     * @param c         collection, if null, empty string is returned
     * @param separator separator between elements, if null space is used.
     * @return stringified collection
     */
    public static String collectionToString(Collection<?> c, String separator) {
        if (c == null || c.size() == 0)
            return "";

        String sep = (separator == null ? " " : separator);
        return collectionToString(c, sep, new StringBuilder()).toString();
    }

    /**
     * turns a collection into a string.
     *
     * @param c          collection, if null, empty string is returned
     * @param separator  separator between elements.
     * @param escapeChar the escape character to be used in case the separator is found
     *                   within the string. If null, no escaping is done.
     * @return stringified collection
     */
    public static String collectionToString(Collection<?> c, char separator, char escapeChar) {
        if (c == null || c.size() == 0)
            return "";

        return collectionToString(c, new StringBuilder(), separator, escapeChar).toString();
    }

    /**
     * turns a collection into a string.
     *
     * @param c          collection, if null, empty string is returned
     * @param appendTo   an appendable object to which the strings are appended.
     * @param separator  separator between elements
     * @param escapeChar the escape character to be used in case the separator is found
     * @return appendTo
     */
    public static Appendable collectionToString(Collection<?> c, Appendable appendTo, char separator, char escapeChar) {

        if (c == null || c.size() == 0 || appendTo == null)
            return appendTo;

        try {
            // replace escape char with escaped escape
            String esc = String.valueOf(escapeChar);
            String escEsc = Matcher.quoteReplacement(esc + escapeChar);
            Pattern escPat = Pattern.compile(esc, Pattern.LITERAL);

            // replace separator with escaped separator
            String sep = String.valueOf(separator);
            String escSep = esc + sep;
            Pattern sepPat = Pattern.compile(sep, Pattern.LITERAL);

            for (Iterator<?> it = c.iterator(); it.hasNext(); ) {
                String val = String.valueOf(it.next());
                val = escPat.matcher(val).replaceAll(escEsc);
                val = sepPat.matcher(val).replaceAll(escSep);
                //val = val.replace(esc, escEsc).replace(sep, escSep);
                appendTo.append(val);
                if (it.hasNext()) {
                    appendTo.append(separator);
                }
            }
        } catch (IOException e) {
            throw new DharmaException(Dharma.msg("Dharma.util.StringUtils.collectionToStringError") + e); //$NON-NLS-1$
        }
        return appendTo;
    }

    /**
     * replaces all instances of the escape character with its escaped version,
     * and all instances of the target character with the escaped version. Already
     * escaped characters are escaped again, so be careful what you pass in.
     *
     * @param input      the input string
     * @param target     the target character
     * @param escapeChar the escape character
     * @return the escaped string.
     */
    public static String escape(String input, char target, char escapeChar) {
        if (isEmpty(input)) {
            return input;
        }

        // replace escape char with escaped escape
        String esc = String.valueOf(escapeChar);
        String escEsc = esc + escapeChar;

        // replace separator with escaped target
        String tgt = String.valueOf(target);
        String escTgt = esc + tgt;

        return input.replace(esc, escEsc).replace(tgt, escTgt);
    }

    /**
     * turns a collection into a string.
     *
     * @param c         collection, if null, empty string is returned
     * @param separator separator between elements, if null space is used.
     * @param appendTo  an appendable object to which the strings are appended.
     * @return appendTo
     */
    public static Appendable collectionToString(Collection<?> c, String separator, Appendable appendTo) {
        if (c == null || c.size() == 0 || appendTo == null)
            return appendTo;

        String sep = (separator == null ? " " : separator);
        try {
            for (Iterator<?> it = c.iterator(); it.hasNext(); ) {
                appendTo.append(String.valueOf(it.next()));
                if (it.hasNext()) {
                    appendTo.append(sep);
                }
            }
        } catch (IOException e) {
            throw new DharmaException(Dharma.msg("Dharma.util.StringUtils.collectionToStringError") + e); //$NON-NLS-1$
        }
        return appendTo;
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

    /**
     * turns a bean into a one line string, useful for logging.
     *
     * @param bean the bean, if null it returns empty string.
     * @return something like <code>{ property1=value1, property2=value2, ... }</code>
     */
//    public static String beanToString(Object bean) {
//        return beanToString(bean, ", ");
//    }

//    /**
//     * turns a bean into a string, useful for logging. The bean cannot have
//     * cycles, otherwise it will cause a stack overflow.
//     *
//     * @param bean      the bean, if null it returns empty string.
//     * @param separator the separator, if null, the string ", " is used.
//     * @return stringified bean.
//     */
//    @SuppressWarnings("unchecked")
//    public static String beanToString(Object bean, String separator) {
//        if (bean == null)
//            return "";
//
//        String sep = (separator == null ? ", " : separator);
//        //String className = bean.getClass().getName();
//        PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(bean);
//        StringBuilder buf = new StringBuilder();
//        buf.append(bean.getClass().getName()).append('@').append(System.identityHashCode(bean)).append("{ ");
//        for (int i = 0; i < properties.length; i++) {
//            String propertyName = properties[i].getName();
//            if ("class".equals(propertyName)) {
//                continue;
//            }
//            if (properties[i].getReadMethod() == null) {
//                continue;
//            }
//            try {
//                buf.append(propertyName).append("=");
//                Object value = PropertyUtils.getProperty(bean, propertyName);
//                if (value == null) {
//                    buf.append("null");
//                } else if (value.getClass().isArray()) {
//                    buf.append("[").append(arrayToString(value, ", ")).append("]");
//                } else if (value instanceof Collection) {
//                    buf.append("(").append(collectionToString((Collection) value, ", ")).append(")");
//                } else {
//                    buf.append(value);
//                }
//            } catch (Throwable ex) {
//                // shut up, we're just a string method.
//            }
//            if (i < properties.length - 1)
//                buf.append(sep);
//        }
//        buf.append(" }");
//        return buf.toString();
//    }

    /**
     * puts the public fields of an object into a string, using comma as separator.
     *
     * @param obj the object, can be null
     * @return a string like this: <code>f1=f1val, f2=f2val, ...</code>
     */
    public static final String fieldsToString(Object obj) {
        return fieldsToString(obj, ", ");
    }

    /**
     * puts the public fields of an object into a string.
     *
     * @param obj       the object, can be null
     * @param separator the separator to use
     * @return a string like this (separator is comma): <code>f1=f1val, f2=f2val, ...</code>
     */
    public static final String fieldsToString(Object obj, String separator) {
        StringBuilder buf = new StringBuilder();
        appendFields(obj, separator, buf);
        return buf.toString();
    }

    /**
     * appends stringified public field values to a buffer.
     *
     * @param obj       the object, can be null
     * @param separator what separator to use
     * @param buf       the buffer to append to
     */
    public static final void appendFields(Object obj, String separator, Appendable buf) {
        if (obj == null) {
            return;
        }
        Field[] fields = obj.getClass().getFields();
        for (Iterator<Field> it = ArrayUtils.iterator(fields); it.hasNext(); ) {
            Field field = it.next();
            try {
                buf.append(field.getName()).append('=').append(String.valueOf(field.get(obj)));
            } catch (Exception e) {
                append(buf, e.getMessage());
            }
            if (it.hasNext()) {
                append(buf, separator);
            }
        }
    }

    /**
     * appends a string to an appendable, handling IOExceptions (which should not
     * likely happen since we mainly use StringBuilders here).
     *
     * @param buf the appendanble
     * @param s   the string to append
     */
    public static void append(Appendable buf, String s) {
        try {
            buf.append(s);
        } catch (IOException e) {
            try {
                buf.append(e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * turns a map into a string
     *
     * @param map       the map to stringify, if null, empty string is returned
     * @param separator separator between key=value pairs
     * @param sortKeys  if true, map is printed in ascending order of keys.
     * @return a string like this <code>{key=value[sep]key=value[sep] ...}</code>
     */
    public static <K, V> String mapToString(Map<K, V> map, String separator, boolean sortKeys) {
        if (!sortKeys) {
            return mapToString(map, separator);
        }

        if (map == null)
            return "";

        String sep = (separator == null ? ", " : separator);
        StringBuilder buf = new StringBuilder();
        Set<K> sortedKeys = new TreeSet<K>(map.keySet());
        buf.append('{');
        for (Iterator<K> it = sortedKeys.iterator(); it.hasNext(); ) {
            K key = it.next();
            buf.append(key).append('=').append(map.get(key));
            if (it.hasNext()) {
                buf.append(sep);
            }
        }
        buf.append('}');
        return buf.toString();
    }

    /**
     * turns a map into a one-line string, useful for logging, with spaces
     * between name-value pairs.
     *
     * @param map the map to stringify, if null, empty string is returned
     * @return a string like this <code>{key=value[sep]key=value[sep] ...}</code>
     */
    public static <K, V> String mapToString(Map<K, V> map) {
        return mapToString(map, " ");
    }

    /**
     * turns a map into a string
     *
     * @param map       the map to stringify, if null, empty string is returned
     * @param separator separator between key=value pairs
     * @return a string like this <code>{key=value[sep]key=value[sep] ...}</code>
     */
    public static <K, V> String mapToString(Map<K, V> map, String separator) {
        if (map == null)
            return "";

        String sep = (separator == null ? ", " : separator);
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        for (Iterator<Map.Entry<K, V>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<K, V> entry = it.next();
            buf.append(entry.getKey()).append('=').append(entry.getValue());
            if (it.hasNext()) {
                buf.append(sep);
            }
        }
        buf.append('}');
        return buf.toString();
    }

    /**
     * Builds an array of localized strings using the keys supplied and a resource bundle.
     *
     * @param keys      keys to look up, their toString() value is used as a key.
     * @param resources resource bundle
     * @return localized strings.
     */
    public static String[] buildLocalizedStrings(Object[] keys, ResourceBundle resources) {
        if (keys == null || keys.length == 0 || resources == null) {
            return null;
        }
        String[] result = new String[keys.length];
        for (int i = 0; i < result.length; i++) {
            if (keys[i] != null) {
                result[i] = resources.getString(keys[i].toString());
            } else {
                result[i] = null;
            }
        }
        return result;
    }

    /**
     * formats a date and does not barf if the date is null
     */
    public static String format(Date date) {
        return format(date, Dharma.getDateTimeFormat());
    }

    /**
     * formats a date and does not barf if the date is null
     */
    public static String format(Date date, DateFormat fmt) {
        if (date == null) {
            return "";
        }
        if (fmt == null) {
            throw new IllegalArgumentException("fmt == null");
        }
        return fmt.format(date);
    }

    public static String formatHtmlTextBlock(String text, final int maxNumLines, int maxLineLength) {
        if (isEmpty(text)) {
            return text;
        }
        StringBuilder buf = new StringBuilder();
        text = text.replaceAll("\n", "<br>");
        text = WordUtils.wrap(text, maxLineLength, "<br>", false);
        String s[] = text.split("<br>");
        int i = 0;
        for (i = 0; i < maxNumLines - 1 && i < s.length; i++) {
            buf.append(s[i]).append("<br>");
        }
        if (s.length > maxNumLines)
            buf.append(s[i]).append("...<b><i>more</i></b>").append("<br>");
        return buf.toString();
    }

    public static String getExceptionTrace(Exception ex) {
        if (ex == null) {
            return "";
        }
        StringWriter w = new StringWriter(1024);
        ex.printStackTrace(new PrintWriter(w));
        return w.toString();
    }

    /**
     * gets the first nChars of a stringified exception trace.
     *
     * @param ex     exception
     * @param nChars how many chars
     * @return first nChars of the trace, empty string if ex is null,
     * or less than nChars if the trace does not have nChars chars.
     */
    public static String getExceptionTrace(Exception ex, int nChars) {
        if (ex == null) {
            return "";
        }
        StringWriter w = new StringWriter(nChars);
        ex.printStackTrace(new PrintWriter(w));
        StringBuffer buf = w.getBuffer();
        if (buf.length() < nChars) {
            return buf.toString();
        }
        return buf.substring(0, nChars);
    }

    public static String breakString(String original, int maxSegmentLength, String breakSymbol) {
        if (isEmpty(original) || isEmpty(breakSymbol) || maxSegmentLength <= 0 || original.length() <= maxSegmentLength) {
            return original;
        }

        int len = original.length();
        StringBuilder result = new StringBuilder(len + (len / maxSegmentLength + 1) + breakSymbol.length());

        for (int i = 1; i <= len; i++) {
            result.append(original.charAt(i - 1));
            if (i % maxSegmentLength == 0) {
                result.append(breakSymbol);
            }
        }
        return result.toString();
    }

    public static String getLastPathComponent(String src, char pathSep) {
        if (src == null) {
            return null;
        }
        int idx = src.lastIndexOf(pathSep);
        if (idx < 0) {
            return src;
        } else if (idx >= src.length()) {
            return "";
        } else {
            return src.substring(idx + 1);
        }
    }

    public static String getParentPathComponent(String src, char pathSep) {
        int idx = src.lastIndexOf(pathSep);
        if (idx < 0) {
            return "";
        } else if (idx >= src.length()) {
            return "";
        } else {
            return src.substring(0, idx);
        }
    }

    public static boolean pathStartsWith(String path1, String path2) {
        return path1.equals(path2) || (path1.startsWith(path2) && (path1.charAt(path2.length()) == '/' || path2.equals("/"))); //$NON-NLS-1$
    }

    public static String escapeHTML(String s) {
        if (isEmpty(s)) {
            return s;
        }
        StringBuilder buf = new StringBuilder();
        escapeHTML(s, buf);
        return buf.toString();
    }

    public static void escapeHTML(final String str, StringBuilder buf) {
        appendEscapedHTML(str, buf);
    }

    public static void escapeHTML(final String str, StringBuffer buf) {
        appendEscapedHTML(str, buf);
    }

    public static void appendEscapedHTML(final String str, Appendable sb) {
        if (isEmpty(str)) {
            return;
        }
        try {
            int len = str.length();
            int c = 0;
            for (int i = 0; i < len; i++) {
                c = str.codePointAt(i);
                String namedEntity = (c > 0xFF ? null : NAMED_HTML_ENTITIES.get(c));
                if (namedEntity != null) {
                    sb.append('&').append(namedEntity).append(';');
                } else {
                    if (c < 0x20) {
                        // non-printable chars except for whitespace.
                        switch (c) {
                            case 10: // '\n'
                            case 13: // '\r'
                            case 9: // '\t'
                                sb.append((char) c);
                                break;
                            default:
                                sb.append("?");
                                break;
                        }
                    } else if (c > 0x7F) {
                        // above 127 we just dump the hex of the codepoint.
                        // most of these chars though will have been found
                        // inside NAMED_HTML_ENTITIES.
                        sb.append("&#x");
                        sb.append(Integer.toHexString(c));
                        sb.append(';');
                        if (Character.isSupplementaryCodePoint(c)) {
                            i++;
                        }
                    } else {
                        // the character is somewhere in between 0x20 and 0x7f
                        // so it can never be 4 bytes in length, thus we can
                        // cast it straight to avoid creation of useless arrays:
                        sb.append((char) c);
                    }
                }
            }
        } catch (IOException e) {
            throw new DharmaException(Dharma.msg("Dharma.util.StringUtils.appendEscapedHTMLError", e.getMessage()), e); //$NON-NLS-1$
        }

    }

    public static String escapeURI(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        int n = s.length();
        StringBuilder sb = new StringBuilder(n > 16 ? n + 16 : 16);
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case ' ':
                    sb.append("%20");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();

    }

    public static Object expand(String str, Pair<String, String> expansionStrings[]) {
        for (int i = 0; i < expansionStrings.length; ++i) {
            Pair<String, String> pair = expansionStrings[i];
            str = str.replaceAll("@" + pair.getFirst() + "@", pair.getSecond());
        }
        return str;
    }

    /**
     * Replace characters having special meaning in regular expressions
     * with their escaped equivalents.
     * <p/>
     * <P>The escaped characters include :
     * <ul>
     * <li>.
     * <li>\
     * <li>?, * , and +
     * <li>&
     * <li>:
     * <li>{ and }
     * <li>[ and ]
     * <li>( and )
     * <li>^ and $
     * </ul>
     */
    public static String escapeRegex(final String expression) {
        StringBuilder result = new StringBuilder();
        StringCharacterIterator it = new StringCharacterIterator(expression);
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            switch (c) {
                case '.':
                case '\\':
                case '?':
                case '*':
                case '+':
                case '&':
                case ':':
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                case '^':
                case '$':
                    result.append("\\").append(c);
                    break;
                default:
                    result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * tells whether a string contains an unescaped character within itself.
     *
     * @param input      the input string
     * @param target     the character we're looking for
     * @param escapeChar the escape character to use
     * @return true if found an unescaped separator
     */
    public static boolean containsUnescapedChar(String input, char target, char escapeChar) {
        if (isEmpty(input)) {
            return false;
        }
        char prev = 0;
        boolean escapedEscape = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == target && (prev != escapeChar || escapedEscape)) {
                return true;
            }
            escapedEscape = (!escapedEscape && c == escapeChar && prev == escapeChar);
            prev = c;
        }
        return false;
    }

    /**
     * splits the input string according to the unescaped separator.
     *
     * @param input      the input string
     * @param separator  the separator character to use
     * @param escapeChar the escape character. Escaped separators are not used in splitting. Escape char cannot be the
     *                   same char as separator, an IllegalArgumentException is thrown in that case.
     * @return the list of tokens, or empty list if input is empty or does not split. Escapes are removed.
     */
    public static List<String> split(String input, char separator, char escapeChar) {
        return split(input, separator, escapeChar, true);
    }

    /**
     * splits the input string according to the unescaped separator.
     *
     * @param input                 the input string
     * @param separator             the separator character to use
     * @param escapeChar            the escape character. Escaped separators are not used in splitting. Escape char cannot be the
     *                              same char as separator, an IllegalArgumentException is thrown in that case.
     * @param unescapeNonSeparators if true, escapes are removed everywhere; if false, they only get removed for the separators.
     * @return the list of tokens, or empty list if input is empty or does not split.
     */
    public static List<String> split(String input, char separator, char escapeChar, boolean unescapeNonSeparators) {
        if (isEmpty(input)) {
            return Collections.emptyList();
        }
        // this is a limitation, but you're welcome to make it work.
        if (separator == escapeChar) {
            throw new IllegalArgumentException(Dharma.msg("Dharma.util.StringUtils.SeparatorAsEscapeCharError")); //$NON-NLS-1$
        }

        List<String> result = new ArrayList<String>();
        char prev = 0;
        boolean escapedEscape = false;
        StringBuilder buf = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++) {

            char c = input.charAt(i);

            // handle separators
            if (c == separator) {
                // this is an escaped separator, we unescape it.
                if (prev == escapeChar && !escapedEscape) {
                    buf.append(c);
                }
                // this is a real separator, finish current chunk and open new chunk
                else {
                    result.add(buf.toString());
                    buf = new StringBuilder();
                }
            }

            // handle regular character, possibly escapeChar
            else {
                if (c != escapeChar) {
                    if (prev == escapeChar && !escapedEscape && unescapeNonSeparators == false) {
                        buf.append(prev);
                    }
                    buf.append(c);
                }
                // it's an escape char and it's the last one. Must get out.
                else if (i == len - 1) {
                    buf.append(c);
                    break;
                }
            }

            // set up the escaped escape state.
            escapedEscape = (!escapedEscape && c == escapeChar && prev == escapeChar);
            if (escapedEscape) {
                if (unescapeNonSeparators == false) {
                    buf.append(prev);
                }
                buf.append(c);
            }

            prev = c;
        }

        result.add(buf.toString());

        return result;

    }

    /**
     * Splits a string separated by <code>separator</code> character(s), taking into account chunks
     * quoted by the <code>quote</code> character. Escaping within the quotes is supported, using the
     * <code>escape</code> character for escaping.
     * <p/>
     * Note: this method will not work when surrogate chars are used for separators.
     * <p/>
     * <p>The algorithm works in two modes:
     * <ol>
     * <li>until a quote is hit, it hunts for separators and splits according to separators. Escapes here count
     * as normal characters, being added to the chunks, unless they escape a quote. Escapes can be escaped.
     * When a (unescaped) quote is hit, it toggles to the next mode.
     * <li>in quoted mode, it hunts for the next quote that is not escaped. Escapes can be escaped here too.
     * </ol>
     *
     * @param input       the input string
     * @param quote       the quote char.
     * @param separators  string containing possible separators for splitting.
     * @param escape      the escape char.
     * @param stripQuotes if true, quotes get stripped. This is useful in parsing cmd line args and environment vars,
     *                    which is system dependend (on DOS, if you set FOO="BAR", you get "BAR" for $FOO, whereas on unix you get BAR
     *                    with no quotes). So for command lines you'd want quotes stripped on any OS, whereas for environment vars
     *                    you'd want quotes stripped on DOS and unstripped on Unix. Ugh...
     * @return a list of strings, possibly empty but never null.
     * @throws IllegalArgumentException if <code>separators.contains(quote) OR quote == escape OR separators.contains(escape)</code>.
     */
    public static List<String> splitQuoted(String input, char quote, CharSequence separators, char escape,
                                           boolean stripQuotes) {

        if (separators == null) {
            throw new IllegalArgumentException("null separator sequence");
        }
        if (quote == escape) {
            throw new IllegalArgumentException(Dharma.msg("Dharma.util.StringUtils.QuoteEscapeCharEqualError", quote));
        }
        for (int i = 0; i < separators.length(); i++) {
            Character sep = separators.charAt(i);
            if (sep == quote) {
                throw new IllegalArgumentException(Dharma.msg("Dharma.util.StringUtils.QuoteChasAsSeparatorError", quote));
            }
            if (sep == escape) {
                throw new IllegalArgumentException(Dharma.msg("Dharma.util.StringUtils.EscapeCharAsSeparatorError", escape));
            }
            if (Character.isHighSurrogate(sep) || Character.isLowSurrogate(sep)) {
                throw new IllegalArgumentException("surrogate chars cannot be used as separators");
            }
        }

        List<String> result = new ArrayList<String>();

        if (StringUtils.isEmpty(input)) {
            return result;
        }

        StringBuilder buf = new StringBuilder();

        // the whole shite can be explained best if a state diagram is drawn and transitions made between states.
        // I came up with these 6 possible states, maybe they can be minimized, but at the expense of clarity.
        // Also, the code for each state is somewhat redundant, but it's like that for clarity as well.
        final byte START = 0;
        final byte APPEND = 1;
        final byte ESCAPE = 2;
        final byte QUOTE = 3;
        final byte QAPPEND = 4;
        final byte QESCAPE = 5;

        byte currentState = START;
        char currentChar = 0;

        for (int i = 0; i < input.length(); i++) {
            currentChar = input.charAt(i);
            switch (currentState) {
                case START:
                    if (currentChar == escape) {
                        currentState = ESCAPE;
                    } else if (currentChar == quote) {
                        if (!stripQuotes) {
                            buf.append(currentChar);
                        }
                        currentState = QUOTE;
                    } else if (containsChar(separators, currentChar)) {
                        // do nothing
                    } else {
                        buf.append(currentChar);
                        currentState = APPEND;
                    }
                    break;
                case APPEND:
                    if (currentChar == escape) {
                        currentState = ESCAPE;
                    } else if (currentChar == quote) {
                        if (!stripQuotes) {
                            buf.append(currentChar);
                        }
                        currentState = QUOTE;
                    } else if (containsChar(separators, currentChar)) {
                        if (buf.length() > 0) {
                            result.add(buf.toString());
                            buf = new StringBuilder();
                        }
                        currentState = APPEND;
                    } else {
                        buf.append(currentChar);
                        currentState = APPEND;
                    }
                    break;
                case ESCAPE:
                    if (currentChar == escape) {
                        buf.append(currentChar);
                        currentState = APPEND;
                    } else if (currentChar == quote) {
                        buf.append(currentChar);
                        currentState = APPEND;
                    } else if (containsChar(separators, currentChar)) {
                        buf.append(currentChar);
                        currentState = APPEND;
                    } else {
                        buf.append(escape);
                        buf.append(currentChar);
                        currentState = APPEND;
                    }
                    break;
                case QUOTE: // this state can probably be minimized into the QAPPEND state (its outbound transitions are the same as QAPPEND)
                    if (currentChar == escape) {
                        currentState = QESCAPE;
                    } else if (currentChar == quote) {
                        if (!stripQuotes) {
                            buf.append(currentChar);
                        }
                        currentState = APPEND;
                    } else {
                        buf.append(currentChar);
                        currentState = QAPPEND;
                    }
                    break;
                case QAPPEND:
                    if (currentChar == escape) {
                        currentState = QESCAPE;
                    } else if (currentChar == quote) {
                        if (!stripQuotes) {
                            buf.append(currentChar);
                        }
                        currentState = APPEND;
                    } else {
                        buf.append(currentChar);
                        currentState = QAPPEND;
                    }
                    break;
                case QESCAPE:
                    if (currentChar == escape) {
                        buf.append(escape);
                    } else if (currentChar == quote) {
                        buf.append(currentChar);
                    } else {
                        buf.append(escape);
                        buf.append(currentChar);
                    }
                    currentState = QAPPEND;
                    break;
            }
        }

        if (currentChar == escape) {
            buf.append(currentChar);
        }

        // add the last chunk if not empty.
        if (buf.length() > 0) {
            result.add(buf.toString());
        }

        return result;
    }

    public static final Comparator<String> defaultComparator() {
        return DEFAULT_COMPARATOR;
    }

    public static boolean isTrue(String s) {
        String tr = trim(s);
        if (isEmpty(tr)) {
            return false;
        }
        return "true".equalsIgnoreCase(tr) || "yes".equalsIgnoreCase(tr);
    }

    public static boolean isFalse(String s) {
        String tr = trim(s);
        if (isEmpty(tr)) {
            return false;
        }
        return "false".equalsIgnoreCase(tr) || "no".equalsIgnoreCase(tr);
    }

    public static boolean containsChar(CharSequence seq, char c) {
        if (seq == null) {
            return false;
        }
        for (int i = 0; i < seq.length(); i++) {
            if (c == seq.charAt(i)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Eliminates all but one of the consecutive occurrences of character <code>dup</code> from the string <code>s</code>,
     * for instance <code>"//Library///foo/bar/"</code> would become <code>"/Library/foo/bar/"</code>.
     *
     * @param s   input string
     * @param dup char that is suspected to have consecutive duplicates inside <code>s</code>
     * @return the original string if <code>s</code> is null, empty or has no duplicates, a new string with duplicates
     * eliminated otherwise.
     */
    public static String eliminateDuplicateChars(String s, char dup) {
        if (s == null || s.length() == 0) {
            return s;
        }
        int len = s.length();
        StringBuilder tmp = new StringBuilder();
        boolean haveDup = false;
        for (int i = 0; i < len; i++) {
            Character c = s.charAt(i);
            if (c == dup) {
                if (!haveDup) {
                    tmp.append(c);
                    haveDup = true;
                }
            } else {
                tmp.append(c);
                haveDup = false;
            }
        }
        // if I did not do any elimination, avoid making a copy of the original string.
        if (tmp.length() == len) {
            return s;
        } else {
            return tmp.toString();
        }
    }

    /**
     * Counts the number of substring's in string.
     *
     * @param string
     * @param substring
     * @return
     */
    public static int countOccurrences(String string, String substring) {
        int idx = string.indexOf(substring);
        int count = 0;
        while (idx != -1) {
            count++;
            idx = string.indexOf(substring, idx + 1);
        }
        return count;
    }

    /**
     * counts the number of (unicode) characters in a string; this
     * method does not work with surrogate pairs, use
     * {@code Character.codePointCount()} instead.
     *
     * @param string    input string, can be null
     * @param character the character to count
     * @return how many characters found.
     */
    public static int countCharacters(String string, char character) {
        if (string == null) {
            return 0;
        }
        int len = string.length();
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (character == string.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * tests if a string contains only ASCII characters by attempting to encode it
     * using a US-ASCII encoder. It may be an expensive method, use sparingly.
     *
     * @param str input string
     * @return true if input is null or consists of only ASCII characters, false otherwise.
     */
    public static boolean isASCII(String str) {
        if (str == null) {
            return true;
        }
        CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();
        return encoder.canEncode(str);
    }

    /**
     * URL encode a string; Try with UTF-8 encoding, does not throw.
     *
     * @param input the raw string
     * @return
     */
    public static String urlEncode(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        try {
            return URLEncoder.encode(input, com.iconclude.dharma.commons.util.Constants.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            return input;
        }
    }

    public static void dumpString(String str) {
        System.out.println("dumpString(\"" + str + "\"): ");
        System.out.print("\tbytes: ");
        for (byte b : str.getBytes()) {
            System.out.printf("0x%x, ", b);
        }
        System.out.print("\n\tcodepoints: ");
        int cplen = str.length();
        for (int i = 0; i < cplen; ) {
            int cp = str.codePointAt(i);
            System.out.printf("U+%x,", cp);
            i += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
        }
        System.out.print("\n\tcode units (chars): ");
        CharacterIterator it = new StringCharacterIterator(str);
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            System.out.print(c + ", ");
        }
        System.out.print("\n\tcharacter iteration: ");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            System.out.print(c + ", ");
        }

        System.out.println();
    }

    /**
     * If you have a list of names that match {@code titlePattern}, which is a bunch of stuff
     * terminated by a number in round brackets (NN), and if you want to add one more name
     * like that to the list, then this routine will do it and take care to increment the NN
     * if necessary.
     * <p/>
     * <p>Examples:
     * <pre>
     * title=abc, titles=[abc], returns abc(1)
     * title=abc, titles=[abc,abc(1)] returns abc(2)
     * title=abc(2), titles=[abc,abc(1)] returns abc(2)
     * title=abc(2), titles=[abc,abc(2)] returns abc(3)
     * title=abc(2), titles=[abc,abc(3)] returns abc(4) // does not plug gaps
     * title=abc(6), titles=[abc,abc(1)] returns abc(6) // no need to increment
     * </pre>
     *
     * @param title  the new title to be added
     * @param titles the existing titles
     * @return a new title, the titles list does not get appended to - client must do that.
     */
    public static String deduplicateTitle(String title, Set<String> titles) {
        if (title == null || titles == null || titles.isEmpty()) {
            return title;
        }
        Pair<String, Integer> parsedTitle = parseTitle(title);
        String stem = parsedTitle.getFirst();
        int count = parsedTitle.getSecond();
        int maxCount = -1;
        boolean haveCount = false;
        for (String t : titles) {
            Pair<String, Integer> pt = parseTitle(t);
            if (stem.equals(pt.getFirst())) {
                maxCount = Math.max(maxCount, pt.getSecond());
                if (count > 0 && pt.getSecond() == count) {
                    haveCount = true;
                }
            }
        }
        if (count > 0 && !haveCount) {
            return title;
        }
        String newTitle = title;
        if (maxCount > -1) {
            maxCount++;
            newTitle = stem + "(" + maxCount + ")";
        }
        return newTitle;
    }

    public static Pair<String, Integer> parseTitle(String title) {
        if (title == null) {
            return null;
        }
        Matcher m = titlePattern.matcher(title);
        if (m.matches() && m.groupCount() == 2) {
            String stem = m.group(1);
            String number = m.group(2);
            try {
                int count = Integer.parseInt(number);
                if (count > 0) {
                    return new Pair<String, Integer>(stem, count);
                }
            } catch (Throwable ignore) {
            }
        }
        return new Pair<String, Integer>(title, 0);
    }

}
