package org.apache.ftpserver.impl;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ConnectionConfigFactory;

public class DefaultConnectionConfig implements ConnectionConfig {

    private int maxLogins = 10;

    private boolean anonymousLoginEnabled = true;

    private int maxAnonymousLogins = 10;

    private int maxLoginFailures = 3;

    private int loginFailureDelay = 500;

    private int maxThreads = 0;

    public DefaultConnectionConfig(boolean anonymousLoginEnabled, int loginFailureDelay, int maxLogins, int maxAnonymousLogins, int maxLoginFailures, int maxThreads) {
        this.anonymousLoginEnabled = anonymousLoginEnabled;
        this.loginFailureDelay = loginFailureDelay;
        this.maxLogins = maxLogins;
        this.maxAnonymousLogins = maxAnonymousLogins;
        this.maxLoginFailures = maxLoginFailures;
        this.maxThreads = maxThreads;
    }

    public int getLoginFailureDelay() {
        return loginFailureDelay;
    }

    public int getMaxAnonymousLogins() {
        return maxAnonymousLogins;
    }

    public int getMaxLoginFailures() {
        return maxLoginFailures;
    }

    public int getMaxLogins() {
        return maxLogins;
    }

    public boolean isAnonymousLoginEnabled() {
        return anonymousLoginEnabled;
    }

    public int getMaxThreads() {
        return maxThreads;
    }
}
