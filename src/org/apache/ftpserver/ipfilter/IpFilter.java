package org.apache.ftpserver.ipfilter;

import java.net.InetAddress;

public interface IpFilter {

    public boolean accept(InetAddress address);
}
