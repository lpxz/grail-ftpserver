package org.apache.ftpserver.filesystem.nativefs.impl;

import java.util.List;
import junit.framework.TestCase;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.BaseUser;

public abstract class FtpFileTestTemplate extends TestCase {

    protected static final String FILE2_PATH = "/dir1/file2";

    protected static final String DIR1_PATH = "/dir1";

    protected static final String DIR1_WITH_SLASH_PATH = "/dir1/";

    protected static final String FILE1_PATH = "/file1";

    protected static final String FILE3_PATH = "/file3";

    protected static final User USER = new BaseUser() {

        private static final long serialVersionUID = 4906315989316879758L;

        public AuthorizationRequest authorize(AuthorizationRequest request) {
            return request;
        }
    };

    protected abstract FtpFile createFileObject(String fileName, User user);

    public void testNullFileName() {
        try {
            createFileObject(null, USER);
            fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testWhiteSpaceFileName() {
        try {
            createFileObject(" \t", USER);
            fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testEmptyFileName() {
        try {
            createFileObject("", USER);
            fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testNonLeadingSlash() {
        try {
            createFileObject("foo", USER);
            fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testFullName() {
        FtpFile fileObject = createFileObject(FILE2_PATH, USER);
        assertEquals("/dir1/file2", fileObject.getAbsolutePath());
        fileObject = createFileObject("/dir1/", USER);
        assertEquals("/dir1", fileObject.getAbsolutePath());
        fileObject = createFileObject("/dir1", USER);
        assertEquals("/dir1", fileObject.getAbsolutePath());
    }

    public void testShortName() {
        FtpFile fileObject = createFileObject("/dir1/file2", USER);
        assertEquals("file2", fileObject.getName());
        fileObject = createFileObject("/dir1/", USER);
        assertEquals("dir1", fileObject.getName());
        fileObject = createFileObject("/dir1", USER);
        assertEquals("dir1", fileObject.getName());
    }

    public void testListFilesInOrder() {
        FtpFile root = createFileObject("/", USER);
        List<FtpFile> files = root.listFiles();
        assertEquals(3, files.size());
        assertEquals("dir1", files.get(0).getName());
        assertEquals("file1", files.get(1).getName());
        assertEquals("file3", files.get(2).getName());
    }
}
