package org.apache.ftpserver;

import org.apache.ftpserver.impl.DefaultConnectionConfig;

public class ConnectionConfigFactory {

    private int maxLogins = 10;

    private boolean anonymousLoginEnabled = true;

    private int maxAnonymousLogins = 10;

    private int maxLoginFailures = 3;

    private int loginFailureDelay = 500;

    private int maxThreads = 0;

    public ConnectionConfig createConnectionConfig() {
        return new DefaultConnectionConfig(anonymousLoginEnabled, loginFailureDelay, maxLogins, maxAnonymousLogins, maxLoginFailures, maxThreads);
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

    public void setMaxLogins(final int maxLogins) {
        this.maxLogins = maxLogins;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setAnonymousLoginEnabled(final boolean anonymousLoginEnabled) {
        this.anonymousLoginEnabled = anonymousLoginEnabled;
    }

    public void setMaxAnonymousLogins(final int maxAnonymousLogins) {
        this.maxAnonymousLogins = maxAnonymousLogins;
    }

    public void setMaxLoginFailures(final int maxLoginFailures) {
        this.maxLoginFailures = maxLoginFailures;
    }

    public void setLoginFailureDelay(final int loginFailureDelay) {
        this.loginFailureDelay = loginFailureDelay;
    }
}
