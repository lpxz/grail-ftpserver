package org.apache.ftpserver.clienttests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import org.apache.ftpserver.test.TestUtil;

public class RetrieveTest extends ClientTestTemplate {

    private static final String TEST_FILENAME = "test.txt";

    private static final String TEST_FILENAME_WITH_LEADING_SPACE = " leading.txt";

    private static final File TEST_FILE = new File(ROOT_DIR, TEST_FILENAME);

    private static final File TEST_FILE_WITH_LEADING_SPACE = new File(ROOT_DIR, TEST_FILENAME_WITH_LEADING_SPACE);

    private static final String EOL = System.getProperty("line.separator");

    private static byte[] testData = null;

    protected void setUp() throws Exception {
        super.setUp();
        testData = ("TESTDATA" + EOL).getBytes("UTF-8");
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testRetrieve() throws Exception {
        TestUtil.writeDataToFile(TEST_FILE, testData);
        assertTrue(TEST_FILE.exists());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertTrue(client.retrieveFile(TEST_FILENAME, baos));
        assertTrue(TEST_FILE.exists());
        TestUtil.assertArraysEqual(testData, baos.toByteArray());
    }

    public void testRetrieveWithLeadingSpace() throws Exception {
        TestUtil.writeDataToFile(TEST_FILE_WITH_LEADING_SPACE, testData);
        assertTrue(TEST_FILE_WITH_LEADING_SPACE.exists());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertTrue(client.retrieveFile(TEST_FILENAME_WITH_LEADING_SPACE, baos));
        assertTrue(TEST_FILE_WITH_LEADING_SPACE.exists());
        TestUtil.assertArraysEqual(testData, baos.toByteArray());
    }

    public void testRetrieveNoFileName() throws Exception {
        assertEquals(501, client.sendCommand("RETR"));
    }

    public void testRetrieveInValidFileName() throws Exception {
        assertEquals(550, client.sendCommand("RETR foo:bar;foo"));
    }

    public void testRetrieveDirectory() throws Exception {
        TEST_FILE.mkdirs();
        assertEquals(550, client.sendCommand("RETR " + TEST_FILE.getName()));
    }

    public void testRetrieveWithRestart() throws Exception {
        int skipLen = 4;
        TestUtil.writeDataToFile(TEST_FILE, testData);
        assertTrue(TEST_FILE.exists());
        client.setRestartOffset(4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertTrue(client.retrieveFile(TEST_FILENAME, baos));
        int len = testData.length - skipLen;
        byte[] expected = new byte[len];
        System.arraycopy(testData, skipLen, expected, 0, len);
        assertTrue(TEST_FILE.exists());
        TestUtil.assertArraysEqual(expected, baos.toByteArray());
    }

    public void testRetrieveWithPath() throws Exception {
        File dir = new File(ROOT_DIR, "foo/bar");
        dir.mkdirs();
        File testFile = new File(dir, TEST_FILENAME);
        TestUtil.writeDataToFile(testFile, testData);
        assertTrue(testFile.exists());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertTrue(client.retrieveFile("foo/bar/" + TEST_FILENAME, baos));
        assertTrue(testFile.exists());
        TestUtil.assertArraysEqual(testData, baos.toByteArray());
    }

    public void testRetrieveNonExistingFile() throws Exception {
        assertFalse(TEST_FILE.exists());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertFalse(client.retrieveFile(TEST_FILENAME, baos));
    }
}
