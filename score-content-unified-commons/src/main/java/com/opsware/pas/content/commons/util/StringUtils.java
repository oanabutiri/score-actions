package com.opsware.pas.content.commons.util;

import com.iconclude.webservices.extensions.java.types.ActionRequest;
import com.iconclude.webservices.extensions.java.util.ActionRequestUtils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IAction static String Utility Methods.
 * <p/>
 * This class has been exposed to Content authors (partners and customers).
 * All changes made to this class MUST remain backwards compatible with previous versions.
 * <p/>
 * �Copyright 2008-2012 Hewlett-Packard Development Company, L.P.
 */
public class StringUtils {

    /**
     * Checks if a supplied String is null or empty.
     *
     * @param s String to check
     * @return true if supplied string is null or empty
     */
    public static boolean isNull(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Given a RAS Map and a key string, returns the value of the key/value pair.
     *
     * @param m A Map of key value pairs
     * @param s Map key whose object ** MUST ** be a string
     * @return String value or an empty string if Map key is empty or null
     */
    public static String resolveString(com.iconclude.webservices.extensions.java.types.Map m,
                                       String k) {
        k = (String) m.map(k);
        if (isNull(k))
            return "";
        else
            return k;
    }


    /**
     * Grab an input from a java.util.Map<String,String>, such as you'd get form ActionAdapter.
     * If it's not there throw an exception, since it's a required input.
     *
     * @param inputs
     * @param key
     * @return
     * @throws Exception
     */
    public static String resolveRequiredString(java.util.Map<String, String> inputs, String key) throws Exception {

        String inputValue = inputs.get(key);

        if (isNull(inputValue)) {
            throw new Exception("Input '" + key + "' is required, but was not found.");
        }

        return inputValue;

    }


    /**
     * resolveString for a java.util.Map
     *
     * @param inputs
     * @param key
     * @return
     */
    public static String resolveString(java.util.Map<String, String> inputs, String key) {

        return resolve(inputs.get(key));
    }


    /**
     * Returns contents of an input from an ActionRequest as a string.
     * <p/>
     * If the input does not exist, is empty or null, an empty string is returned.
     *
     * @param ar        ActionRequest to read input from
     * @param inputName The name of the input to return from the ActionRequest
     * @return the string representation of the input or
     *         an empty string if the input is empty or null
     */
    public static String resolveString(ActionRequest ar, String inputName) {
        String s = ActionRequestUtils.resolveStringParam(ar, inputName);
        if (isNull(s))
            return "";
        else
            return s;
    }

    /**
     * Returns the contents of an input from an ActionRequest as a string.
     * <p/>
     * The inputName may have changed over time so multiple 'aliases' can be
     * consulted.
     *
     * @param ar
     * @param inputName
     * @param deprecated alias array
     * @return the string representation of the input or
     *         an empty string if the input is empty or null
     */
    public static String resolveString(ActionRequest ar,
                                       String inputName,
                                       String[] deprecated) {
        // Try current input name
        String resolved = resolveString(ar, inputName);
        if (!isNull(resolved))
            return resolved;

        // Try alias array
        for (int count = 0; count < deprecated.length; count++) {
            resolved = resolveString(ar, deprecated[count]);
            if (!isNull(resolved))
                return resolved;
        }

        // Not found with current name or any aliases
        return "";
    }

    /**
     * @param ar        ActionRequest to resolve input from.
     * @param inputName Input name to resolve from the ActionRequest.
     * @return
     * @throws IllegalArgumentException When input is not true or false.
     */
    public static boolean resolveRequiredBoolean(ActionRequest ar, String inputName) throws IllegalArgumentException {
        return resolveRequiredBoolean(inputName, resolveString(ar, inputName));
    }

    /**
     * @param inputs    Map of inputs.  This is typically available in operations that use Action Adapter.
     * @param inputName Input name to resolve from the input Map.
     * @return
     * @throws IllegalArgumentException When input is not true or false.
     */

    public static boolean resolveRequiredBoolean(java.util.Map<String, String> inputs, String inputName) throws IllegalArgumentException {
        return resolveRequiredBoolean(inputName, inputs.get(inputName));
    }

    /**
     * @param name  used to create descriptive exceptions if value is not blank and not true or false.
     * @param value to validate if it is true, false.
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean resolveRequiredBoolean(String name, String value) throws IllegalArgumentException {
        if (StringUtils.isNull(value)) {
            //this is a required input:
            throw new IllegalArgumentException(name + " input is required.  Valid values: true, false");
        } else if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid value for " + name + " input.  Valid values: true, false");
        }
    }

    /**
     * @param ar        ActionRequest to resolve input from.
     * @param inputName Input name to resolve from the ActionRequest.
     * @return
     * @throws IllegalArgumentException When input is not true or false.
     */
    public static int resolveRequiredInteger(ActionRequest ar, String inputName) throws IllegalArgumentException {
        return resolveRequiredInteger(inputName, resolveString(ar, inputName), true);
    }

    /**
     * @param inputs    Map of inputs.  This is typically available in operations that use Action Adapter.
     * @param inputName Input name to resolve from the input Map.
     * @return
     * @throws IllegalArgumentException When input is not true or false.
     */

    public static int resolveRequiredInteger(java.util.Map<String, String> inputs, String inputName) throws IllegalArgumentException {
        return resolveRequiredInteger(inputName, inputs.get(inputName), true);
    }

    /**
     * @param name  used to create descriptive exceptions if value is not blank and not true or false.
     * @param value to validate if it is true, false.
     * @return
     * @throws IllegalArgumentException
     */
    public static int resolveRequiredInteger(String name, String value, boolean positiveOnly) throws IllegalArgumentException {
        if (StringUtils.isNull(value)) {
            //this is a required input:
            if (positiveOnly) {
                throw new IllegalArgumentException(name + " input is required.  Valid values are positive integers.");
            }
            throw new IllegalArgumentException(name + " input is required.  Valid values are positive or negative integers.");
        }

        try {
            int retVal = Integer.parseInt(value);
            if (positiveOnly && (retVal < 0)) {
                throw new IllegalArgumentException("Input '" + name + "' must be an positive integer.");
            }
            return retVal;
        } catch (NumberFormatException nfe) {
            if (positiveOnly) {
                throw new IllegalArgumentException("Invalid value for " + name + " input.  Valid values are positive integers.", nfe);
            }
            throw new IllegalArgumentException("Invalid value for " + name + " input.  Valid values are positive and negative integers.", nfe);
        }
    }


    /**
     * @param ar        ActionRequest to resolve input from.
     * @param inputName Input name to resolve from the ActionRequest.
     * @return
     * @throws IllegalArgumentException When input is not true or false.
     */
    public static long resolveRequiredLong(ActionRequest ar, String inputName) throws IllegalArgumentException {
        return resolveRequiredLong(inputName, resolveString(ar, inputName), true);
    }

    /**
     * @param inputs    Map of inputs.  This is typically available in operations that use Action Adapter.
     * @param inputName Input name to resolve from the input Map.
     * @return
     * @throws IllegalArgumentException When input is not true or false.
     */

    public static long resolveRequiredLong(java.util.Map<String, String> inputs, String inputName) throws IllegalArgumentException {
        return resolveRequiredLong(inputName, inputs.get(inputName), true);
    }


    /**
     * @param name  used to create descriptive exceptions if value is not blank and not true or false.
     * @param value to validate if it is true, false.
     * @return
     * @throws IllegalArgumentException
     */
    public static long resolveRequiredLong(String name, String value, boolean positiveOnly) throws IllegalArgumentException {
        if (StringUtils.isNull(value)) {
            //this is a required input:
            if (positiveOnly) {
                throw new IllegalArgumentException(name + " input is required.  Valid values are positive integers.");
            }
            throw new IllegalArgumentException(name + " input is required.  Valid values are positive or negative integers.");
        }

        try {
            long retVal = Long.parseLong(value);
            if (positiveOnly && (retVal < 0)) {
                throw new IllegalArgumentException("Input '" + name + "' must be an positive integer.");
            }
            return retVal;
        } catch (NumberFormatException nfe) {
            if (positiveOnly) {
                throw new IllegalArgumentException("Invalid value for " + name + " input.  Valid values are positive integers.", nfe);
            }
            throw new IllegalArgumentException("Invalid value for " + name + " input.  Valid values are positive and negative integers.", nfe);
        }
    }


    /**
     * @param ar        ActionRequest to resolve input from.
     * @param inputName Input name to resolve from the ActionRequest
     * @param def       Default value when input is empty in ActionRequest.
     * @return
     * @throws IllegalArgumentException if input is not blank and not true or false.
     */
    public static Boolean resolveOptionalBoolean(ActionRequest ar, String inputName, Boolean def) throws IllegalArgumentException {
        return resolveOptionalBoolean(inputName, resolveString(ar, inputName), def);
    }

    /**
     * @param inputs    Map of inputs.  This is typically available in operations that use Action Adapter.
     * @param inputName Input name to resolve from the input Map.
     * @param def       Default value when input is empty in input Map.
     * @return
     * @throws IllegalArgumentException
     */
    public static Boolean resolveOptionalBoolean(java.util.Map<String, String> inputs, String inputName, Boolean def) throws IllegalArgumentException {
        return resolveOptionalBoolean(inputName, inputs.get(inputName), def);
    }

    /**
     * @param name  used to create descriptive exceptions if value is not blank and not true or false.
     * @param value to validate if it is true, false.
     * @param def   default return if value is blank.
     * @return
     * @throws IllegalArgumentException if value is not blank and not true or false.
     */
    public static Boolean resolveOptionalBoolean(String name, String value, Boolean def) throws IllegalArgumentException {
        if (StringUtils.isNull(value)) {
            return def;
        } else if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid value for input '" + name + "'.  Valid values: true, false");
        }
    }


