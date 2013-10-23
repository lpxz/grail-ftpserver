package org.apache.ftpserver.clienttests;

import java.io.File;
import java.util.regex.Pattern;

public class StatTest extends ClientTestTemplate {

    private static final String PATTERN = "^-rw-------\\s\\s\\s1\\suser\\sgroup\\s{12}0\\s[A-Za-z0-9\\s]{6}\\s\\d\\d:\\d\\d\\stest\\d.txt$";

    private static final File TEST_DIR = new File(ROOT_DIR, "test");

    private static final File TEST_FILE1 = new File(TEST_DIR, "test1.txt");

    private static final File TEST_FILE2 = new File(TEST_DIR, "test2.txt");

    public void testStatDir() throws Exception {
        assertTrue(TEST_DIR.mkdir());
        assertTrue(TEST_FILE1.createNewFile());
        assertTrue(TEST_FILE2.createNewFile());
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        assertEquals(212, client.stat(TEST_DIR.getName()));
        String[] reply = client.getReplyString().split("\r\n");
        assertTrue(reply[1], Pattern.matches(PATTERN, reply[1]));
        assertTrue(reply[2], Pattern.matches(PATTERN, reply[2]));
    }

    public void testStatFile() throws Exception {
        assertTrue(TEST_DIR.mkdir());
        assertTrue(TEST_FILE1.createNewFile());
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        assertEquals(213, client.stat(TEST_DIR.getName() + "/" + TEST_FILE1.getName()));
        String[] reply = client.getReplyString().split("\r\n");
        assertTrue(reply[1], Pattern.matches(PATTERN, reply[1]));
    }

    public void testStat() throws Exception {
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        client.stat();
        String[] reply = client.getReplyString().split("\r\n");
        assertEquals("211-Apache FtpServer", reply[0]);
        assertEquals("Connected to 127.0.0.1", reply[1]);
        assertEquals("Connected from 127.0.0.1", reply[2]);
        assertEquals("Logged in as admin", reply[3]);
        assertEquals("211 End of status.", reply[4]);
    }
}
