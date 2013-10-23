package org.apache.ftpserver.clienttests;

public class StorePassiveTest extends StoreTest {

    protected void setUp() throws Exception {
        super.setUp();
        client.setRemoteVerificationEnabled(false);
        client.enterLocalPassiveMode();
    }
}
