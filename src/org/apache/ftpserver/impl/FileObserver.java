package org.apache.ftpserver.impl;

import org.apache.ftpserver.ftplet.FtpFile;

public interface FileObserver {

    void notifyUpload(FtpIoSession session, FtpFile file, long size);

    void notifyDownload(FtpIoSession session, FtpFile file, long size);

    void notifyDelete(FtpIoSession session, FtpFile file);

    void notifyMkdir(FtpIoSession session, FtpFile file);

    void notifyRmdir(FtpIoSession session, FtpFile file);
}
