package org.apache.ftpserver.clienttests;

import java.io.File;
import java.io.OutputStream;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;

public class DataTransferTimeoutTest extends ClientTestTemplate {

    private static final String TEST_FILENAME = "test.txt";

    private static final File TEST_FILE = new File(ROOT_DIR, TEST_FILENAME);

    @Override
    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory serverFactory = super.createServer();
        ListenerFactory listenerFactory = new ListenerFactory(serverFactory.getListener("default"));
        listenerFactory.setIdleTimeout(1);
        serverFactory.addListener("default", listenerFactory.createListener());
        return serverFactory;
    }

    protected void setUp() throws Exception {
        super.setUp();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testTimeoutForStore() throws Exception {
        OutputStream os = client.storeFileStream(TEST_FILENAME);
        os.write(1);
        for (int i = 0; i < 100; i++) {
            Thread.sleep(20);
            os.write(1);
            os.flush();
        }
        os.close();
        client.completePendingCommand();
        client.noop();
    }
}
