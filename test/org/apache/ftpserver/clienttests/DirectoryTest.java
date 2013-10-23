package org.apache.ftpserver.clienttests;

import java.io.File;
import org.apache.commons.net.ftp.FTPReply;

public class DirectoryTest extends ClientTestTemplate {

    private static final File TEST_DIR1 = new File(ROOT_DIR, "dir1");

    private static final File TEST_DIR_IN_DIR1 = new File(TEST_DIR1, "dir3");

    protected void setUp() throws Exception {
        super.setUp();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testMkdir() throws Exception {
        assertFalse(TEST_DIR1.exists());
        assertTrue(FTPReply.isPositiveCompletion(client.mkd(TEST_DIR1.getName())));
        assertTrue(TEST_DIR1.exists());
    }

    public void testMkdirNoDirectoryName() throws Exception {
        assertEquals(501, client.sendCommand("MKD"));
    }

    public void testMkdirDouble() throws Exception {
        assertFalse(TEST_DIR1.exists());
        assertFalse(TEST_DIR_IN_DIR1.exists());
        assertFalse(FTPReply.isPositiveCompletion(client.mkd(TEST_DIR1.getName() + '/' + TEST_DIR_IN_DIR1.getName())));
        assertFalse(TEST_DIR1.exists());
        assertFalse(TEST_DIR_IN_DIR1.exists());
    }

    public void testMkdirDoubleFirstExists() throws Exception {
        TEST_DIR1.mkdirs();
        assertTrue(TEST_DIR1.exists());
        assertFalse(TEST_DIR_IN_DIR1.exists());
        assertTrue(FTPReply.isPositiveCompletion(client.mkd(TEST_DIR1.getName() + '/' + TEST_DIR_IN_DIR1.getName())));
        assertTrue(TEST_DIR1.exists());
        assertTrue(TEST_DIR_IN_DIR1.exists());
    }

    public void testMkdirExisting() throws Exception {
        TEST_DIR1.mkdirs();
        assertTrue(TEST_DIR1.exists());
        assertTrue(FTPReply.isNegativePermanent(client.mkd(TEST_DIR1.getName())));
        assertTrue(TEST_DIR1.exists());
    }

    public void testMkdirExistingFile() throws Exception {
        TEST_DIR1.createNewFile();
        assertTrue(TEST_DIR1.exists());
        assertTrue(FTPReply.isNegativePermanent(client.mkd(TEST_DIR1.getName())));
        assertTrue(TEST_DIR1.exists());
    }

    public void testMkdirWithoutWriteAccess() throws Exception {
        client.rein();
        client.login(ANONYMOUS_USERNAME, ANONYMOUS_PASSWORD);
        assertFalse(TEST_DIR1.exists());
        assertTrue(FTPReply.isNegativePermanent(client.mkd(TEST_DIR1.getName())));
        assertFalse(TEST_DIR1.exists());
    }
}
