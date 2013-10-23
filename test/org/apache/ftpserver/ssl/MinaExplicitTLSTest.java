package org.apache.ftpserver.ssl;

public class MinaExplicitTLSTest extends ExplicitSecurityTestTemplate {

    protected String getAuthValue() {
        return "TLS";
    }
}
