package org.apache.ftpserver.commands.impl.listing;

import java.io.File;
import junit.framework.TestCase;
import org.apache.ftpserver.command.impl.listing.DirectoryLister;
import org.apache.ftpserver.command.impl.listing.FileFormater;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.NLSTFileFormater;
import org.apache.ftpserver.filesystem.nativefs.impl.NativeFileSystemView;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.test.TestUtil;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.util.IoUtils;

public class DirectoryListerTest extends TestCase {

    private static final File TEST_TMP_DIR = new File("test-tmp");

    protected static final File ROOT_DIR = new File(TEST_TMP_DIR, "ftproot");

    private static final File TEST_FILE1 = new File(ROOT_DIR, "test1.txt");

    private static final File TEST_DIR1 = new File(ROOT_DIR, "dir1");

    private static final File TEST_DIR2 = new File(ROOT_DIR, "dir2");

    private static final File TEST_FILE1_IN_DIR1 = new File(TEST_DIR1, "test3.txt");

    private static final File TEST_FILE2_IN_DIR1 = new File(TEST_DIR1, "test4.txt");

    private static final File TEST_DIR_IN_DIR1 = new File(TEST_DIR1, "dir3");

    private static final byte[] TEST_DATA = "TESTDATA".getBytes();

    private DirectoryLister directoryLister;

    private FileSystemView fileSystemView;

    protected void setUp() throws Exception {
        BaseUser baseUser = new BaseUser();
        baseUser.setHomeDirectory(ROOT_DIR.getAbsolutePath());
        fileSystemView = new NativeFileSystemView(baseUser) {
        };
        directoryLister = new DirectoryLister();
        assertTrue(ROOT_DIR.mkdirs());
        assertTrue(TEST_DIR1.mkdirs());
        assertTrue(TEST_DIR2.mkdirs());
        TestUtil.writeDataToFile(TEST_FILE1, TEST_DATA);
        TestUtil.writeDataToFile(TEST_FILE1_IN_DIR1, TEST_DATA);
        TEST_FILE2_IN_DIR1.createNewFile();
        assertTrue(TEST_DIR_IN_DIR1.mkdir());
    }

    public void testListFiles() throws Exception {
        ListArgument arg = new ListArgument(TEST_DIR1.getName(), null, null);
        FileFormater formater = new NLSTFileFormater();
        String actual = directoryLister.listFiles(arg, fileSystemView, formater);
        assertEquals("dir3\r\ntest3.txt\r\ntest4.txt\r\n", actual);
    }

    protected void tearDown() throws Exception {
        if (TEST_TMP_DIR.exists()) {
            IoUtils.delete(TEST_TMP_DIR);
        }
    }
}
