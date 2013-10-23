package org.apache.ftpserver.usermanager.impl;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;

public class ConcurrentLoginPermission implements Authority {

    private int maxConcurrentLogins;

    private int maxConcurrentLoginsPerIP;

    public ConcurrentLoginPermission(int maxConcurrentLogins, int maxConcurrentLoginsPerIP) {
        this.maxConcurrentLogins = maxConcurrentLogins;
        this.maxConcurrentLoginsPerIP = maxConcurrentLoginsPerIP;
    }

    public AuthorizationRequest authorize(AuthorizationRequest request) {
        if (request instanceof ConcurrentLoginRequest) {
            ConcurrentLoginRequest concurrentLoginRequest = (ConcurrentLoginRequest) request;
            if (maxConcurrentLogins != 0 && maxConcurrentLogins < concurrentLoginRequest.getConcurrentLogins()) {
                return null;
            } else if (maxConcurrentLoginsPerIP != 0 && maxConcurrentLoginsPerIP < concurrentLoginRequest.getConcurrentLoginsFromThisIP()) {
                return null;
            } else {
                concurrentLoginRequest.setMaxConcurrentLogins(maxConcurrentLogins);
                concurrentLoginRequest.setMaxConcurrentLoginsPerIP(maxConcurrentLoginsPerIP);
                return concurrentLoginRequest;
            }
        } else {
            return null;
        }
    }

    public boolean canAuthorize(AuthorizationRequest request) {
        return request instanceof ConcurrentLoginRequest;
    }
}
