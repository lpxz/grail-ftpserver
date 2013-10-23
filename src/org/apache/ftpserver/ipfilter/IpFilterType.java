package org.apache.ftpserver.ipfilter;

public enum IpFilterType {

    ALLOW, DENY;

    public static IpFilterType parse(String value) {
        for (IpFilterType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid IpFilterType: " + value);
    }
}
