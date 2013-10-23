package org.apache.ftpserver.clienttests;

import java.net.SocketException;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

public class DefaultMaxLoginTest extends ClientTestTemplate {

    private static final String UNKNOWN_USERNAME = "foo";

    private static final String UNKNOWN_PASSWORD = "bar";

    public void testLogin() throws Exception {
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
        try {
            client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD);
            fail("Must be disconnected");
        } catch (FTPConnectionClosedException e) {
        } catch (SocketException e) {
        }
    }
}
