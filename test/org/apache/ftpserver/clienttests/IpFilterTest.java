package org.apache.ftpserver.clienttests;

import java.net.InetAddress;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ipfilter.DefaultIpFilter;
import org.apache.ftpserver.ipfilter.IpFilterType;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.mina.filter.firewall.Subnet;
import org.springframework.context.annotation.FilterType;

public class IpFilterTest extends ClientTestTemplate {

    private DefaultIpFilter filter = new DefaultIpFilter(IpFilterType.DENY);

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ListenerFactory factory = new ListenerFactory(server.getListener("default"));
        factory.setIpFilter(filter);
        server.addListener("default", factory.createListener());
        return server;
    }

    protected boolean isConnectClient() {
        return false;
    }

    public void testDenyBlackList() throws Exception {
        filter.clear();
        filter.setType(IpFilterType.DENY);
        filter.add(new Subnet(InetAddress.getByName("localhost"), 32));
        try {
            client.connect("localhost", getListenerPort());
            fail("Must throw");
        } catch (FTPConnectionClosedException e) {
        }
    }

    public void testDenyEmptyWhiteList() throws Exception {
        filter.clear();
        filter.setType(IpFilterType.ALLOW);
        try {
            client.connect("localhost", getListenerPort());
            fail("Must throw");
        } catch (FTPConnectionClosedException e) {
        }
    }

    public void testWhiteList() throws Exception {
        filter.clear();
        filter.setType(IpFilterType.ALLOW);
        filter.add(new Subnet(InetAddress.getByName("localhost"), 32));
        client.connect("localhost", getListenerPort());
    }
}
