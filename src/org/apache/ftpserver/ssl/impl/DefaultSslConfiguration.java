package org.apache.ftpserver.ssl.impl;

import java.security.GeneralSecurityException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import org.apache.ftpserver.ssl.ClientAuth;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.util.ClassUtils;

public class DefaultSslConfiguration implements SslConfiguration {

    private KeyManagerFactory keyManagerFactory;

    private TrustManagerFactory trustManagerFactory;

    private String sslProtocol = "TLS";

    private ClientAuth clientAuth = ClientAuth.NONE;

    private String keyAlias;

    private String[] enabledCipherSuites;

    public DefaultSslConfiguration(KeyManagerFactory keyManagerFactory, TrustManagerFactory trustManagerFactory, ClientAuth clientAuthReqd, String sslProtocol, String[] enabledCipherSuites, String keyAlias) {
        super();
        this.clientAuth = clientAuthReqd;
        this.enabledCipherSuites = enabledCipherSuites;
        this.keyAlias = keyAlias;
        this.keyManagerFactory = keyManagerFactory;
        this.sslProtocol = sslProtocol;
        this.trustManagerFactory = trustManagerFactory;
    }

    public SSLContext getSSLContext(String protocol) throws GeneralSecurityException {
        if (protocol == null) {
            protocol = sslProtocol;
        }
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        for (int i = 0; i < keyManagers.length; i++) {
            if (ClassUtils.extendsClass(keyManagers[i].getClass(), "javax.net.ssl.X509ExtendedKeyManager")) {
                keyManagers[i] = new ExtendedAliasKeyManager(keyManagers[i], keyAlias);
            } else if (keyManagers[i] instanceof X509KeyManager) {
                keyManagers[i] = new AliasKeyManager(keyManagers[i], keyAlias);
            }
        }
        SSLContext ctx = SSLContext.getInstance(protocol);
        ctx.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
        return ctx;
    }

    public ClientAuth getClientAuth() {
        return clientAuth;
    }

    public SSLContext getSSLContext() throws GeneralSecurityException {
        return getSSLContext(sslProtocol);
    }

    public String[] getEnabledCipherSuites() {
        if (enabledCipherSuites != null) {
            return enabledCipherSuites.clone();
        } else {
            return null;
        }
    }
}
