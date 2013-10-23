package org.apache.ftpserver.clienttests;

import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.test.TestUtil;

public class PasvTest extends ClientTestTemplate {

    protected boolean isConnectClient() {
        return false;
    }

    @Override
    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        ListenerFactory listenerFactory = new ListenerFactory(server.getListener("default"));
        DataConnectionConfigurationFactory dccFactory = new DataConnectionConfigurationFactory();
        int passivePort = TestUtil.findFreePort(12444);
        dccFactory.setPassivePorts(passivePort + "-" + passivePort);
        listenerFactory.setDataConnectionConfiguration(dccFactory.createDataConnectionConfiguration());
        server.addListener("default", listenerFactory.createListener());
        return server;
    }

    public void testMultiplePasv() throws Exception {
        for (int i = 0; i < 5; i++) {
            client.connect("localhost", getListenerPort());
            client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
            client.pasv();
            client.quit();
            client.disconnect();
        }
    }

    public void testPasvIp() throws Exception {
        String[] ips = TestUtil.getHostAddresses();
        for (int i = 0; i < ips.length; i++) {
            String ip = ips[i];
            String ftpIp = ip.replace('.', ',');
            if (!ip.startsWith("0.")) {
                try {
                    client.connect(ip, getListenerPort());
                } catch (FTPConnectionClosedException e) {
                    Thread.sleep(200);
                    client.connect(ip, getListenerPort());
                }
                client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                client.pasv();
                assertTrue(client.getReplyString().indexOf(ftpIp) > -1);
                client.quit();
                client.disconnect();
            }
        }
    }
}
