package org.apache.ftpserver.impl;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpStatistics;

public interface ServerFtpStatistics extends FtpStatistics {

    void setObserver(StatisticsObserver observer);

    void setFileObserver(FileObserver observer);

    void setUpload(FtpIoSession session, FtpFile file, long size);

    void setDownload(FtpIoSession session, FtpFile file, long size);

    void setMkdir(FtpIoSession session, FtpFile dir);

    void setRmdir(FtpIoSession session, FtpFile dir);

    void setDelete(FtpIoSession session, FtpFile file);

    void setOpenConnection(FtpIoSession session);

    void setCloseConnection(FtpIoSession session);

    void setLogin(FtpIoSession session);

    void setLoginFail(FtpIoSession session);

    void setLogout(FtpIoSession session);

    void resetStatisticsCounters();
}
