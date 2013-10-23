package org.apache.ftpserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import org.apache.ftpserver.impl.DefaultDataConnectionConfiguration;
import org.apache.ftpserver.impl.PassivePorts;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataConnectionConfigurationFactory {

    private Logger log = LoggerFactory.getLogger(DataConnectionConfigurationFactory.class);

    private int idleTime = 300;

    private SslConfiguration ssl;

    private boolean activeEnabled = true;

    private String activeLocalAddress;

    private int activeLocalPort = 0;

    private boolean activeIpCheck = false;

    private String passiveAddress;

    private String passiveExternalAddress;

    private PassivePorts passivePorts = new PassivePorts(Collections.<Integer>emptySet(), true);

    private boolean implicitSsl;

    public DataConnectionConfiguration createDataConnectionConfiguration() {
        checkValidAddresses();
        return new DefaultDataConnectionConfiguration(idleTime, ssl, activeEnabled, activeIpCheck, activeLocalAddress, activeLocalPort, passiveAddress, passivePorts, passiveExternalAddress, implicitSsl);
    }

    private void checkValidAddresses() {
        try {
            InetAddress.getByName(passiveAddress);
            InetAddress.getByName(passiveExternalAddress);
        } catch (UnknownHostException ex) {
            throw new FtpServerConfigurationException("Unknown host", ex);
        }
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public boolean isActiveEnabled() {
        return activeEnabled;
    }

    public void setActiveEnabled(boolean activeEnabled) {
        this.activeEnabled = activeEnabled;
    }

    public boolean isActiveIpCheck() {
        return activeIpCheck;
    }

    public void setActiveIpCheck(boolean activeIpCheck) {
        this.activeIpCheck = activeIpCheck;
    }

    public String getActiveLocalAddress() {
        return activeLocalAddress;
    }

    public void setActiveLocalAddress(String activeLocalAddress) {
        this.activeLocalAddress = activeLocalAddress;
    }

    public int getActiveLocalPort() {
        return activeLocalPort;
    }

    public void setActiveLocalPort(int activeLocalPort) {
        this.activeLocalPort = activeLocalPort;
    }

    public String getPassiveAddress() {
        return passiveAddress;
    }

    public void setPassiveAddress(String passiveAddress) {
        this.passiveAddress = passiveAddress;
    }

    public String getPassiveExternalAddress() {
        return passiveExternalAddress;
    }

    public void setPassiveExternalAddress(String passiveExternalAddress) {
        this.passiveExternalAddress = passiveExternalAddress;
    }

    public synchronized int requestPassivePort() {
        int dataPort = -1;
        int loopTimes = 2;
        Thread currThread = Thread.currentThread();
        while ((dataPort == -1) && (--loopTimes >= 0) && (!currThread.isInterrupted())) {
            dataPort = passivePorts.reserveNextPort();
            if (dataPort == -1) {
                try {
                    log.info("Out of passive ports, waiting for one to be released. Might be stuck");
                    wait();
                } catch (InterruptedException ex) {
                }
            }
        }
        return dataPort;
    }

    public String getPassivePorts() {
        return passivePorts.toString();
    }

    public void setPassivePorts(String passivePorts) {
        this.passivePorts = new PassivePorts(passivePorts, true);
    }

    public synchronized void releasePassivePort(final int port) {
        passivePorts.releasePort(port);
        notify();
    }

    public SslConfiguration getSslConfiguration() {
        return ssl;
    }

    public void setSslConfiguration(SslConfiguration ssl) {
        this.ssl = ssl;
    }

    public boolean isImplicitSsl() {
        return implicitSsl;
    }

    public void setImplicitSsl(boolean implicitSsl) {
        this.implicitSsl = implicitSsl;
    }
}
