package org.apache.ftpserver.clienttests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.ftpserver.test.TestUtil;

public class I18NTest extends ClientTestTemplate {

    private static final String TESTDATA = "TESTDATA";

    private static final String ENCODING = "UTF-8";

    private static byte[] testData = null;

    protected void setUp() throws Exception {
        super.setUp();
        testData = TESTDATA.getBytes(ENCODING);
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    protected FTPClient createFTPClient() throws Exception {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF-8");
        return client;
    }

    public void testStoreWithUTF8FileName() throws Exception {
        String oddFileName = "����";
        File testFile = new File(ROOT_DIR, oddFileName);
        assertTrue(client.storeFile(oddFileName, new ByteArrayInputStream(testData)));
        assertTrue(testFile.exists());
        TestUtil.assertFileEqual(testData, testFile);
    }

    public void testRetrieveWithUTF8FileName() throws Exception {
        String oddFileName = "����";
        File testFile = new File(ROOT_DIR, oddFileName);
        TestUtil.writeDataToFile(testFile, testData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertTrue(client.retrieveFile(testFile.getName(), baos));
        TestUtil.assertArraysEqual(testData, baos.toByteArray());
    }
}
