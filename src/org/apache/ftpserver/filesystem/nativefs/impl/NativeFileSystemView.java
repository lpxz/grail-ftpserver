package org.apache.ftpserver.filesystem.nativefs.impl;

import java.io.File;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeFileSystemView implements FileSystemView {

    private final Logger LOG = LoggerFactory.getLogger(NativeFileSystemView.class);

    private String rootDir;

    private String currDir;

    private User user;

    private boolean caseInsensitive = false;

    protected NativeFileSystemView(User user) throws FtpException {
        this(user, false);
    }

    public NativeFileSystemView(User user, boolean caseInsensitive) throws FtpException {
        if (user == null) {
            throw new IllegalArgumentException("user can not be null");
        }
        if (user.getHomeDirectory() == null) {
            throw new IllegalArgumentException("User home directory can not be null");
        }
        this.caseInsensitive = caseInsensitive;
        String rootDir = user.getHomeDirectory();
        rootDir = NativeFtpFile.normalizeSeparateChar(rootDir);
        if (!rootDir.endsWith("/")) {
            rootDir += '/';
        }
        LOG.debug("Native filesystem view created for user \"{}\" with root \"{}\"", user.getName(), rootDir);
        this.rootDir = rootDir;
        this.user = user;
        currDir = "/";
    }

    public FtpFile getHomeDirectory() {
        return new NativeFtpFile("/", new File(rootDir), user);
    }

    public FtpFile getWorkingDirectory() {
        FtpFile fileObj = null;
        if (currDir.equals("/")) {
            fileObj = new NativeFtpFile("/", new File(rootDir), user);
        } else {
            File file = new File(rootDir, currDir.substring(1));
            fileObj = new NativeFtpFile(currDir, file, user);
        }
        return fileObj;
    }

    public FtpFile getFile(String file) {
        String physicalName = NativeFtpFile.getPhysicalName(rootDir, currDir, file, caseInsensitive);
        File fileObj = new File(physicalName);
        String userFileName = physicalName.substring(rootDir.length() - 1);
        return new NativeFtpFile(userFileName, fileObj, user);
    }

    public boolean changeWorkingDirectory(String dir) {
        dir = NativeFtpFile.getPhysicalName(rootDir, currDir, dir, caseInsensitive);
        File dirObj = new File(dir);
        if (!dirObj.isDirectory()) {
            return false;
        }
        dir = dir.substring(rootDir.length() - 1);
        if (dir.charAt(dir.length() - 1) != '/') {
            dir = dir + '/';
        }
        currDir = dir;
        return true;
    }

    public boolean isRandomAccessible() {
        return true;
    }

    public void dispose() {
    }
}
