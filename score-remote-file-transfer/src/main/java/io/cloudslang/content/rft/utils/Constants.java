/*
 * Created on May 23, 2006
 *
 */
package io.cloudslang.content.rft.utils;

// TODO this was moved here from Central project because of the need to access the product names,
// and it is quite Central/Studio specific...
public class Constants {

    // root directory (relative to ICONCLUDE_HOME) for CEntral
    public static final String TRIAL_POSTFIX;

    static {
        if (Boolean.parseBoolean(System.getProperty("trial.mode"))) {
            TRIAL_POSTFIX = " Trial";
        } else {
            TRIAL_POSTFIX = "";
        }
    }
}
