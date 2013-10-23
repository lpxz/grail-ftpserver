package org.apache.ftpserver.commands.impl.listing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import junit.framework.TestCase;
import org.apache.ftpserver.command.impl.listing.NLSTFileFormater;
import org.apache.ftpserver.ftplet.FtpFile;

public class NLSTFileFormaterTest extends TestCase {

    private static final FtpFile TEST_FILE = new MockFileObject();

    public NLSTFileFormater formater = new NLSTFileFormater();

    public static class MockFileObject implements FtpFile {

        public InputStream createInputStream(long offset) throws IOException {
            return null;
        }

        public OutputStream createOutputStream(long offset) throws IOException {
            return null;
        }

        public boolean delete() {
            return false;
        }

        public boolean doesExist() {
            return false;
        }

        public String getAbsolutePath() {
            return "fullname";
        }

        public String getGroupName() {
            return "group";
        }

        public long getLastModified() {
            return 1;
        }

        public int getLinkCount() {
            return 1;
        }

        public String getOwnerName() {
            return "owner";
        }

        public String getName() {
            return "short";
        }

        public long getSize() {
            return 13;
        }

        public boolean isRemovable() {
            return false;
        }

        public boolean isReadable() {
            return true;
        }

        public boolean isWritable() {
            return false;
        }

        public boolean isDirectory() {
            return false;
        }

        public boolean isFile() {
            return true;
        }

        public boolean isHidden() {
            return false;
        }

        public List<FtpFile> listFiles() {
            return null;
        }

        public boolean mkdir() {
            return false;
        }

        public boolean move(FtpFile destination) {
            return false;
        }

        public boolean setLastModified(long time) {
            return false;
        }
    }

    public void testSingleFile() {
        assertEquals("short\r\n", formater.format(TEST_FILE));
    }

    public void testSingleDir() {
        FtpFile dir = new MockFileObject() {

            public boolean isDirectory() {
                return true;
            }

            public boolean isFile() {
                return false;
            }
        };
        assertEquals("short\r\n", formater.format(dir));
    }
}
