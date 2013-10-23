package org.apache.ftpserver.ipfilter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

public class MinaIpFilter extends IoFilterAdapter {

    private IpFilter filter = null;

    public MinaIpFilter(IpFilter filter) {
        this.filter = filter;
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) {
        SocketAddress remoteAddress = session.getRemoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetAddress ipAddress = ((InetSocketAddress) remoteAddress).getAddress();
            if (!filter.accept(ipAddress)) {
                session.close(true);
            } else {
                nextFilter.sessionCreated(session);
            }
        }
    }
}
