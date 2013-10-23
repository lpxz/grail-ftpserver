package org.apache.ftpserver.clienttests;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;

public class PasvAddressWithHostnameTest extends ClientTestTemplate {

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ListenerFactory listenerFactory = new ListenerFactory(server.getListener("default"));
        DataConnectionConfigurationFactory dccFactory = new DataConnectionConfigurationFactory();
        dccFactory.setPassiveExternalAddress("127.0.0.1");
        listenerFactory.setDataConnectionConfiguration(dccFactory.createDataConnectionConfiguration());
        server.addListener("default", listenerFactory.createListener());
        return server;
    }

    public void testPasvAddress() throws Exception {
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.pasv();
        assertTrue(client.getReplyString().indexOf("(127,0,0,1,") > -1);
    }
}
