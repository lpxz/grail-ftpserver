package org.apache.ftpserver.impl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.ftpserver.DataConnectionException;
import org.apache.ftpserver.ftplet.DataConnectionFactory;

public interface ServerDataConnectionFactory extends DataConnectionFactory {

    void initActiveDataConnection(InetSocketAddress address);

    InetSocketAddress initPassiveDataConnection() throws DataConnectionException;

    void setSecure(boolean secure);

    void setServerControlAddress(InetAddress serverControlAddress);

    void setZipMode(boolean zip);

    boolean isTimeout(long currTime);

    void dispose();

    boolean isSecure();

    boolean isZipMode();

    InetAddress getInetAddress();

    int getPort();
}
