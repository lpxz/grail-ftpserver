package org.apache.ftpserver.clienttests;

import java.io.IOException;

public class SuspendResumeTest extends ClientTestTemplate {

    @Override
    protected boolean isConnectClient() {
        return false;
    }

    public void testSuspendResumeServer() throws Exception {
        client.connect("localhost", getListenerPort());
        client.disconnect();
        server.suspend();
        try {
            client.connect("localhost", getListenerPort());
            fail("Must throw IOException");
        } catch (IOException e) {
        } finally {
            client.disconnect();
        }
        server.resume();
        client.connect("localhost", getListenerPort());
        client.disconnect();
    }

    public void testSuspendResumeListener() throws Exception {
        client.connect("localhost", getListenerPort());
        client.disconnect();
        server.getListener("default").suspend();
        try {
            client.connect("localhost", getListenerPort());
            fail("Must throw IOException");
        } catch (IOException e) {
        } finally {
            client.disconnect();
        }
        server.getListener("default").resume();
        client.connect("localhost", getListenerPort());
        client.disconnect();
    }
}
