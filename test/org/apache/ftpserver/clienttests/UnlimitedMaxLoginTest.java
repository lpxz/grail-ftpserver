package org.apache.ftpserver.clienttests;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServerFactory;

public class UnlimitedMaxLoginTest extends ClientTestTemplate {

    private static final String UNKNOWN_USERNAME = "foo";

    private static final String UNKNOWN_PASSWORD = "bar";

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ConnectionConfigFactory ccFactory = new ConnectionConfigFactory();
        ccFactory.setMaxLoginFailures(0);
        server.setConnectionConfig(ccFactory.createConnectionConfig());
        return server;
    }

    public void testLogin() throws Exception {
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
    }
}
