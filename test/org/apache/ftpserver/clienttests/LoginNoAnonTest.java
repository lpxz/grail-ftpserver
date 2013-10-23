package org.apache.ftpserver.clienttests;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServerFactory;

public class LoginNoAnonTest extends ClientTestTemplate {

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ConnectionConfigFactory ccFactory = new ConnectionConfigFactory();
        ccFactory.setAnonymousLoginEnabled(false);
        server.setConnectionConfig(ccFactory.createConnectionConfig());
        return server;
    }

    public void testLoginWithAnon() throws Exception {
        assertFalse(client.login(ANONYMOUS_USERNAME, ANONYMOUS_PASSWORD));
    }
}
