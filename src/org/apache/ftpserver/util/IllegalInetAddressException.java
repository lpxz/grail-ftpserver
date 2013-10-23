package org.apache.ftpserver.util;

public class IllegalInetAddressException extends IllegalArgumentException {

    private static final long serialVersionUID = -7771719692741419933L;

    public IllegalInetAddressException() {
        super();
    }

    public IllegalInetAddressException(String s) {
        super(s);
    }
}
