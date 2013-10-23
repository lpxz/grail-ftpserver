package org.apache.ftpserver.clienttests;

import java.io.ByteArrayInputStream;
import java.io.File;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.ftpserver.test.TestUtil;

public class StoreTest extends ClientTestTemplate {

    private static final String EOL = System.getProperty("line.separator");

    private static final String CRLF = "\r\n";

    private static final String TESTDATA = "TESTDATA" + EOL + "line2" + EOL;

    private static final String TESTDATA_CRLF = "TESTDATA" + CRLF + "line2" + CRLF;

    private static final String ENCODING = "UTF-8";

    private static final String TEST_FILENAME = "test.txt";

    private static final String TEST_FILENAME_WITH_LEADING_SPACE = " leading.txt";

    private static final int SKIP_LEN = 4;

    private static final File TEST_DIR = new File(ROOT_DIR, "foo/bar");

    private static byte[] testData = null;

    private static byte[] testDataCrLf = null;

    private static byte[] doubleTestData = null;

    private static byte[] oneAndAHalfTestData = null;

    protected void setUp() throws Exception {
        super.setUp();
        testData = TESTDATA.getBytes(ENCODING);
        testDataCrLf = TESTDATA_CRLF.getBytes(ENCODING);
        doubleTestData = (TESTDATA + TESTDATA).getBytes(ENCODING);
        oneAndAHalfTestData = ("TEST" + TESTDATA).getBytes(ENCODING);
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testStore() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        assertTrue(client.storeFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testStoreWithCrLf() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        assertTrue(client.storeFile(TEST_FILENAME, new ByteArrayInputStream(testDataCrLf)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testStoreWithLeadingSpace() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME_WITH_LEADING_SPACE);
        assertTrue(client.storeFile(TEST_FILENAME_WITH_LEADING_SPACE, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testStoreNoFileName() throws Exception {
        assertEquals(501, client.sendCommand("STOR"));
    }

    public void testStoreWithRestart() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        TestUtil.writeDataToFile(testFile, testData);
        client.setRestartOffset(SKIP_LEN);
        assertTrue(client.storeFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(oneAndAHalfTestData, testFile);
    }

    public void testStoreEmptyFile() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        assertTrue(client.storeFile(TEST_FILENAME, new ByteArrayInputStream(new byte[0])));
        assertTrue(testFile.exists());
        assertEquals(0, testFile.length());
    }

    public void testStoreWithExistingFile() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        testFile.createNewFile();
        assertTrue(testFile.exists());
        assertEquals(0, testFile.length());
        assertTrue(client.storeFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testStoreWithDirectoryInPlace() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        assertFalse(testFile.exists());
        assertTrue(testFile.mkdirs());
        assertTrue(testFile.exists());
        assertFalse(client.storeFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        assertTrue(testFile.isDirectory());
    }

    public void testStoreWithPath() throws Exception {
        TEST_DIR.mkdirs();
        File testFile = new File(TEST_DIR, TEST_FILENAME);
        assertTrue(client.storeFile("foo/bar/" + TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testStoreWithLeadingSlash() throws Exception {
        TEST_DIR.mkdirs();
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        assertTrue(client.storeFile("/" + TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testStoreWithNonExistingPath() throws Exception {
        File testFile = new File(TEST_DIR, TEST_FILENAME);
        assertFalse(client.storeFile("foo/bar/" + TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertFalse(testFile.exists());
    }

    public void testStoreWithoutWriteAccess() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        client.rein();
        client.login("anonymous", "foo@bar.com");
        assertFalse(client.storeFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertFalse(testFile.exists());
    }

    public void testStoreUniqueWithNoDirectory() throws Exception {
        assertTrue(client.storeUniqueFile(new ByteArrayInputStream(testData)));
        doAssertOfUniqueFile(client, ROOT_DIR);
    }

    public void testStoreUniqueWithCompletePath() throws Exception {
        TEST_DIR.mkdirs();
        File existingFile = new File(TEST_DIR, "existingFile.txt");
        existingFile.createNewFile();
        assertTrue(client.storeUniqueFile("foo/bar/existingFile.txt", new ByteArrayInputStream(testData)));
        doAssertOfUniqueFile(client, ROOT_DIR);
    }

    public void testStoreUniqueWithDirectory() throws Exception {
        TEST_DIR.mkdirs();
        assertTrue(client.storeUniqueFile("foo/bar", new ByteArrayInputStream(testData)));
        doAssertOfUniqueFile(client, ROOT_DIR);
    }

    public void testStoreUniqueWithDirectoryWithTrailingSlash() throws Exception {
        TEST_DIR.mkdirs();
        assertTrue(client.storeUniqueFile("foo/bar/", new ByteArrayInputStream(testData)));
        doAssertOfUniqueFile(client, ROOT_DIR);
    }

    private void doAssertOfUniqueFile(FTPClient client, File dir) throws Exception {
        String reply = client.getReplyString();
        String generatedFileName = reply.substring(5, reply.indexOf(':'));
        File testFile = new File(dir, generatedFileName);
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testAppend() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        TestUtil.writeDataToFile(testFile, testData);
        assertTrue(client.appendFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(doubleTestData, testFile);
    }

    public void testAppendNoFileName() throws Exception {
        assertEquals(501, client.sendCommand("APPE"));
    }

    public void testAppendWithDirectoryInPlace() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        testFile.mkdirs();
        assertTrue(testFile.exists());
        assertFalse(client.appendFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        assertTrue(testFile.isDirectory());
    }

    public void testAppendWithPath() throws Exception {
        TEST_DIR.mkdirs();
        File testFile = new File(TEST_DIR, TEST_FILENAME);
        TestUtil.writeDataToFile(testFile, testData);
        assertTrue(client.appendFile("foo/bar/" + TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(doubleTestData, testFile);
    }

    public void testAppendWithoutWriteAccess() throws Exception {
        client.rein();
        client.login("anonymous", "foo@bar.com");
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        assertFalse(client.appendFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertFalse(testFile.exists());
    }

    public void testAppendToNoExistingFile() throws Exception {
        File testFile = new File(ROOT_DIR, TEST_FILENAME);
        assertTrue(client.appendFile(TEST_FILENAME, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }
}
