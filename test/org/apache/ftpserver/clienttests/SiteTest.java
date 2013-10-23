package org.apache.ftpserver.clienttests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class SiteTest extends ClientTestTemplate {

    private static final String TEST_FILENAME = "test.txt";

    private static final byte[] TESTDATA = "TESTDATA".getBytes();

    private static final String TIMESTAMP_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";

    public void testSiteDescUser() throws Exception {
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.sendCommand("SITE DESCUSER admin");
        String[] siteReplies = client.getReplyString().split("\r\n");
        assertEquals("200-", siteReplies[0]);
        assertEquals("userid          : admin", siteReplies[1]);
        assertEquals("userpassword    : ********", siteReplies[2]);
        assertEquals("homedirectory   : ./test-tmp/ftproot", siteReplies[3]);
        assertEquals("writepermission : true", siteReplies[4]);
        assertEquals("enableflag      : true", siteReplies[5]);
        assertEquals("idletime        : 0", siteReplies[6]);
        assertEquals("uploadrate      : 0", siteReplies[7]);
        assertEquals("200 downloadrate    : 0", siteReplies[8]);
    }

    public void testAnonNotAllowed() throws Exception {
        client.login(ANONYMOUS_USERNAME, ANONYMOUS_PASSWORD);
        assertTrue(FTPReply.isNegativePermanent(client.sendCommand("SITE DESCUSER admin")));
    }

    public void testSiteWho() throws Exception {
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.sendCommand("SITE WHO");
        String[] siteReplies = client.getReplyString().split("\r\n");
        assertEquals("200-", siteReplies[0]);
        String pattern = "200 admin           127.0.0.1       " + TIMESTAMP_PATTERN + " " + TIMESTAMP_PATTERN + " ";
        assertTrue(Pattern.matches(pattern, siteReplies[1]));
    }

    public void testSiteStat() throws Exception {
        server.stop();
        initServer();
        FTPClient client1 = new FTPClient();
        client1.connect("localhost", getListenerPort());
        assertTrue(client1.login(ADMIN_USERNAME, ADMIN_PASSWORD));
        assertTrue(client1.makeDirectory("foo"));
        assertTrue(client1.makeDirectory("foo2"));
        assertTrue(client1.removeDirectory("foo2"));
        assertTrue(client1.storeFile(TEST_FILENAME, new ByteArrayInputStream(TESTDATA)));
        assertTrue(client1.storeFile(TEST_FILENAME, new ByteArrayInputStream(TESTDATA)));
        assertTrue(client1.retrieveFile(TEST_FILENAME, new ByteArrayOutputStream()));
        assertTrue(client1.deleteFile(TEST_FILENAME));
        assertTrue(client1.logout());
        client1.disconnect();
        FTPClient client2 = new FTPClient();
        client2.connect("localhost", getListenerPort());
        assertTrue(client2.login(ANONYMOUS_USERNAME, ANONYMOUS_PASSWORD));
        client.connect("localhost", getListenerPort());
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.sendCommand("SITE STAT");
        String[] siteReplies = client.getReplyString().split("\r\n");
        assertEquals("200-", siteReplies[0]);
        String pattern = "Start Time               : " + TIMESTAMP_PATTERN;
        assertTrue(Pattern.matches(pattern, siteReplies[1]));
        assertTrue(Pattern.matches("File Upload Number       : 2", siteReplies[2]));
        assertTrue(Pattern.matches("File Download Number     : 1", siteReplies[3]));
        assertTrue(Pattern.matches("File Delete Number       : 1", siteReplies[4]));
        assertTrue(Pattern.matches("File Upload Bytes        : 16", siteReplies[5]));
        assertTrue(Pattern.matches("File Download Bytes      : 8", siteReplies[6]));
        assertTrue(Pattern.matches("Directory Create Number  : 2", siteReplies[7]));
        assertTrue(Pattern.matches("Directory Remove Number  : 1", siteReplies[8]));
        assertTrue(Pattern.matches("Current Logins           : 2", siteReplies[9]));
        assertTrue(Pattern.matches("Total Logins             : 3", siteReplies[10]));
        assertTrue(Pattern.matches("Current Anonymous Logins : 1", siteReplies[11]));
        assertTrue(Pattern.matches("Total Anonymous Logins   : 1", siteReplies[12]));
        assertTrue(Pattern.matches("Current Connections      : 2", siteReplies[13]));
        assertTrue(Pattern.matches("200 Total Connections        : 3", siteReplies[14]));
    }
}