    /**
     * @param ar        ActionRequest to resolve input from.
     * @param inputName Input name to resolve from the ActionRequest
     * @param def       Default value when input is empty in ActionRequest.
     * @return
     * @throws IllegalArgumentException if input is not blank and not true or false.
     */
    public static Integer resolveOptionalInteger(ActionRequest ar, String inputName, Integer def) throws IllegalArgumentException {
        return resolveOptionalInteger(inputName, resolveString(ar, inputName), def, true);
    }

    /**
     * @param ar           ActionRequest to resolve input from.
     * @param inputName    Input name to resolve from the ActionRequest
     * @param def          Default value when input is empty in ActionRequest.
     * @param positiveOnly if true will throw IllegalArgumentException if value is less than zero.
     * @return
     * @throws IllegalArgumentException if input is not blank and not true or false.
     */
    public static Integer resolveOptionalInteger(ActionRequest ar, String inputName, Integer def, boolean positiveOnly) throws IllegalArgumentException {
        return resolveOptionalInteger(inputName, resolveString(ar, inputName), def, positiveOnly);
    }

    /**
     * @param inputs    Map of inputs.  This is typically available in operations that use Action Adapter.
     * @param inputName Input name to resolve from the input Map.
     * @param def       Default value when input is empty in input Map.
     * @return
     * @throws IllegalArgumentException
     */
    public static Integer resolveOptionalInteger(java.util.Map<String, String> inputs, String inputName, Integer def) throws IllegalArgumentException {
        return resolveOptionalInteger(inputName, inputs.get(inputName), def, true);
    }

