package org.apache.ftpserver.clienttests;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.ftpserver.DataConnectionConfigurationFactory;

public class BindExceptionSerialTest extends ClientTestTemplate {

    @Override
    protected FTPClient createFTPClient() throws Exception {
        FTPClient c = super.createFTPClient();
        c.setDataTimeout(1000);
        return c;
    }

    @Override
    protected DataConnectionConfigurationFactory createDataConnectionConfigurationFactory() {
        DataConnectionConfigurationFactory factory = super.createDataConnectionConfigurationFactory();
        factory.setActiveLocalPort(2020);
        factory.setActiveLocalAddress("localhost");
        return factory;
    }

    @Override
    public void connectClient() throws Exception {
        super.connectClient();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testSerialExecution() throws Exception {
        try {
            System.out.println("-- call one");
            System.out.println(Arrays.asList(client.listFiles()));
            System.out.println("-- call two");
            System.out.println(Arrays.asList(client.listFiles()));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
