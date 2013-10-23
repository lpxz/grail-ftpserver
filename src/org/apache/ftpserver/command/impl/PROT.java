package org.apache.ftpserver.command.impl;

import java.io.IOException;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.ssl.SslConfiguration;

public class PROT extends AbstractCommand {

    private SslConfiguration getSslConfiguration(final FtpIoSession session) {
        DataConnectionConfiguration dataCfg = session.getListener().getDataConnectionConfiguration();
        SslConfiguration configuration = dataCfg.getSslConfiguration();
        if (configuration == null) {
            configuration = session.getListener().getSslConfiguration();
        }
        return configuration;
    }

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String arg = request.getArgument();
        if (arg == null) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "PROT", null));
            return;
        }
        arg = arg.toUpperCase();
        ServerDataConnectionFactory dcon = session.getDataConnection();
        if (arg.equals("C")) {
            dcon.setSecure(false);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "PROT", null));
        } else if (arg.equals("P")) {
            if (getSslConfiguration(session) == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, 431, "PROT", null));
            } else {
                dcon.setSecure(true);
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "PROT", null));
            }
        } else {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_504_COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER, "PROT", null));
        }
    }
}
