package org.mads.iotapipub.version;

/**
 *
 */
public class Version {
    public static String getVERSION() {
        return VERSION;
    }

    private static final String VERSION="0.1";
    private static final String BUILD_TYPE="alpha";

    public static String getName() {
        return NAME;
    }

    private static final String NAME="IOTAPI publisher";

    public static String getBuildType() {
        return BUILD_TYPE;
    }

}
