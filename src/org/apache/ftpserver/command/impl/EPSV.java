package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.ftpserver.DataConnectionException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;

public class EPSV extends AbstractCommand {

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException {
        session.resetState();
        ServerDataConnectionFactory dataCon = session.getDataConnection();
        try {
            InetSocketAddress dataConAddress = dataCon.initPassiveDataConnection();
            int servPort = dataConAddress.getPort();
            String portStr = "|||" + servPort + '|';
            session.write(LocalizedFtpReply.translate(session, request, context, 229, "EPSV", portStr));
        } catch (DataConnectionException e) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "EPSV", null));
            return;
        }
    }
}
