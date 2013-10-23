package org.apache.ftpserver.filesystem.nativefs.impl;

import java.io.File;
import java.io.IOException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.util.IoUtils;

public class NativeFileSystemViewTest extends FileSystemViewTemplate {

    private static final File TEST_TMP_DIR = new File("test-tmp");

    private static final File ROOT_DIR = new File(TEST_TMP_DIR, "ftproot");

    private static final File TEST_DIR1 = new File(ROOT_DIR, DIR1_NAME);

    protected void setUp() throws Exception {
        initDirs();
        TEST_DIR1.mkdirs();
        user.setHomeDirectory(ROOT_DIR.getAbsolutePath());
    }

    public void testConstructor() throws FtpException {
        NativeFileSystemView view = new NativeFileSystemView(user);
        assertEquals("/", view.getWorkingDirectory().getAbsolutePath());
    }

    public void testConstructorWithNullUser() throws FtpException {
        try {
            new NativeFileSystemView(null);
            fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testConstructorWithNullHomeDir() throws FtpException {
        user.setHomeDirectory(null);
        try {
            new NativeFileSystemView(user);
            fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    protected void tearDown() throws Exception {
        cleanTmpDirs();
    }

    protected void initDirs() throws IOException {
        cleanTmpDirs();
        TEST_TMP_DIR.mkdirs();
        ROOT_DIR.mkdirs();
    }

    protected void cleanTmpDirs() throws IOException {
        if (TEST_TMP_DIR.exists()) {
            IoUtils.delete(TEST_TMP_DIR);
        }
    }
}
