package org.apache.ftpserver.ssl;

import javax.net.ssl.SSLHandshakeException;
import org.apache.commons.net.ftp.FTPSClient;

public class MinaCipherSuitesTest extends SSLTestTemplate {

    protected String getAuthValue() {
        return "TLS";
    }

    protected boolean useImplicit() {
        return true;
    }

    protected SslConfigurationFactory createSslConfiguration() {
        SslConfigurationFactory sslConfigFactory = super.createSslConfiguration();
        sslConfigFactory.setEnabledCipherSuites(new String[] { "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA" });
        return sslConfigFactory;
    }

    protected FTPSClient createFTPClient() throws Exception {
        return new FTPSClient(true);
    }

    protected boolean isConnectClient() {
        return false;
    }

    public void testEnabled() throws Exception {
        ((FTPSClient) client).setEnabledCipherSuites(new String[] { "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA" });
        connectClient();
    }

    public void testDisabled() throws Exception {
        ((FTPSClient) client).setEnabledCipherSuites(new String[] { "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA" });
        try {
            doConnect();
            fail("Must throw SSLHandshakeException");
        } catch (SSLHandshakeException e) {
        }
    }
}
