package org.apache.ftpserver.util;

import java.util.Locale;

public final class OS {

    private static final String FAMILY_OS_400 = "os/400";

    private static final String FAMILY_Z_OS = "z/os";

    private static final String FAMILY_WIN9X = "win9x";

    private static final String FAMILY_OPENVMS = "openvms";

    private static final String FAMILY_UNIX = "unix";

    private static final String FAMILY_TANDEM = "tandem";

    private static final String FAMILY_MAC = "mac";

    private static final String FAMILY_DOS = "dos";

    private static final String FAMILY_NETWARE = "netware";

    private static final String FAMILY_OS_2 = "os/2";

    private static final String FAMILY_WINDOWS = "windows";

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);

    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);

    private static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);

    private static final String PATH_SEP = System.getProperty("path.separator");

    private OS() {
    }

    private static boolean isFamily(final String family) {
        return isOs(family, null, null, null);
    }

    public static boolean isFamilyDOS() {
        return isFamily(FAMILY_DOS);
    }

    public static boolean isFamilyMac() {
        return isFamily(FAMILY_MAC);
    }

    public static boolean isFamilyNetware() {
        return isFamily(FAMILY_NETWARE);
    }

    public static boolean isFamilyOS2() {
        return isFamily(FAMILY_OS_2);
    }

    public static boolean isFamilyTandem() {
        return isFamily(FAMILY_TANDEM);
    }

    public static boolean isFamilyUnix() {
        return isFamily(FAMILY_UNIX);
    }

    public static boolean isFamilyWindows() {
        return isFamily(FAMILY_WINDOWS);
    }

    public static boolean isFamilyWin9x() {
        return isFamily(FAMILY_WIN9X);
    }

    public static boolean isFamilyZOS() {
        return isFamily(FAMILY_Z_OS);
    }

    public static boolean isFamilyOS400() {
        return isFamily(FAMILY_OS_400);
    }

    public static boolean isFamilyOpenVms() {
        return isFamily(FAMILY_OPENVMS);
    }

    public static boolean isName(final String name) {
        return isOs(null, name, null, null);
    }

    public static boolean isArch(final String arch) {
        return isOs(null, null, arch, null);
    }

    public static boolean isVersion(final String version) {
        return isOs(null, null, null, version);
    }

    public static boolean isOs(final String family, final String name, final String arch, final String version) {
        boolean retValue = false;
        if (family != null || name != null || arch != null || version != null) {
            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;
            if (family != null) {
                if (family.equals(FAMILY_WINDOWS)) {
                    isFamily = OS_NAME.indexOf(FAMILY_WINDOWS) > -1;
                } else if (family.equals(FAMILY_OS_2)) {
                    isFamily = OS_NAME.indexOf(FAMILY_OS_2) > -1;
                } else if (family.equals(FAMILY_NETWARE)) {
                    isFamily = OS_NAME.indexOf(FAMILY_NETWARE) > -1;
                } else if (family.equals(FAMILY_DOS)) {
                    isFamily = PATH_SEP.equals(";") && !isFamily(FAMILY_NETWARE);
                } else if (family.equals(FAMILY_MAC)) {
                    isFamily = OS_NAME.indexOf(FAMILY_MAC) > -1;
                } else if (family.equals(FAMILY_TANDEM)) {
                    isFamily = OS_NAME.indexOf("nonstop_kernel") > -1;
                } else if (family.equals(FAMILY_UNIX)) {
                    isFamily = PATH_SEP.equals(":") && !isFamily(FAMILY_OPENVMS) && (!isFamily(FAMILY_MAC) || OS_NAME.endsWith("x"));
                } else if (family.equals(FAMILY_WIN9X)) {
                    isFamily = isFamily(FAMILY_WINDOWS) && (OS_NAME.indexOf("95") >= 0 || OS_NAME.indexOf("98") >= 0 || OS_NAME.indexOf("me") >= 0 || OS_NAME.indexOf("ce") >= 0);
                } else if (family.equals(FAMILY_Z_OS)) {
                    isFamily = OS_NAME.indexOf(FAMILY_Z_OS) > -1 || OS_NAME.indexOf("os/390") > -1;
                } else if (family.equals(FAMILY_OS_400)) {
                    isFamily = OS_NAME.indexOf(FAMILY_OS_400) > -1;
                } else if (family.equals(FAMILY_OPENVMS)) {
                    isFamily = OS_NAME.indexOf(FAMILY_OPENVMS) > -1;
                } else {
                    throw new IllegalArgumentException("Don\'t know how to detect os family \"" + family + "\"");
                }
            }
            if (name != null) {
                isName = name.equals(OS_NAME);
            }
            if (arch != null) {
                isArch = arch.equals(OS_ARCH);
            }
            if (version != null) {
                isVersion = version.equals(OS_VERSION);
            }
            retValue = isFamily && isName && isArch && isVersion;
        }
        return retValue;
    }
}