    /**
     * @param name         used to create descriptive exceptions if value is not blank and not true or false.
     * @param value        to validate if it is an integer.
     * @param def          default return if value is blank.
     * @param positiveOnly if true will throw IllegalArgumentException if value is less than zero.
     * @return
     * @throws IllegalArgumentException if value is not an integer.
     */
    public static Integer resolveOptionalInteger(String name, String value, Integer def, boolean positiveOnly) throws IllegalArgumentException {
        if (StringUtils.isNull(value)) {
            return def;
        }
        try {
            Integer retVal = Integer.parseInt(value);
            if (positiveOnly && (retVal < 0)) {
                throw new IllegalArgumentException("Input '" + name + "' must be an positive integer.");
            }
            return retVal;
        } catch (NumberFormatException nfe) {
            if (positiveOnly) {
                throw new IllegalArgumentException("Input '" + name + "' must be positive integer.", nfe);
            } else {
                throw new IllegalArgumentException("Input '" + name + "' must be an integer.", nfe);
            }
        }
    }


    /**
     * @param ar        ActionRequest to resolve input from.
     * @param inputName Input name to resolve from the ActionRequest
     * @param def       Default value when input is empty in ActionRequest.
     * @return
     * @throws IllegalArgumentException if input is not blank and not true or false.
     */
    public static Long resolveOptionalLong(ActionRequest ar, String inputName, Long def) throws IllegalArgumentException {
        return resolveOptionalLong(inputName, resolveString(ar, inputName), def, true);
    }

