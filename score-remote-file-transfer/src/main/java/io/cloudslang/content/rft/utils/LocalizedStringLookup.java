/*
 * Created on Oct 27, 2005 by statu
 * Updated 4/3/08 by bmoeller
 */
package io.cloudslang.content.rft.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.ArrayUtils;

import java.net.URL;
import java.text.Format;
import java.text.MessageFormat;
import java.util.*;


/**
 * This class provides lookup of localized strings.  Unless you need the ability to specify a defaultString,
 * I recommend using Dharma.msg instead of LocalizedStringLookup.lookupMsg.  First, it's shorter.  Second, you
 * don't need to create an array to hold the parameters (Dharma.msg uses varargs).
 * <p/>
 * The key must begin with the bundle name, which is a shortened version of the language file.  For example, if your
 * basic language file is CentralMessages.properties and it contains a property "my.key.name=hello" then
 * you should call Dharma.msg("Central.my.key.name") and it will return "hello" for english.
 * <p/>
 * Message lookup can be disabled from the command line by specifying -DdisableMessageLookup=true and the locale can
 * be changed from the command line with -Dlanguage=<ISO language code> and -Dcountry=<ISO country code>
 * <p/>
 * The possible <ISO language code>s can be found at: http://www.loc.gov/standards/iso639-2/englangn.html
 * <p/>
 * The possible <ISO country code>s can be found at: http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html
 *
 * @author moellerb
 */
public class LocalizedStringLookup {

    private static final Log LOG = LogFactory.getLog(LocalizedStringLookup.class);

    private static final String MESSAGE_BUNDLES_SUFFIX = "Messages"; //$NON-NLS-1$

    private static final String DISABLE_MESSAGE_LOOKUP_PROPERTY = "disableMessageLookup"; //$NON-NLS-1$
    private static final String LANGUAGE_OVERRIDE_PROPERTY = "user.language"; //$NON-NLS-1$
    private static final String COUNTRY_OVERRIDE_PROPERTY = "user.region"; //$NON-NLS-1$

    protected static Map<String, ResourceBundle> _messageBundles;

    /**
     * Lookup a message without parameters.  The key must start with the bundle name.  For example, if your
     * basic language file is CentralMessages.properties and it contains a property "my.key.name=hello" then
     * using a key of "Central.my.key.name" will return "hello" for english.
     *
     * @param msgKey is the key starting with the bundle name
     * @return the localized message
     */
    public static String lookupMsg(String msgKey) {
        return lookupMsg(msgKey, (Object[]) null);
    }

    /**
     * Lookup a message with parameters.  The key must start with the bundle name.  For example, if your
     * basic language file is CentralMessages.properties and it contains a property "my.key.name={0} world" then
     * calling Dharma.msg("Central.my.key.name", "Hello"); will return "hello world" for english.
     *
     * @param msgKey    is the key starting with the bundle name
     * @param msgParams is a list of parameters
     * @return the localized message
     */
    public static String lookupMsg(String msgKey, Object[] msgParams) {
        return lookupMsg(msgKey, msgParams, (String) null);
    }

    /**
     * Lookup a message with parameters and a default.  The key must start with the bundle name.  This is
     * the only version of lookup that requires LocalizedStringLookup instead of DharmaMsg.  For example, if your
     * basic language file is CentralMessages.properties and it contains a property "my.key.name={0} world" then
     * calling LocalizedStringLookup.lookupMsg("Central.my.key.name", "Hello", "Goodbye"); will return "hello world" for english.
     * However, if CentralMessages.properties doesn't contain the property "my.key.name" then it will return "Goodbye".
     *
     * @param msgKey        is the key starting with the bundle name
     * @param msgParams     is a list of parameters
     * @param defaultString is the default value if the key cannot be found
     * @return the localized message
     */
    public static String lookupMsg(String msgKey, Object[] msgParams, String defaultString) {
        return lookupMsg(msgKey, msgParams, defaultString, (Locale) null);
    }

