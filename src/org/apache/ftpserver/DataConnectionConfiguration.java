package org.apache.ftpserver;

import java.net.InetAddress;
import org.apache.ftpserver.ssl.SslConfiguration;

public interface DataConnectionConfiguration {

    int getIdleTime();

    boolean isActiveEnabled();

    boolean isActiveIpCheck();

    String getActiveLocalAddress();

    int getActiveLocalPort();

    String getPassiveAddress();

    String getPassiveExernalAddress();

    String getPassivePorts();

    int requestPassivePort();

    void releasePassivePort(int port);

    SslConfiguration getSslConfiguration();

    boolean isImplicitSsl();
}
