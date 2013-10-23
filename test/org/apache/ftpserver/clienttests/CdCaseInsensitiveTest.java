package org.apache.ftpserver.clienttests;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;

public class CdCaseInsensitiveTest extends CdTest {

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        NativeFileSystemFactory fs = (NativeFileSystemFactory) server.getFileSystem();
        fs.setCaseInsensitive(true);
        return server;
    }

    public void testCWDCaseInsensitive() throws Exception {
        assertTrue(client.changeWorkingDirectory(TEST_DIR1.getName().toUpperCase()));
        assertEquals("/dir1", client.printWorkingDirectory());
        assertTrue(client.changeWorkingDirectory(TEST_DIR_IN_DIR1.getName().toUpperCase()));
        assertEquals("/dir1/dir3", client.printWorkingDirectory());
        assertTrue(client.changeWorkingDirectory("/DiR2"));
        assertEquals("/dir2", client.printWorkingDirectory());
    }
}
