package org.apache.ftpserver.filesystem.nativefs.impl;

import junit.framework.TestCase;
import org.apache.ftpserver.usermanager.impl.BaseUser;

public abstract class FileSystemViewTemplate extends TestCase {

    protected static final String DIR1_NAME = "dir1";

    protected BaseUser user = new BaseUser();

    public void testChangeDirectory() throws Exception {
        NativeFileSystemView view = new NativeFileSystemView(user);
        assertEquals("/", view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.changeWorkingDirectory(DIR1_NAME));
        assertEquals("/" + DIR1_NAME, view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.changeWorkingDirectory("."));
        assertEquals("/" + DIR1_NAME, view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.changeWorkingDirectory(".."));
        assertEquals("/", view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.changeWorkingDirectory("./" + DIR1_NAME));
        assertEquals("/" + DIR1_NAME, view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.changeWorkingDirectory("~"));
        assertEquals("/", view.getWorkingDirectory().getAbsolutePath());
    }

    public void testChangeDirectoryCaseInsensitive() throws Exception {
        NativeFileSystemView view = new NativeFileSystemView(user, true);
        assertEquals("/", view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.changeWorkingDirectory("/DIR1"));
        assertEquals("/dir1", view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.getWorkingDirectory().doesExist());
        assertTrue(view.changeWorkingDirectory("/dir1"));
        assertEquals("/dir1", view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.getWorkingDirectory().doesExist());
        assertTrue(view.changeWorkingDirectory("/DiR1"));
        assertEquals("/dir1", view.getWorkingDirectory().getAbsolutePath());
        assertTrue(view.getWorkingDirectory().doesExist());
    }
}
