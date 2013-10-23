package org.apache.ftpserver.config.spring;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.ipfilter.IpFilter;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.mina.filter.firewall.Subnet;

public class MyCustomListener implements Listener {

    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public Set<FtpIoSession> getActiveSessions() {
        return null;
    }

    public DataConnectionConfiguration getDataConnectionConfiguration() {
        return null;
    }

    public int getIdleTimeout() {
        return 0;
    }

    public int getPort() {
        return port;
    }

    public String getServerAddress() {
        return null;
    }

    public SslConfiguration getSslConfiguration() {
        return null;
    }

    public boolean isImplicitSsl() {
        return false;
    }

    public boolean isStopped() {
        return false;
    }

    public boolean isSuspended() {
        return false;
    }

    public void resume() {
    }

    public void start(FtpServerContext serverContext) {
    }

    public void stop() {
    }

    public void suspend() {
    }

    public List<InetAddress> getBlockedAddresses() {
        return null;
    }

    public List<Subnet> getBlockedSubnets() {
        return null;
    }

    public IpFilter getIpFilter() {
        return null;
    }
}
