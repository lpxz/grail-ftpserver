package org.apache.ftpserver.ssl;

public class MinaImplicitClientAuthTest extends MinaClientAuthTest {

    protected boolean useImplicit() {
        return true;
    }
}
