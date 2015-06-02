/**
 *
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.util.Constants;

import java.io.Serializable;


/**
 * @author octavian
 *         <p/>
 *         ATTENTION! At the moment GroupTemplate cannot be mixed with other IGroup derived
 *         classes (RCGroup and AuthGroup) inside containers or where equality is important,
 *         as each equals implementation across the 3 IGroup derivations are solely looking
 *         for its own class. We will have to change that if GroupTemplate instances mix
 *         with the others (for example in ACLs).
 */
public class GroupTemplate implements Serializable, Comparable, IGroup {
    //	 these are harcoded, "well-known" group templates
    public final static GroupTemplate ADMIN_TEMPL = new GroupTemplate("ADMINISTRATOR",
            "Represents " + Constants.APP_NAME + " administrators.",
            "a06bed09-9983-42af-a8ee-c34b30fd3913", true, true,
            new Capabilities(Capabilities.ALL_CAPS),
            // admin gets full access regardless
            null);
    public final static GroupTemplate AUDITOR_TEMPL = new GroupTemplate("AUDITOR",
            "Represents " + Constants.APP_NAME + " auditors. Users from this group have unconditional read access to the repository.",
            "5d03b4a0-9eb6-43b9-8053-dfbc6e0a778d", false, true,
            new Capabilities(Capabilities.CAPABILITY.RUN_REPORTS.val() | Capabilities.CAPABILITY.VIEW_SCHEDULES.val()),
            // auditor group get implicit read access regardless
            null);
    public final static GroupTemplate PROMOTER_TEMPL = new GroupTemplate("PROMOTER",
            "Represents " + Constants.APP_NAME + " promoters. Users from this group have unconditional read access to the repository.",
            "6492eb1a-b26c-41f8-b9ad-adc173e17260", false, true,
            new Capabilities(),
            // auditor group get implicit read access regardless
            null);
    public final static GroupTemplate EVERYBODY_TEMPL = new GroupTemplate("EVERYBODY",
            "Represents " + Constants.APP_NAME + " 'everybody' group. Every authenticated user is part of this group.",
            "6fbe84a8-d471-4fac-9b68-4416e17b3a15", false, true,
            new Capabilities(),
            new Permissions(Permissions.PERMISSION.READ, Permissions.PERMISSION.WRITE, Permissions.PERMISSION.EXECUTE));
    public final static GroupTemplate LEVEL_ONE_TEMPL = new GroupTemplate("LEVEL_ONE",
            "Represents " + Constants.APP_NAME + " level one users.",
            "a06bed09-9983-42af-a8ee-c34b30fd3914", false, false,
            new Capabilities(),
            null);
    public final static GroupTemplate LEVEL_TWO_TEMPL = new GroupTemplate("LEVEL_TWO",
            "Represents " + Constants.APP_NAME + " level two users.",
            "a06bed09-9983-42af-a8ee-c34b30fd3915", false, false,
            new Capabilities(),
            null);
    public final static GroupTemplate LEVEL_THREE_TEMPL = new GroupTemplate("LEVEL_THREE",
            "Represents " + Constants.APP_NAME + " level three users.",
            "a06bed09-9983-42af-a8ee-c34b30fd3916", false, false,
            new Capabilities(),
            null);
    public final static GroupTemplate[] DEFAULT_GROUPS_TEMPL = {
            ADMIN_TEMPL,
            AUDITOR_TEMPL,
            PROMOTER_TEMPL,
            EVERYBODY_TEMPL,
            LEVEL_ONE_TEMPL,
            LEVEL_TWO_TEMPL,
            LEVEL_THREE_TEMPL
    };
    private static final long serialVersionUID = 1149281477605863465L;
    private String _name;
    private String _anno;
    private String _uuid;
    private Capabilities _caps;
    private Permissions _perms;
    private boolean _admin;
    private boolean _builtIn;

    private GroupTemplate(String name, String anno, String uuid, boolean admin,
                          boolean builtIn, Capabilities caps, Permissions perms) {
        _name = name;
        _anno = anno;
        _uuid = uuid;
        _admin = admin;
        _builtIn = builtIn;
        _caps = caps;
        _perms = perms;
    }

    public static boolean isAdminGroup(IGroup group) {
        if (null == group)
            throw new IllegalArgumentException("null group passed to isAdminGroup");
        if (0 == GroupTemplate.ADMIN_TEMPL.getUuid().compareTo(group.getUuid()))
            return true;
        return false;
    }

    public static boolean isAuditorGroup(IGroup group) {
        if (null == group)
            throw new IllegalArgumentException("null group passed to isAuditorGroup");
        if (0 == GroupTemplate.AUDITOR_TEMPL.getUuid().compareTo(group.getUuid()))
            return true;
        return false;
    }

    public static boolean isPromoterGroup(IGroup group) {
        if (null == group)
            throw new IllegalArgumentException("null group passed to isPromoterGroup");
        if (0 == GroupTemplate.PROMOTER_TEMPL.getUuid().compareTo(group.getUuid()))
            return true;
        return false;
    }

    public static boolean isEverybodyGroup(IGroup group) {
        if (null == group)
            throw new IllegalArgumentException("null group passed to isEverybodyGroup");
        if (0 == GroupTemplate.EVERYBODY_TEMPL.getUuid().compareTo(group.getUuid()))
            return true;
        return false;
    }

    public String getName() {
        return _name;
    }

    public String getAnnotation() {
        return _anno;
    }

    public String getUuid() {
        return _uuid;
    }

    public boolean isAdministrator() {
        return _admin;
    }

    public boolean isBuiltIn() {
        return _builtIn;
    }

    public Capabilities getCapabilities() {
        return _caps;
    }

    public Permissions getPermissions() {
        return _perms;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GroupTemplate))
            return false;
        GroupTemplate other = (GroupTemplate) obj;
        // TODO this should use UUID instances so it is not case sensitive
        return (0 == other.getUuid().compareTo(this.getUuid()));
    }

    public int hashCode() {
        return getUuid().hashCode();
    }

    public int compareTo(Object obj) {
        if (obj == this)
            return 0;
        if (!(obj instanceof GroupTemplate))
            throw new ClassCastException();
        GroupTemplate other = (GroupTemplate) obj;
        // TODO this should use UUID instances so it is not case sensitive
        return other.getUuid().compareTo(this.getUuid());
    }
}