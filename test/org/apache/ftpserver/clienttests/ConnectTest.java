package org.apache.ftpserver.clienttests;

import org.apache.ftpserver.listener.nio.NioListener;

public class ConnectTest extends ClientTestTemplate {

    @Override
    protected boolean isConnectClient() {
        return false;
    }

    @Override
    protected boolean isStartServer() {
        return false;
    }

    public void testPort() throws Exception {
        assertEquals(0, ((NioListener) server.getListener("default")).getPort());
        server.start();
        assertTrue(((NioListener) server.getListener("default")).getPort() > 0);
    }
}
