package org.apache.ftpserver.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.ipfilter.IpFilter;
import org.apache.ftpserver.listener.nio.NioListener;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.mina.filter.firewall.Subnet;

public class ListenerFactory {

    private String serverAddress;

    private int port = 21;

    private SslConfiguration ssl;

    private boolean implicitSsl = false;

    private DataConnectionConfiguration dataConnectionConfig = new DataConnectionConfigurationFactory().createDataConnectionConfiguration();

    private int idleTimeout = 300;

    private List<InetAddress> blockedAddresses;

    private List<Subnet> blockedSubnets;

    private IpFilter ipFilter = null;

    public ListenerFactory() {
    }

    public ListenerFactory(Listener listener) {
        serverAddress = listener.getServerAddress();
        port = listener.getPort();
        ssl = listener.getSslConfiguration();
        implicitSsl = listener.isImplicitSsl();
        dataConnectionConfig = listener.getDataConnectionConfiguration();
        idleTimeout = listener.getIdleTimeout();
        blockedAddresses = listener.getBlockedAddresses();
        blockedSubnets = listener.getBlockedSubnets();
        this.ipFilter = listener.getIpFilter();
    }

    public Listener createListener() {
        try {
            InetAddress.getByName(serverAddress);
        } catch (UnknownHostException e) {
            throw new FtpServerConfigurationException("Unknown host", e);
        }
        if (ipFilter != null) {
            if (blockedAddresses != null || blockedSubnets != null) {
                throw new IllegalStateException("Usage of IPFilter in combination with blockedAddesses/subnets is not supported. ");
            }
        }
        if (blockedAddresses != null || blockedSubnets != null) {
            return new NioListener(serverAddress, port, implicitSsl, ssl, dataConnectionConfig, idleTimeout, blockedAddresses, blockedSubnets);
        } else {
            return new NioListener(serverAddress, port, implicitSsl, ssl, dataConnectionConfig, idleTimeout, ipFilter);
        }
    }

    public boolean isImplicitSsl() {
        return implicitSsl;
    }

    public void setImplicitSsl(boolean implicitSsl) {
        this.implicitSsl = implicitSsl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public SslConfiguration getSslConfiguration() {
        return ssl;
    }

    public void setSslConfiguration(SslConfiguration ssl) {
        this.ssl = ssl;
    }

    public DataConnectionConfiguration getDataConnectionConfiguration() {
        return dataConnectionConfig;
    }

    public void setDataConnectionConfiguration(DataConnectionConfiguration dataConnectionConfig) {
        this.dataConnectionConfig = dataConnectionConfig;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @Deprecated
    public List<InetAddress> getBlockedAddresses() {
        return blockedAddresses;
    }

    @Deprecated
    public void setBlockedAddresses(List<InetAddress> blockedAddresses) {
        this.blockedAddresses = blockedAddresses;
    }

    @Deprecated
    public List<Subnet> getBlockedSubnets() {
        return blockedSubnets;
    }

    @Deprecated
    public void setBlockedSubnets(List<Subnet> blockedSubnets) {
        this.blockedSubnets = blockedSubnets;
    }

    public IpFilter getIpFilter() {
        return ipFilter;
    }

    public void setIpFilter(IpFilter ipFilter) {
        this.ipFilter = ipFilter;
    }
}
