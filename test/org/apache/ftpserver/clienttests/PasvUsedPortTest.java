package org.apache.ftpserver.clienttests;

import java.net.ServerSocket;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.test.TestUtil;

public class PasvUsedPortTest extends ClientTestTemplate {

    private int passivePort;

    @Override
    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ListenerFactory listenerFactory = new ListenerFactory(server.getListener("default"));
        DataConnectionConfigurationFactory dccFactory = new DataConnectionConfigurationFactory();
        passivePort = TestUtil.findFreePort(12444);
        dccFactory.setPassivePorts(passivePort + "-" + (passivePort + 1));
        listenerFactory.setDataConnectionConfiguration(dccFactory.createDataConnectionConfiguration());
        server.addListener("default", listenerFactory.createListener());
        return server;
    }

    public void testPasvWithUsedPort() throws Exception {
        ServerSocket ss = new ServerSocket(passivePort);
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.pasv();
        assertEquals("227 Entering Passive Mode (127,0,0,1,48,157)", client.getReplyString().trim());
        client.quit();
        client.disconnect();
        ss.close();
    }
}
