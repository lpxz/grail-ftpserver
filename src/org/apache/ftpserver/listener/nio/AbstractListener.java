package org.apache.ftpserver.listener.nio;

import java.net.InetAddress;
import java.util.List;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.ipfilter.DefaultIpFilter;
import org.apache.ftpserver.ipfilter.IpFilter;
import org.apache.ftpserver.ipfilter.IpFilterType;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.mina.filter.firewall.Subnet;

public abstract class AbstractListener implements Listener {

    private String serverAddress;

    private int port = 21;

    private SslConfiguration ssl;

    private boolean implicitSsl = false;

    private int idleTimeout;

    private List<InetAddress> blockedAddresses;

    private List<Subnet> blockedSubnets;

    private IpFilter ipFilter = null;

    private DataConnectionConfiguration dataConnectionConfig;

    @Deprecated
    public AbstractListener(String serverAddress, int port, boolean implicitSsl, SslConfiguration sslConfiguration, DataConnectionConfiguration dataConnectionConfig, int idleTimeout, List<InetAddress> blockedAddresses, List<Subnet> blockedSubnets) {
        this(serverAddress, port, implicitSsl, sslConfiguration, dataConnectionConfig, idleTimeout, createBlackListFilter(blockedAddresses, blockedSubnets));
        this.blockedAddresses = blockedAddresses;
        this.blockedSubnets = blockedSubnets;
    }

    public AbstractListener(String serverAddress, int port, boolean implicitSsl, SslConfiguration sslConfiguration, DataConnectionConfiguration dataConnectionConfig, int idleTimeout, IpFilter ipFilter) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.implicitSsl = implicitSsl;
        this.dataConnectionConfig = dataConnectionConfig;
        this.ssl = sslConfiguration;
        this.idleTimeout = idleTimeout;
        this.ipFilter = ipFilter;
    }

    private static IpFilter createBlackListFilter(List<InetAddress> blockedAddresses, List<Subnet> blockedSubnets) {
        if (blockedAddresses == null && blockedSubnets == null) {
            return null;
        }
        DefaultIpFilter ipFilter = new DefaultIpFilter(IpFilterType.DENY);
        if (blockedSubnets != null) {
            ipFilter.addAll(blockedSubnets);
        }
        if (blockedAddresses != null) {
            for (InetAddress address : blockedAddresses) {
                ipFilter.add(new Subnet(address, 32));
            }
        }
        return ipFilter;
    }

    public boolean isImplicitSsl() {
        return implicitSsl;
    }

    public int getPort() {
        return port;
    }

    protected void setPort(int port) {
        this.port = port;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public SslConfiguration getSslConfiguration() {
        return ssl;
    }

    public DataConnectionConfiguration getDataConnectionConfiguration() {
        return dataConnectionConfig;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public List<InetAddress> getBlockedAddresses() {
        return blockedAddresses;
    }

    public List<Subnet> getBlockedSubnets() {
        return blockedSubnets;
    }

    public IpFilter getIpFilter() {
        return ipFilter;
    }
}
