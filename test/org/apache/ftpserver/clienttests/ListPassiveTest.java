package org.apache.ftpserver.clienttests;

public class ListPassiveTest extends ListTest {

    protected void setUp() throws Exception {
        super.setUp();
        client.setRemoteVerificationEnabled(false);
        client.enterLocalPassiveMode();
    }
}
