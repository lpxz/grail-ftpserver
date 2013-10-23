package org.apache.ftpserver.impl;

import java.net.BindException;
import junit.framework.TestCase;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.test.TestUtil;

public class DefaultFtpServerTest extends TestCase {

    public void testFailStartingSecondListener() throws Exception {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(TestUtil.findFreePort());
        Listener defaultListener = listenerFactory.createListener();
        Listener secondListener = listenerFactory.createListener();
        serverFactory.addListener("default", defaultListener);
        serverFactory.addListener("second", secondListener);
        FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpServerConfigurationException e) {
            if (e.getCause() instanceof BindException) {
                assertTrue(defaultListener.isStopped());
                assertTrue(secondListener.isStopped());
                assertTrue(server.isStopped());
            } else {
                throw e;
            }
        }
    }
}
