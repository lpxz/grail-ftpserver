package org.apache.ftpserver.usermanager.impl;

import org.apache.ftpserver.ftplet.AuthorizationRequest;

public class ConcurrentLoginRequest implements AuthorizationRequest {

    private int concurrentLogins;

    private int concurrentLoginsFromThisIP;

    private int maxConcurrentLogins = 0;

    private int maxConcurrentLoginsPerIP = 0;

    public ConcurrentLoginRequest(int concurrentLogins, int concurrentLoginsFromThisIP) {
        super();
        this.concurrentLogins = concurrentLogins;
        this.concurrentLoginsFromThisIP = concurrentLoginsFromThisIP;
    }

    public int getConcurrentLogins() {
        return concurrentLogins;
    }

    public int getConcurrentLoginsFromThisIP() {
        return concurrentLoginsFromThisIP;
    }

    public int getMaxConcurrentLogins() {
        return maxConcurrentLogins;
    }

    void setMaxConcurrentLogins(int maxConcurrentLogins) {
        this.maxConcurrentLogins = maxConcurrentLogins;
    }

    public int getMaxConcurrentLoginsPerIP() {
        return maxConcurrentLoginsPerIP;
    }

    void setMaxConcurrentLoginsPerIP(int maxConcurrentLoginsPerIP) {
        this.maxConcurrentLoginsPerIP = maxConcurrentLoginsPerIP;
    }
}
