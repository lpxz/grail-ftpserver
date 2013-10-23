package org.apache.ftpserver.ssl;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.ftpserver.impl.FtpIoSession;

public class MinaClientAuthTest extends SSLTestTemplate {

    protected FTPSClient createFTPClient() throws Exception {
        FTPSClient client = new FTPSClient(useImplicit());
        client.setNeedClientAuth(true);
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream(FTPCLIENT_KEYSTORE);
        ks.load(fis, KEYSTORE_PASSWORD.toCharArray());
        fis.close();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, KEYSTORE_PASSWORD.toCharArray());
        client.setKeyManager(kmf.getKeyManagers()[0]);
        return client;
    }

    protected String getAuthValue() {
        return "TLS";
    }

    protected String getClientAuth() {
        return "true";
    }

    public void testCommandChannel() throws Exception {
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        assertTrue(FTPReply.isPositiveCompletion(client.noop()));
    }

    public void testClientCertificates() throws Exception {
        FtpIoSession session = server.getListener("default").getActiveSessions().iterator().next();
        assertEquals(1, session.getClientCertificates().length);
        X509Certificate cert = (X509Certificate) session.getClientCertificates()[0];
        assertTrue(cert.getSubjectDN().toString().contains("FtpClient"));
    }
}
