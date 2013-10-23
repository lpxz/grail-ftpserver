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
import org.apache.ftpserver.util.IllegalInetAddressException;
import org.apache.ftpserver.util.IllegalPortException;
import org.apache.ftpserver.util.SocketAddressEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PORT extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(PORT.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException {
        session.resetState();
        if (!request.hasArgument()) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "PORT", null));
            return;
        }
        DataConnectionConfiguration dataCfg = session.getListener().getDataConnectionConfiguration();
        if (!dataCfg.isActiveEnabled()) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "PORT.disabled", null));
            return;
        }
        InetSocketAddress address;
        try {
            address = SocketAddressEncoder.decode(request.getArgument());
            if (address.getPort() == 0) {
                throw new IllegalPortException("PORT port must not be 0");
            }
        } catch (IllegalInetAddressException e) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "PORT", null));
            return;
        } catch (IllegalPortException e) {
            LOG.debug("Invalid data port: " + request.getArgument(), e);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "PORT.invalid", null));
            return;
        } catch (UnknownHostException e) {
            LOG.debug("Unknown host", e);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "PORT.host", null));
            return;
        }
        if (dataCfg.isActiveIpCheck()) {
            if (session.getRemoteAddress() instanceof InetSocketAddress) {
                InetAddress clientAddr = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                if (!address.getAddress().equals(clientAddr)) {
                    session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "PORT.mismatch", null));
                    return;
                }
            }
        }
        session.getDataConnection().initActiveDataConnection(address);
        session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "PORT", null));
    }
}
