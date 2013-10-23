package org.apache.ftpserver.clienttests;

import java.net.SocketException;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServerFactory;

public class CustomMaxLoginTest extends ClientTestTemplate {

    private static final String UNKNOWN_USERNAME = "foo";

    private static final String UNKNOWN_PASSWORD = "bar";

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ConnectionConfigFactory ccFactory = new ConnectionConfigFactory();
        ccFactory.setMaxLoginFailures(2);
        server.setConnectionConfig(ccFactory.createConnectionConfig());
        return server;
    }

    public void testLogin() throws Exception {
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        try {
            client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD);
            fail("Must be disconnected");
        } catch (FTPConnectionClosedException e) {
        } catch (SocketException e) {
        }
    }
}
