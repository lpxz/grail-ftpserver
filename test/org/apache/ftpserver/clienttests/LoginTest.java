package org.apache.ftpserver.clienttests;

import java.io.ByteArrayInputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.ftpserver.ftplet.FtpStatistics;

public class LoginTest extends ClientTestTemplate {

    private static final String UNKNOWN_USERNAME = "foo";

    private static final String UNKNOWN_PASSWORD = "bar";

    public void testLogin() throws Exception {
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
    }

    public void testCommandWithoutLogin() throws Exception {
        assertFalse(client.storeFile("foo", new ByteArrayInputStream("foo".getBytes())));
    }

    public void testLoginNoUser() throws Exception {
        assertFalse(client.login(null, null));
    }

    public void testLoginDisabledUser() throws Exception {
        assertFalse(client.login("testuser4", "password"));
    }

    public void testLoginWithAccount() throws Exception {
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        assertTrue(FTPReply.isPositiveCompletion(client.acct("FOO")));
    }

    public void testLoginWithEmptyPassword() throws Exception {
        assertTrue(FTPReply.isPositiveIntermediate(client.user(ADMIN_USERNAME)));
        assertEquals(530, client.sendCommand("PASS"));
    }

    public void testLoginWithEmptyCorrectPassword() throws Exception {
        assertTrue(FTPReply.isPositiveIntermediate(client.user("testuser3")));
        assertTrue(FTPReply.isPositiveCompletion(client.sendCommand("PASS")));
    }

    public void testLoginIncorrectPassword() throws Exception {
        assertFalse(client.login(ADMIN_USERNAME, UNKNOWN_PASSWORD));
    }

    public void testReLogin() throws Exception {
        assertFalse(client.login(ADMIN_USERNAME, UNKNOWN_PASSWORD));
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
    }

    public void testDoubleLoginSameUser() throws Exception {
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
    }

    public void testDoubleLoginDifferentUser() throws Exception {
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        assertFalse("Login with different user not allowed", client.login(TESTUSER1_USERNAME, TESTUSER_PASSWORD));
    }

    public void testREIN() throws Exception {
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        assertTrue(FTPReply.isPositiveCompletion(client.rein()));
        assertTrue(client.login(TESTUSER1_USERNAME, TESTUSER_PASSWORD));
    }

    public void testReLoginWithOnlyPass() throws Exception {
        assertFalse(client.login(ADMIN_USERNAME, UNKNOWN_PASSWORD));
        int reply = client.pass(ADMIN_PASSWORD);
        assertTrue(FTPReply.isNegativePermanent(reply));
    }

    public void testOnlyPass() throws Exception {
        int reply = client.pass(ADMIN_PASSWORD);
        assertTrue(FTPReply.isNegativePermanent(reply));
    }

    public void testLoginThenPass() throws Exception {
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        int reply = client.pass(ADMIN_PASSWORD);
        assertTrue(FTPReply.isPositiveCompletion(reply));
    }

    public void testLoginAnon() throws Exception {
        assertTrue(client.login(ANONYMOUS_USERNAME, ANONYMOUS_PASSWORD));
    }

    public void testLoginUnknownUser() throws Exception {
        assertFalse(client.login(UNKNOWN_USERNAME, UNKNOWN_PASSWORD));
    }

    public void testLoginCount() throws Exception {
        FtpStatistics stats = server.getServerContext().getFtpStatistics();
        assertTrue(client.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        int n = stats.getCurrentLoginNumber();
        assertEquals(1, n);
        client.rein();
        client.logout();
        assertEquals(0, stats.getCurrentLoginNumber());
    }

    public void testLoginWithMaxConnections() throws Exception {
        FTPClient client1 = new FTPClient();
        FTPClient client2 = new FTPClient();
        FTPClient client3 = new FTPClient();
        FTPClient client4 = new FTPClient();
        try {
            client1.connect("localhost", getListenerPort());
            client2.connect("localhost", getListenerPort());
            client3.connect("localhost", getListenerPort());
            client4.connect("localhost", getListenerPort());
            assertTrue(client1.login(TESTUSER1_USERNAME, TESTUSER_PASSWORD));
            assertTrue(client2.login(TESTUSER1_USERNAME, TESTUSER_PASSWORD));
            assertTrue(client3.login(TESTUSER1_USERNAME, TESTUSER_PASSWORD));
            try {
                assertTrue(client4.login(TESTUSER1_USERNAME, TESTUSER_PASSWORD));
                assertEquals(421, client.getReplyCode());
                fail("Must throw FTPConnectionClosedException");
            } catch (FTPConnectionClosedException e) {
            }
        } finally {
            closeQuitely(client1);
            closeQuitely(client2);
            closeQuitely(client3);
            closeQuitely(client4);
        }
    }

    private void closeQuitely(FTPClient client) {
        try {
            client.logout();
        } catch (Exception e) {
        }
        try {
            client.disconnect();
        } catch (Exception e) {
        }
    }
}
