package org.apache.ftpserver.clienttests;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.mina.filter.firewall.Subnet;

public class SubnetBlacklistTest extends ClientTestTemplate {

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ListenerFactory factory = new ListenerFactory(server.getListener("default"));
        List<Subnet> blockedSubnets = new ArrayList<Subnet>();
        blockedSubnets.add(new Subnet(InetAddress.getByName("localhost"), 32));
        factory.setBlockedSubnets(blockedSubnets);
        server.addListener("default", factory.createListener());
        return server;
    }

    protected boolean isConnectClient() {
        return false;
    }

    public void testConnect() throws Exception {
        try {
            client.connect("localhost", getListenerPort());
            fail("Must throw");
        } catch (FTPConnectionClosedException e) {
        }
    }
}
