package org.apache.ftpserver.clienttests;

public class RetrievePassiveTest extends RetrieveTest {

    protected void setUp() throws Exception {
        super.setUp();
        client.setRemoteVerificationEnabled(false);
        client.enterLocalPassiveMode();
    }
}
