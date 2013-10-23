package org.apache.ftpserver.ssl.impl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

public final class ExtendedAliasKeyManager extends X509ExtendedKeyManager {

    private X509ExtendedKeyManager delegate;

    private String serverKeyAlias;

    public ExtendedAliasKeyManager(KeyManager mgr, String keyAlias) {
        this.delegate = (X509ExtendedKeyManager) mgr;
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

    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return delegate.chooseEngineClientAlias(keyType, issuers, engine);
    }

    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
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
            return delegate.chooseEngineServerAlias(keyType, issuers, engine);
        }
    }
}
