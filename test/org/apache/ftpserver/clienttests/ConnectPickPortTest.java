package org.apache.ftpserver.clienttests;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.listener.nio.NioListener;

public class ConnectPickPortTest extends ClientTestTemplate {

    @Override
    protected boolean isConnectClient() {
        return false;
    }

    @Override
    protected boolean isStartServer() {
        return false;
    }

    @Override
    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(0);
        server.addListener("default", factory.createListener());
        return server;
    }

    public void testPortWithZeroPort() throws Exception {
        assertEquals(0, ((NioListener) server.getServerContext().getListener("default")).getPort());
        server.start();
        assertTrue(((NioListener) server.getServerContext().getListener("default")).getPort() > 0);
    }
}
