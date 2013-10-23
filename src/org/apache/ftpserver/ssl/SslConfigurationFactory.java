package org.apache.ftpserver.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.ssl.impl.DefaultSslConfiguration;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SslConfigurationFactory {

    private final Logger LOG = LoggerFactory.getLogger(SslConfigurationFactory.class);

    private File keystoreFile = new File("./res/.keystore");

    private String keystorePass;

    private String keystoreType = KeyStore.getDefaultType();

    private String keystoreAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

    private File trustStoreFile;

    private String trustStorePass;

    private String trustStoreType = KeyStore.getDefaultType();

    private String trustStoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

    private String sslProtocol = "TLS";

    private ClientAuth clientAuth = ClientAuth.NONE;

    private String keyPass;

    private String keyAlias;

    private String[] enabledCipherSuites;

    public File getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(File keyStoreFile) {
        this.keystoreFile = keyStoreFile;
    }

    public String getKeystorePassword() {
        return keystorePass;
    }

    public void setKeystorePassword(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    public String getKeystoreAlgorithm() {
        return keystoreAlgorithm;
    }

    public void setKeystoreAlgorithm(String keystoreAlgorithm) {
        this.keystoreAlgorithm = keystoreAlgorithm;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public void setClientAuthentication(String clientAuthReqd) {
        if ("true".equalsIgnoreCase(clientAuthReqd) || "yes".equalsIgnoreCase(clientAuthReqd) || "need".equalsIgnoreCase(clientAuthReqd)) {
            this.clientAuth = ClientAuth.NEED;
        } else if ("want".equalsIgnoreCase(clientAuthReqd)) {
            this.clientAuth = ClientAuth.WANT;
        } else {
            this.clientAuth = ClientAuth.NONE;
        }
    }

    public String getKeyPassword() {
        return keyPass;
    }

    public void setKeyPassword(String keyPass) {
        this.keyPass = keyPass;
    }

    public File getTruststoreFile() {
        return trustStoreFile;
    }

    public void setTruststoreFile(File trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    public String getTruststorePassword() {
        return trustStorePass;
    }

    public void setTruststorePassword(String trustStorePass) {
        this.trustStorePass = trustStorePass;
    }

    public String getTruststoreType() {
        return trustStoreType;
    }

    public void setTruststoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getTruststoreAlgorithm() {
        return trustStoreAlgorithm;
    }

    public void setTruststoreAlgorithm(String trustStoreAlgorithm) {
        this.trustStoreAlgorithm = trustStoreAlgorithm;
    }

    private KeyStore loadStore(File storeFile, String storeType, String storePass) throws IOException, GeneralSecurityException {
        InputStream fin = null;
        try {
            if (storeFile.exists()) {
                LOG.debug("Trying to load store from file");
                fin = new FileInputStream(storeFile);
            } else {
                LOG.debug("Trying to load store from classpath");
                fin = getClass().getClassLoader().getResourceAsStream(storeFile.getPath());
                if (fin == null) {
                    throw new FtpServerConfigurationException("Key store could not be loaded from " + storeFile.getPath());
                }
            }
            KeyStore store = KeyStore.getInstance(storeType);
            store.load(fin, storePass.toCharArray());
            return store;
        } finally {
            IoUtils.close(fin);
        }
    }

    public SslConfiguration createSslConfiguration() {
        try {
            LOG.debug("Loading key store from \"{}\", using the key store type \"{}\"", keystoreFile.getAbsolutePath(), keystoreType);
            KeyStore keyStore = loadStore(keystoreFile, keystoreType, keystorePass);
            KeyStore trustStore;
            if (trustStoreFile != null) {
                LOG.debug("Loading trust store from \"{}\", using the key store type \"{}\"", trustStoreFile.getAbsolutePath(), trustStoreType);
                trustStore = loadStore(trustStoreFile, trustStoreType, trustStorePass);
            } else {
                trustStore = keyStore;
            }
            String keyPassToUse;
            if (keyPass == null) {
                keyPassToUse = keystorePass;
            } else {
                keyPassToUse = keyPass;
            }
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keystoreAlgorithm);
            keyManagerFactory.init(keyStore, keyPassToUse.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustStoreAlgorithm);
            trustManagerFactory.init(trustStore);
            return new DefaultSslConfiguration(keyManagerFactory, trustManagerFactory, clientAuth, sslProtocol, enabledCipherSuites, keyAlias);
        } catch (Exception ex) {
            LOG.error("DefaultSsl.configure()", ex);
            throw new FtpServerConfigurationException("DefaultSsl.configure()", ex);
        }
    }

    public ClientAuth getClientAuth() {
        return clientAuth;
    }

    public String[] getEnabledCipherSuites() {
        if (enabledCipherSuites != null) {
            return enabledCipherSuites.clone();
        } else {
            return null;
        }
    }

    public void setEnabledCipherSuites(String[] enabledCipherSuites) {
        if (enabledCipherSuites != null) {
            this.enabledCipherSuites = enabledCipherSuites.clone();
        } else {
            this.enabledCipherSuites = null;
        }
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }
}
