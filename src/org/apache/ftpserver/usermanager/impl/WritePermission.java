package org.apache.ftpserver.usermanager.impl;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;

public class WritePermission implements Authority {

    private String permissionRoot;

    public WritePermission() {
        this.permissionRoot = "/";
    }

    public WritePermission(final String permissionRoot) {
        this.permissionRoot = permissionRoot;
    }

    public AuthorizationRequest authorize(final AuthorizationRequest request) {
        if (request instanceof WriteRequest) {
            WriteRequest writeRequest = (WriteRequest) request;
            String requestFile = writeRequest.getFile();
            if (requestFile.startsWith(permissionRoot)) {
                return writeRequest;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean canAuthorize(final AuthorizationRequest request) {
        return request instanceof WriteRequest;
    }
}