    /**
     * Lookup a message with parameters and a default.  The key must start with the bundle name.  This is
     * the only version of lookup that requires LocalizedStringLookup instead of DharmaMsg.  For example, if your
     * basic language file is CentralMessages.properties and it contains a property "my.key.name={0} world" then
     * calling LocalizedStringLookup.lookupMsg("Central.my.key.name", "Hello", "Goodbye"); will return "hello world" for english.
     * However, if CentralMessages.properties doesn't contain the property "my.key.name" then it will return "Goodbye".
     *
     * @param msgKey        is the key starting with the bundle name
     * @param msgParams     is a list of parameters
     * @param defaultString is the default value if the key cannot be found
     * @param locale        TODO
     * @return the localized message
     * @throws nothing it is not the business of localization code to throw exceptions; nor
     *                 is the business of the calling code to check for them. Localization ought to do whatever it can to present proper messages,
     *                 but where such thing is not possible, simply build a message out of the grabage that was passed in.
     */
    public static String lookupMsg(String msgKey, Object[] msgParams, String defaultString, Locale locale) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("________________________________________________________________");
            LOG.debug("lookup msg=" + msgKey + " def=" + defaultString + ", loc=" + locale);
        }

        if (msgKey == null || msgKey.length() == 0) {
            String errMsg = "null msgKey"; //$NON-NLS-1$
            String result = buildBestEffortMessage(msgKey, msgParams, defaultString);
            LOG.warn(errMsg + ", using: " + result);
            return result;
        }

        String[] split = msgKey.split("\\.", 2);         //$NON-NLS-1$

        String bundleName = null;
        if (split.length > 0) {
            bundleName = split[0] + MESSAGE_BUNDLES_SUFFIX;
        }

        String key = null;
        if (split.length > 1) {
            key = split[1];
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("  bundleName = " + bundleName);
            LOG.debug("  key = " + key);
        }

        boolean lookupDisabled = (System.getProperty(DISABLE_MESSAGE_LOOKUP_PROPERTY) != null);
        if (lookupDisabled) {
            return buildBestEffortMessage(msgKey, msgParams, defaultString);
        }

        if (bundleName == null) {
            // don't throw, see javadoc.
            String result = buildBestEffortMessage(msgKey, msgParams, defaultString);
            LOG.error("Bad key '" + msgKey + "' missing bundle name, using: " + result);
            return result;
        }

        if (key == null) {
            // don't throw, see javadoc.
            String result = buildBestEffortMessage(msgKey, msgParams, defaultString);
            LOG.error("Bad key '" + msgKey + "' missing lookup id, using: " + result);
            return result;
        }

        ResourceBundle bundle = getMessages(bundleName, locale);

        String msgFmt = null;
        if (bundle != null) {
            try {
                msgFmt = bundle.getString(key);
            } catch (Exception ignore) {
            }
        } else {
            LOG.debug("  didn't find bundle");
        }

        if (StringUtils.isNull(msgFmt)) {
            msgFmt = defaultString;
        }

        if (StringUtils.isNull(msgFmt)) {
            LOG.warn("Could not find message for key '" //$NON-NLS-1$
                    + key
                    + "' in resource bundle '" //$NON-NLS-1$
                    + bundleName + "'"); //$NON-NLS-1$
            return buildBestEffortMessage(msgKey, msgParams, defaultString);
        }

        String resultMsg;
        try {
            if (oddNumberOfSingleQuotes(msgFmt)) {
                // if there are an odd number of single quotes then this is likely a mistake...warn the coder!!
                LOG.warn("Odd number of single quotes for key " + msgKey + ".  Did you remember to escape the quote?");
            }

            MessageFormat fmt = new MessageFormat(msgFmt);
            Format[] formats = fmt.getFormatsByArgumentIndex();
            if (ArrayUtils.isEmpty(msgParams)) {
                if (formats.length != 0) {
                    LOG.warn("Missing " + formats.length + " parameters for key '" //$NON-NLS-1$ //$NON-NLS-2$
                            + key
                            + "' in resource bundle '" //$NON-NLS-1$
                            + bundleName + "'"); //$NON-NLS-1$
                }
            } else {
                if (formats.length != msgParams.length) {
                    LOG.warn("Incorrect number of parameters for key '" //$NON-NLS-1$
                            + key
                            + "' in resource bundle '" //$NON-NLS-1$
                            + bundleName + "'" //$NON-NLS-1$
                            + ", expected " + formats.length + ", recieved " + msgParams.length); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            // Note, we format in ALL cases...whether there were parameters or not.
            // This is to make the requirement for escaping single quotes consistent.
            resultMsg = fmt.format(msgParams);

        } catch (Exception ex) {
            LOG.error("Failed parsing of key '" //$NON-NLS-1$
                    + key
                    + "' in resource bundle '" //$NON-NLS-1$
                    + bundleName + "'", ex); //$NON-NLS-1$
            resultMsg = buildBestEffortMessage(msgKey, msgParams, defaultString);
        }

        if (LOG.isDebugEnabled())
            LOG.debug(" ------- result = " + resultMsg);

        return resultMsg;
    }

    private static boolean oddNumberOfSingleQuotes(String txt) {
        boolean oddNumberQuotes = false;
        for (char c : txt.toCharArray()) {
            if (c == '\'') {
                oddNumberQuotes = !oddNumberQuotes;
            }
        }

        return oddNumberQuotes;
    }

    /**
     * Build a pretty version of the key for when we can't find the message.
     *
     * @param msgKey        is the key for looking up the message
     * @param msgParams     are all of the parameters passed in
     * @param defaultString is the default value if the key is bad
     * @return the key and its parameters string-a-fied
     */
    protected static String buildBestEffortMessage(String msgKey, Object[] msgParams, String defaultString) {
        if ((msgKey == null || msgKey.isEmpty()) && (defaultString != null && !defaultString.isEmpty())) {
            return defaultString;
        }
        StringBuilder buf = new StringBuilder(msgKey == null ? "" : msgKey);
        if (msgParams != null && msgParams.length > 0) {
            buf.append(" [").append(StringUtils.arrayToString(msgParams, ", ")).append(']'); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return buf.toString();
    }

    /**
     * Get the resource bundle given a particular root bundle name.  This will check to see
     * if the locale has been overriden.  The locale changes what actual resource bundle is loaded.
     *
     * @param bundleName is the root name of the bundle to load
     * @return the resource bundle (or an empty resouce bundle if we failed to load)
     */
    protected static final synchronized ResourceBundle getMessages(String bundleName, Locale loc) {
        if (_messageBundles == null) {
            _messageBundles = new LinkedHashMap<String, ResourceBundle>();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("   getMessages(" + bundleName + ", loc=" + loc + ")");
        }

        String bundleCacheKey = bundleName + "_" + ((loc != null) ? loc.toString() : "Default");

        if (LOG.isDebugEnabled()) {
            LOG.debug("      cache key = " + bundleCacheKey);
        }

        ResourceBundle bundle = _messageBundles.get(bundleCacheKey);
        if (bundle == null) {
            LOG.debug("      cache not found, loading");
            // load the bundle
            try {
                // display the url for the bundle to aid debugging
                URL bundleUrl = Thread.currentThread().getContextClassLoader().getResource(bundleName + ".properties"); //$NON-NLS-1$
                String urlMsg = "URL for base bundle [" + bundleName + "]: " + bundleUrl; //$NON-NLS-1$ //$NON-NLS-2$
                LOG.info(urlMsg);

                // get the locale, check to see if the language or country has been overridden
                if (loc == null) {
                    String languageOverride = System.getProperty(LANGUAGE_OVERRIDE_PROPERTY);
                    String countryOverride = System.getProperty(COUNTRY_OVERRIDE_PROPERTY);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("      language override = " + languageOverride);
                        LOG.debug("      country override = " + countryOverride);
                    }
                    if (languageOverride != null) {
                        if (countryOverride != null) {
                            loc = new Locale(languageOverride, countryOverride);
                            Locale.getDefault();
                        } else {
                            loc = new Locale(languageOverride);
                        }
                    } else {
                        loc = Locale.getDefault();
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("      resulting locale = " + loc.getDisplayName());
                }

                // load the resource bundle
                bundle = ResourceBundle.getBundle(bundleName, loc);
                // cache it for later
                _messageBundles.put(bundleCacheKey, bundle);

                // display the locale that was used during lookup to aid debugging (this is not
                // necessarily the bundle that was found!
                String localeMsg = "Loaded resource bundle with locale " + loc.getDisplayName(); //$NON-NLS-1$
                LOG.debug(localeMsg);

            } catch (Exception ex) {
                String errMsg = "Could not find resource bundle: " + bundleName;//$NON-NLS-1$
                LOG.error(errMsg, ex);

                // We failed once, don't bother to look up the bundle again
                bundle = new ListResourceBundle() {
                    public Object[][] getContents() {
                        return new Object[0][];
                    }
                };
                _messageBundles.put(bundleCacheKey, bundle);
            }
        }

        return bundle;
    }

}
