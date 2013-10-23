package org.apache.ftpserver;

public interface ConnectionConfig {

    int getMaxLoginFailures();

    int getLoginFailureDelay();

    int getMaxAnonymousLogins();

    int getMaxLogins();

    boolean isAnonymousLoginEnabled();

    int getMaxThreads();
}
