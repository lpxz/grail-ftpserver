package org.apache.ftpserver.impl;

import java.net.InetAddress;

public interface StatisticsObserver {

    void notifyUpload();

    void notifyDownload();

    void notifyDelete();

    void notifyMkdir();

    void notifyRmdir();

    void notifyLogin(boolean anonymous);

    void notifyLoginFail(InetAddress address);

    void notifyLogout(boolean anonymous);

    void notifyOpenConnection();

    void notifyCloseConnection();
}
