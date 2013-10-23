package org.apache.ftpserver.impl;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.ssl.SslConfiguration;

public class DefaultDataConnectionConfiguration implements DataConnectionConfiguration {

    private int idleTime;

    private SslConfiguration ssl;

    private boolean activeEnabled;

    private String activeLocalAddress;

    private int activeLocalPort;

    private boolean activeIpCheck;

    private String passiveAddress;

    private String passiveExternalAddress;

    private PassivePorts passivePorts;

    private final boolean implicitSsl;

    public DefaultDataConnectionConfiguration(int idleTime, SslConfiguration ssl, boolean activeEnabled, boolean activeIpCheck, String activeLocalAddress, int activeLocalPort, String passiveAddress, PassivePorts passivePorts, String passiveExternalAddress, boolean implicitSsl) {
        this.idleTime = idleTime;
        this.ssl = ssl;
        this.activeEnabled = activeEnabled;
        this.activeIpCheck = activeIpCheck;
        this.activeLocalAddress = activeLocalAddress;
        this.activeLocalPort = activeLocalPort;
        this.passiveAddress = passiveAddress;
        this.passivePorts = passivePorts;
        this.passiveExternalAddress = passiveExternalAddress;
        this.implicitSsl = implicitSsl;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public boolean isActiveEnabled() {
        return activeEnabled;
    }

    public boolean isActiveIpCheck() {
        return activeIpCheck;
    }

    public String getActiveLocalAddress() {
        return activeLocalAddress;
    }

    public int getActiveLocalPort() {
        return activeLocalPort;
    }

    public String getPassiveAddress() {
        return passiveAddress;
    }

    public String getPassiveExernalAddress() {
        return passiveExternalAddress;
    }

    public synchronized int requestPassivePort() {
        return passivePorts.reserveNextPort();
    }

    public String getPassivePorts() {
        return passivePorts.toString();
    }

    public synchronized void releasePassivePort(final int port) {
        passivePorts.releasePort(port);
    }

    public SslConfiguration getSslConfiguration() {
        return ssl;
    }

    public boolean isImplicitSsl() {
        return implicitSsl;
    }
}
