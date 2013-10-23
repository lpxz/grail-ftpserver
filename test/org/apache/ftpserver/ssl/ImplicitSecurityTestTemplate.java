package org.apache.ftpserver.ssl;

public abstract class ImplicitSecurityTestTemplate extends ExplicitSecurityTestTemplate {

    protected boolean useImplicit() {
        return true;
    }
}
