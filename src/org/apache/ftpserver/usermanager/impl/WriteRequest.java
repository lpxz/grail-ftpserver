package org.apache.ftpserver.usermanager.impl;

import org.apache.ftpserver.ftplet.AuthorizationRequest;

public class WriteRequest implements AuthorizationRequest {

    private String file;

    public WriteRequest() {
        this("/");
    }

    public WriteRequest(final String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }
}
