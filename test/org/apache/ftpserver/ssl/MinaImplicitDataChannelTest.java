package org.apache.ftpserver.ssl;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPSSocketFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;

public class MinaImplicitDataChannelTest extends ImplicitSecurityTestTemplate {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected String getAuthValue() {
        return "SSL";
    }

    protected DataConnectionConfigurationFactory createDataConnectionConfigurationFactory() {
        DataConnectionConfigurationFactory result = super.createDataConnectionConfigurationFactory();
        result.setImplicitSsl(true);
        return result;
    }

    protected boolean useImplicit() {
        return true;
    }

    public void testThatDataChannelIsSecure() {
        assertTrue(getActiveSession().getDataConnection().isSecure());
    }

    public void testStoreWithoutProtPInActiveMode() throws Exception {
        secureClientDataConnection();
        assertTrue(getActiveSession().getDataConnection().isSecure());
        client.storeFile(TEST_FILE1.getName(), new ByteArrayInputStream(TEST_DATA));
        assertTrue(TEST_FILE1.exists());
        assertEquals(TEST_DATA.length, TEST_FILE1.length());
    }

    public void testStoreWithProtPInPassiveMode() throws Exception {
        secureClientDataConnection();
        client.enterLocalPassiveMode();
        assertTrue(getActiveSession().getDataConnection().isSecure());
        client.storeFile(TEST_FILE1.getName(), new ByteArrayInputStream(TEST_DATA));
        assertTrue(TEST_FILE1.exists());
        assertEquals(TEST_DATA.length, TEST_FILE1.length());
    }

    private void secureClientDataConnection() throws NoSuchAlgorithmException, KeyManagementException {
        FTPSClient sclient = (FTPSClient) client;
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(new KeyManager[] { clientKeyManager }, new TrustManager[] { clientTrustManager }, null);
        sclient.setSocketFactory(new FTPSSocketFactory(context));
        SSLServerSocketFactory ssf = context.getServerSocketFactory();
        sclient.setServerSocketFactory(ssf);
    }
}
