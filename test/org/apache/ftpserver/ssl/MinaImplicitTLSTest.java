package org.apache.ftpserver.ssl;

public class MinaImplicitTLSTest extends ImplicitSecurityTestTemplate {

    protected String getAuthValue() {
        return "TLS";
    }
}
