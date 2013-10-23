package org.apache.ftpserver.clienttests;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.test.TestUtil;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.util.IoUtils;

public class ClientTestTemplate extends TestCase {

    public static final String ADMIN_PASSWORD = "admin";

    public static final String ADMIN_USERNAME = "admin";

    protected static final String ANONYMOUS_PASSWORD = "foo@bar.com";

    protected static final String ANONYMOUS_USERNAME = "anonymous";

    protected static final String TESTUSER2_USERNAME = "testuser2";

    protected static final String TESTUSER1_USERNAME = "testuser1";

    protected static final String TESTUSER_PASSWORD = "password";

    public DefaultFtpServer server;

    public int serverport = 1999;

    protected FTPClient client;

    private static final File USERS_FILE = new File(TestUtil.getBaseDir(), "src/test/resources/users.properties");

    private static final File TEST_TMP_DIR = new File("test-tmp");

    protected static final File ROOT_DIR = new File(TEST_TMP_DIR, "ftproot");

    protected FtpServerFactory createServer() throws Exception {
        assertTrue(USERS_FILE.getAbsolutePath() + " must exist", USERS_FILE.exists());
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setConnectionConfig(createConnectionConfigFactory().createConnectionConfig());
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(serverport);
        listenerFactory.setDataConnectionConfiguration(createDataConnectionConfigurationFactory().createDataConnectionConfiguration());
        serverFactory.addListener("default", listenerFactory.createListener());
        PropertiesUserManagerFactory umFactory = new PropertiesUserManagerFactory();
        umFactory.setAdminName("admin");
        umFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        umFactory.setFile(USERS_FILE);
        serverFactory.setUserManager(umFactory.createUserManager());
        return serverFactory;
    }

    protected ConnectionConfigFactory createConnectionConfigFactory() {
        return new ConnectionConfigFactory();
    }

    protected DataConnectionConfigurationFactory createDataConnectionConfigurationFactory() {
        return new DataConnectionConfigurationFactory();
    }

    protected void setUp() throws Exception {
        initDirs();
        initServer();
        connectClient();
    }

    public void initDirs() throws IOException {
        cleanTmpDirs();
        TEST_TMP_DIR.mkdirs();
        ROOT_DIR.mkdirs();
    }

    public void initServer() throws IOException, Exception {
        server = (DefaultFtpServer) createServer().createServer();
        if (isStartServer()) {
            server.start();
        }
    }

    protected int getListenerPort() {
        return server.getListener("default").getPort();
    }

    protected boolean isStartServer() {
        return true;
    }

    protected FTPClient createFTPClient() throws Exception {
        FTPClient client = new FTPClient();
        client.setDefaultTimeout(10000);
        return client;
    }

    public void connectClient() throws Exception {
        client = createFTPClient();
        client.addProtocolCommandListener(new ProtocolCommandListener() {

            public void protocolCommandSent(ProtocolCommandEvent event) {
            }

            public void protocolReplyReceived(ProtocolCommandEvent event) {
            }
        });
        if (isConnectClient()) {
            doConnect();
        }
    }

    protected void doConnect() throws Exception {
        try {
            client.connect("localhost", getListenerPort());
        } catch (FTPConnectionClosedException e) {
            Thread.sleep(200);
            client.connect("localhost", getListenerPort());
        }
    }

    protected boolean isConnectClient() {
        return true;
    }

    protected void cleanTmpDirs() throws IOException {
        if (TEST_TMP_DIR.exists()) {
            IoUtils.delete(TEST_TMP_DIR);
        }
    }

    protected FtpIoSession getActiveSession() {
        return server.getListener("default").getActiveSessions().iterator().next();
    }

    protected void tearDown() throws Exception {
        if (isConnectClient()) {
            try {
                client.quit();
            } catch (Exception e) {
            }
        }
        if (server != null) {
            try {
                server.stop();
            } catch (NullPointerException e) {
            }
        }
        cleanTmpDirs();
    }
}
