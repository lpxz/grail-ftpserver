package org.apache.ftpserver.clienttests;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.test.TestUtil;
import org.apache.ftpserver.util.SocketAddressEncoder;

public class PasvAddressTest extends ClientTestTemplate {

    private String passiveAddress;

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ListenerFactory listenerFactory = new ListenerFactory(server.getListener("default"));
        DataConnectionConfigurationFactory dccFactory = new DataConnectionConfigurationFactory();
        passiveAddress = TestUtil.findNonLocalhostIp().getHostAddress();
        dccFactory.setPassiveAddress(passiveAddress);
        dccFactory.setPassivePorts("12347");
        DataConnectionConfiguration dcc = dccFactory.createDataConnectionConfiguration();
        listenerFactory.setDataConnectionConfiguration(dcc);
        server.addListener("default", listenerFactory.createListener());
        return server;
    }

    public void testPasvAddress() throws Exception {
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.pasv();
        String reply = client.getReplyString();
        String ipEncoded = SocketAddressEncoder.encode(new InetSocketAddress(InetAddress.getByName(passiveAddress), 12347));
        assertTrue("The PASV address should contain \"" + ipEncoded + "\" but was \"" + reply + "\"", reply.indexOf(ipEncoded) > -1);
    }
}
