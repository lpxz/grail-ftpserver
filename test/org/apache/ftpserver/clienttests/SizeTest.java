package org.apache.ftpserver.clienttests;

import java.io.File;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.ftpserver.test.TestUtil;

public class SizeTest extends ClientTestTemplate {

    protected static final File TEST_DIR1 = new File(ROOT_DIR, "dir1");

    protected static final File TEST_FILE1 = new File(ROOT_DIR, "file1.txt");

    protected static final byte[] TEST_DATA1 = "TESTDATA".getBytes();

    protected void setUp() throws Exception {
        super.setUp();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    private void assertSizeReply(String reply, int size) {
        reply = reply.trim();
        reply = reply.substring(4);
        assertEquals(size, Integer.parseInt(reply));
    }

    public void testSizeOnFile() throws Exception {
        TestUtil.writeDataToFile(TEST_FILE1, TEST_DATA1);
        assertTrue(FTPReply.isPositiveCompletion(client.sendCommand("SIZE " + TEST_FILE1.getName())));
        assertSizeReply(client.getReplyString(), TEST_DATA1.length);
        assertTrue(FTPReply.isPositiveCompletion(client.sendCommand("SIZE /" + TEST_FILE1.getName())));
        assertSizeReply(client.getReplyString(), TEST_DATA1.length);
    }

    public void testSizeNoArgument() throws Exception {
        assertEquals(501, client.sendCommand("SIZE "));
    }

    public void testSizeNonExistigFile() throws Exception {
        assertEquals(550, client.sendCommand("SIZE foo"));
    }

    public void testSizeOnDir() throws Exception {
        TEST_DIR1.mkdirs();
        assertEquals(550, client.sendCommand("SIZE " + TEST_DIR1.getName()));
    }
}