    /**
     * @param ar           ActionRequest to resolve input from.
     * @param inputName    Input name to resolve from the ActionRequest
     * @param def          Default value when input is empty in ActionRequest.
     * @param positiveOnly if true will throw IllegalArgumentException if value is less than zero.
     * @return
     * @throws IllegalArgumentException if input is not blank and not true or false.
     */
    public static Long resolveOptionalLong(ActionRequest ar, String inputName, Long def, boolean positiveOnly) throws IllegalArgumentException {
        return resolveOptionalLong(inputName, resolveString(ar, inputName), def, positiveOnly);
    }

    /**
     * @param inputs    Map of inputs.  This is typically available in operations that use Action Adapter.
     * @param inputName Input name to resolve from the input Map.
     * @param def       Default value when input is empty in input Map.
     * @return
     * @throws IllegalArgumentException
     */
    public static Long resolveOptionalLong(java.util.Map<String, String> inputs, String inputName, Long def) throws IllegalArgumentException {
        return resolveOptionalLong(inputName, inputs.get(inputName), def, true);
    }

    /**
     * @param name         used to create descriptive exceptions if value is not blank and not true or false.
     * @param value        to validate if it is an integer.
     * @param def          default return if value is blank.
     * @param positiveOnly if true will throw IllegalArgumentException if value is less than zero.
     * @return
     * @throws IllegalArgumentException if value is not an integer.
     */
    public static Long resolveOptionalLong(String name, String value, Long def, boolean positiveOnly) throws IllegalArgumentException {
        if (StringUtils.isNull(value)) {
            return def;
        }
        try {
            Long retVal = Long.parseLong(value);
            if (positiveOnly && (retVal < 0)) {
                throw new IllegalArgumentException("Input '" + name + "' must be an positive long.");
            }
            return retVal;
        } catch (NumberFormatException nfe) {
            if (positiveOnly) {
                throw new IllegalArgumentException("Input '" + name + "' must be positive long.", nfe);
            } else {
                throw new IllegalArgumentException("Input '" + name + "' must be a long.", nfe);
            }
        }
    }


    /**
     * Converts string to a boolean value.
     * If string is null or empty, the default boolean value is returned
     *
     * @param s   String to parse as a Boolean
     * @param def Default Boolean value if String is null or empty
     * @return a Boolean representation of the input string or the def boolean parameter
     */
    public static boolean resolve(String s, boolean def) {
        if (isNull(s))
            return def;

        return Boolean.parseBoolean(s);
    }

    /**
     * If string is null or empty, the default value is returned. Otherwise the given value is returned
     *
     * @param s   String
     * @param def Default value if s is null or empty
     * @return a Input string if populated, or the def parameter
     */
    public static String resolve(String s, String def) {
        if (isNull(s))
            return def;

        return s;
    }

    /**
     * If string is null or empty, then an empty string is returned. Otherwise the given value is returned.
     *
     * @param s String
     * @return a Input string if populated, or an empty string
     */
    public static String resolve(String s) {
        if (isNull(s))
            return "";

        return s;
    }


    /**
     * Returns contents of an input from an ActionRequest as a boolean.
     * <p/>
     * If the input does not exist, is empty or null, a default value will be returned.
     *
     * @param ar        ActionRequest to read input from
     * @param inputName The name of the input to return from the ActionRequest
     * @param def       The default value to return if the input is empty or null
     * @return the boolean representation of the input or
     *         the default value if input is empty or null
     */
    public static boolean resolve(ActionRequest ar,
                                  String inputName,
                                  boolean def) {
        return resolve(resolveString(ar, inputName), def);
    }

    /**
     * Given a RAS Map and a key string, returns the value of the
     * key/value pair as a boolean.
     *
     * @param m   A Map of key value pairs
     * @param k   Map key
     * @param def The default value to return if the Map value is empty or null
     * @return the boolean representation of the Map value or
     *         the default value if Map value is empty or null
     */
    public static boolean resolve(com.iconclude.webservices.extensions.java.types.Map m,
                                  String k,
                                  boolean def) {
        return resolve(resolveString(m, k), def);
    }

