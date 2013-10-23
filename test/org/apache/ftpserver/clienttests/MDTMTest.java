package org.apache.ftpserver.clienttests;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MDTMTest extends ClientTestTemplate {

    private static final SimpleDateFormat FTP_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss.SSS");

    static {
        FTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final File TEST_FILE1 = new File(ROOT_DIR, "test1.txt");

    private static final File TEST_DIR1 = new File(ROOT_DIR, "dir1");

    protected void setUp() throws Exception {
        super.setUp();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testMDTMForFile() throws Exception {
        assertFalse(TEST_FILE1.exists());
        assertTrue(TEST_FILE1.createNewFile());
        Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        expected.clear();
        expected.setTimeInMillis(TEST_FILE1.lastModified());
        assertEquals(213, client.sendCommand("MDTM " + TEST_FILE1.getName()));
        Calendar actual = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        actual.clear();
        actual.setTime(FTP_DATE_FORMAT.parse(client.getReplyString().substring(4).trim()));
        assertEquals(expected, actual);
    }

    public void testMDTMForDir() throws Exception {
        assertFalse(TEST_DIR1.exists());
        assertTrue(TEST_DIR1.mkdirs());
        Date expected = new Date(TEST_DIR1.lastModified());
        assertEquals(213, client.sendCommand("MDTM " + TEST_DIR1.getName()));
        Date actual = FTP_DATE_FORMAT.parse(client.getReplyString().substring(4).trim());
        assertEquals(expected, actual);
    }

    public void testMDTMForNonExistingFile() throws Exception {
        assertEquals(550, client.sendCommand("MDTM " + TEST_FILE1.getName()));
    }

    public void testMDTMWithNoFileName() throws Exception {
        assertEquals(501, client.sendCommand("MDTM"));
    }
}
