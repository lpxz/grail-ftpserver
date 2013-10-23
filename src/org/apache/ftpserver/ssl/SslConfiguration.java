package org.apache.ftpserver.ssl;

import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;

public interface SslConfiguration {

    SSLContext getSSLContext() throws GeneralSecurityException;

    SSLContext getSSLContext(String protocol) throws GeneralSecurityException;

    String[] getEnabledCipherSuites();

    ClientAuth getClientAuth();
}