    /**
     * Converts string to an integer value.
     * If string is null or empty, the default integer value is returned.
     *
     * @param s   String to parse as am integer
     * @param def Default integer value if String is null or empty
     * @return an integer representation of the input string or the def integer parameter
     */
    public static int resolve(String s, int def) {
        if (isNull(s))
            return def;

        return Integer.parseInt(s);
    }

    /**
     * Returns contents of an input from an ActionRequest as an integer.
     * If the input does not exist, is empty or null, a default value will be returned.
     *
     * @param ar        ActionRequest to read input from
     * @param inputName The name of the input to return from the ActionRequest
     * @param def       The default value to return if the input is empty or null
     * @return the integer representation of the input or
     *         the default value if input is empty or null
     */
    public static int resolve(ActionRequest ar, String inputName, int def) {
        return resolve(resolveString(ar, inputName), def);
    }

    /**
     * Given a RAS Map and a key string, returns the value of the
     * key/value pair as an integer.
     *
     * @param m   A Map of key value pairs
     * @param k   Map key
     * @param def The default value to return if the Map value is empty or null
     * @return the integer representation of the Map value or
     *         the default value if Map value is empty or null
     */
    public static int resolve(com.iconclude.webservices.extensions.java.types.Map m,
                              String k,
                              int def) {
        return resolve(resolveString(m, k), def);
    }

    /**
     * Converts string to a long value.  If string is null or empty, the def long value is returned
     *
     * @param s   String to parse as a integer
     * @param def Default long value if String is null or empty
     * @return a long representation of the input string or the def long parameter.
     */
    public static long resolve(String s, long def) {
        if (isNull(s))
            return def;

        return Long.parseLong(s);
    }

    /**
     * Returns contents of an input from an ActionRequest as a long.
     * If the input does not exist, is empty or null, a default value will be returned.
     *
     * @param ar        ActionRequest to read input from
     * @param inputName The name of the input to return from the ActionRequest
     * @param def       The default value to return if the input is empty or null
     * @return the long representation of the input or
     *         the default value if input is empty or null
     */
    public static long resolve(ActionRequest ar, String inputName, long def) {
        return resolve(resolveString(ar, inputName), def);
    }

    /**
     * Returns contents of an input from an ActionRequest as a string.
     * If the input does not exist, is empty or null, a default value will be returned.
     *
     * @param ar        ActionRequest to read input from
     * @param inputName The name of the input to return from the ActionRequest
     * @param def       The default value to return if the input is empty or null
     * @return the string representation of the input or
     *         the default value if input is empty or null
     */
    public static String resolve(ActionRequest ar, String inputName, String def) {
        // Try and get a value for the named input
        String result = resolveString(ar, inputName);
        if (isNull(result))
            result = def; // Use default

        return result;
    }

    public static String resolve(java.util.Map<String, String> ar, String inputName, String def) {
        // Try and get a value for the named input
        String result = resolveString(ar, inputName);
        if (isNull(result))
            result = def; // Use default

        return result;
    }

    /**
     * Returns contents of an input from a RAS Map as a long.
     * If the input does not exist, is empty or null, a default value will be returned.
     *
     * @param m         RAS Map to read input from
     * @param inputName The name of the input to return from the RAS Map
     * @param def       The default value to return if the input is empty or null
     * @return the long representation of the input or
     *         the default value if input is empty or null
     */
    public static long resolve(com.iconclude.webservices.extensions.java.types.Map m,
                               String inputName,
                               long def) {
        return resolve(resolveString(m, inputName), def);
    }

    /**
     * Scrubs an Exception stacktrace??
     *
     * @param e Exception to dump the stacktrace from
     * @return Stacktrace with the first NULL terminator removed
     */
    public static String toString(Throwable e) {
        // Print the stack trace into an in memory string
        StringWriter writer = new StringWriter();
        e.printStackTrace(new java.io.PrintWriter(writer));

        // Process the stack trace, remove the FIRST null character
        return writer.toString().replace("" + (char) 0x00, "");
    }

