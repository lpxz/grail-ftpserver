package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.apache.ftpserver.DataConnectionException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.util.SocketAddressEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PASV extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(PASV.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        session.resetState();
        ServerDataConnectionFactory dataCon = session.getDataConnection();
        String externalPassiveAddress = session.getListener().getDataConnectionConfiguration().getPassiveExernalAddress();
        try {
            InetSocketAddress dataConAddress = dataCon.initPassiveDataConnection();
            InetAddress servAddr;
            if (externalPassiveAddress != null) {
                servAddr = resolveAddress(externalPassiveAddress);
            } else {
                servAddr = dataConAddress.getAddress();
            }
            InetSocketAddress externalDataConAddress = new InetSocketAddress(servAddr, dataConAddress.getPort());
            String addrStr = SocketAddressEncoder.encode(externalDataConAddress);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_227_ENTERING_PASSIVE_MODE, "PASV", addrStr));
        } catch (DataConnectionException e) {
            LOG.warn("Failed to open passive data connection", e);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "PASV", null));
            return;
        }
    }

    private InetAddress resolveAddress(String host) throws DataConnectionException {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            throw new DataConnectionException(ex.getLocalizedMessage(), ex);
        }
    }
}
