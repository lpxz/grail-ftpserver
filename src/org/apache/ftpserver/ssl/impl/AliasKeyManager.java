package org.apache.ftpserver.ssl.impl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;

public final class AliasKeyManager implements X509KeyManager {

    private X509KeyManager delegate;

    private String serverKeyAlias;

    public AliasKeyManager(KeyManager mgr, String keyAlias) {
        this.delegate = (X509KeyManager) mgr;
        this.serverKeyAlias = keyAlias;
    }

    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return delegate.chooseClientAlias(keyType, issuers, socket);
    }

    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        if (serverKeyAlias != null) {
            PrivateKey key = delegate.getPrivateKey(serverKeyAlias);
            if (key != null) {
                if (key.getAlgorithm().equals(keyType)) {
                    return serverKeyAlias;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return delegate.chooseServerAlias(keyType, issuers, socket);
        }
    }

    public X509Certificate[] getCertificateChain(String alias) {
        return delegate.getCertificateChain(alias);
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return delegate.getClientAliases(keyType, issuers);
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return delegate.getServerAliases(keyType, issuers);
    }

    public PrivateKey getPrivateKey(String alias) {
        return delegate.getPrivateKey(alias);
    }
}
