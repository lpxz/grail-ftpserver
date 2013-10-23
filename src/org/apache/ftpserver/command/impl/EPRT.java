package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EPRT extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(EPRT.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException {
        session.resetState();
        String arg = request.getArgument();
        if (arg == null) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "EPRT", null));
            return;
        }
        DataConnectionConfiguration dataCfg = session.getListener().getDataConnectionConfiguration();
        if (!dataCfg.isActiveEnabled()) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "EPRT.disabled", null));
            return;
        }
        String host = null;
        String port = null;
        try {
            char delim = arg.charAt(0);
            int lastDelimIdx = arg.indexOf(delim, 3);
            host = arg.substring(3, lastDelimIdx);
            port = arg.substring(lastDelimIdx + 1, arg.length() - 1);
        } catch (Exception ex) {
            LOG.debug("Exception parsing host and port: " + arg, ex);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "EPRT", null));
            return;
        }
        InetAddress dataAddr = null;
        try {
            dataAddr = InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            LOG.debug("Unknown host: " + host, ex);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "EPRT.host", null));
            return;
        }
        if (dataCfg.isActiveIpCheck()) {
            if (session.getRemoteAddress() instanceof InetSocketAddress) {
                InetAddress clientAddr = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                if (!dataAddr.equals(clientAddr)) {
                    session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "EPRT.mismatch", null));
                    return;
                }
            }
        }
        int dataPort = 0;
        try {
            dataPort = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            LOG.debug("Invalid port: " + port, ex);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "EPRT.invalid", null));
            return;
        }
        session.getDataConnection().initActiveDataConnection(new InetSocketAddress(dataAddr, dataPort));
        session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "EPRT", null));
    }
}