    /**
     * Same as @see processNullTerminatedString except does multiple
     * instances.
     * <p/>
     * TODO: rename to match @see processNullTerminatedString
     * <p/>
     * Only caller:  com.iconclude.content.actions.ldap.ADDeleteUser
     *
     * @param s
     * @return
     */
    public static String replaceInvalidXMLCharacters(String s) {
        // List of characters to replace
        List<String> list = new ArrayList<String>();
        list.add("" + (char) 0x00); // Null terminated emtpy string

        for (String c : list)
            s = s.replace(c, "");

        return s;
    }

    /**
     * Chop off '\0' character if present in a String.
     * <p/>
     * This only removes 1 trailing '\0' (null) character.
     * TODO: rename to match @see replaceInvalidXMLCharacters
     * Only caller: com.opsware.pas.content.commons.util.sql.Format
     *
     * @param s String that may have a null character at the end
     * @return a String without a trailing '\0' character.
     */
    public static String processNullTerminatedString(String s) {
        String returnValue = s;

        char[] charArray = s.toCharArray();

        // Case 1: Empty null terminated String
        if (charArray.length == 1 && (int) charArray[0] <= 0) {
            return "null";
        } else { // Case 2: Non-empty null terminated String
            // Strip trailing '\0' character if any
            if ((int) charArray[charArray.length - 1] <= 0)
                returnValue = s.substring(0, s.length() - 1);
        }

        return returnValue;
    }

    /**
     * Can be used to expand sequences like ${} ??
     * <p/>
     * TODO: make this usable (well documented) or push it down into it's only caller.
     * (CommandLineBuilder)
     *
     * @param toProcess
     * @param proc
     * @param pattern
     * @return input string after being processed??
     */
    public static String match(String toProcess, MatchProcessor proc, Pattern pattern) {
        StringBuilder buff = new StringBuilder();

        Matcher matcher = pattern.matcher(toProcess);

        int nextChar = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            buff.append(proc.processUnmatchedPortion(toProcess, nextChar, start));
            buff.append(proc.processMatchedPortion(toProcess, start, end));
            nextChar = end;
        }
        if (nextChar != toProcess.length())
            buff.append(proc.processUnmatchedPortion(toProcess, nextChar, toProcess.length()));

        return buff.toString();
    }

    /**
     * Interface for the MatchProcessor used by match()
     */
    public static interface MatchProcessor {
        public String processMatchedPortion(String toMatch, int start, int end);

        public String processUnmatchedPortion(String toMatch, int start, int end);
    }


    public static final String XML_AMP = "&amp;";
    public static final String XML_QUOTE = "&quot;";
    public static final String XML_LESS = "&lt;";
    public static final String XML_GREATER = "&gt;";
    public static final String XML_LF = "\n";
    public static final String XML_CR = "\r";
    public static final String XML_TAB = "\t";

    /**
     * Encodes a string into a value suitable to be put in an XML document by making sure
     * that Unicode characters are replaced with ampersand entities and that unprintable
     * characters are eliminated.
     */
    public static String toXML(String val) {
        if (val == null || val.isEmpty()) {
            return val;
        }
        int length = val.length();
        StringBuilder buf = new StringBuilder();
        int codepoint;
        for (int i = 0; i < length; i++) {
            codepoint = val.codePointAt(i);
            switch (codepoint) {
                case '&':
                    buf.append(XML_AMP);
                    break;
                case '"':
                    buf.append(XML_QUOTE);
                    break;
                case '<':
                    buf.append(XML_LESS);
                    break;
                case '>':
                    buf.append(XML_GREATER);
                    break;
                case '\n':
                    buf.append(XML_LF);
                    break;
                case '\r':
                    buf.append(XML_CR);
                    break;
                case '\t':
                    buf.append(XML_TAB);
                    break;
                default:
                    if (codepoint >= 0x20) {
                        if (codepoint > 0x7F) {
                            buf.append("&#x");
                            buf.append(Integer.toHexString(codepoint).toUpperCase());
                            buf.append(";");
                            if (Character.isSupplementaryCodePoint(codepoint)) {
                                i++; // this codepoints consists of 2 chars.
                            }
                        } else {
                            buf.append((char) codepoint);
                        }
                    }
                    break;
            }
        }
        return buf.toString();
    }
}
