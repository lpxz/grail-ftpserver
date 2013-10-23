package org.apache.ftpserver.listener;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.ipfilter.IpFilter;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.mina.filter.firewall.Subnet;

public interface Listener {

    void start(FtpServerContext serverContext);

    void stop();

    boolean isStopped();

    void suspend();

    void resume();

    boolean isSuspended();

    Set<FtpIoSession> getActiveSessions();

    boolean isImplicitSsl();

    SslConfiguration getSslConfiguration();

    int getPort();

    String getServerAddress();

    DataConnectionConfiguration getDataConnectionConfiguration();

    int getIdleTimeout();

    @Deprecated
    List<InetAddress> getBlockedAddresses();

    List<Subnet> getBlockedSubnets();

    IpFilter getIpFilter();
}
