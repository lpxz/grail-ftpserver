package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.util.HashMap;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPTS extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(OPTS.class);

    private static final HashMap<String, Command> COMMAND_MAP = new HashMap<String, Command>(16);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String argument = request.getArgument();
        if (argument == null) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "OPTS", null));
            return;
        }
        int spaceIndex = argument.indexOf(' ');
        if (spaceIndex != -1) {
            argument = argument.substring(0, spaceIndex);
        }
        argument = argument.toUpperCase();
        String optsRequest = "OPTS_" + argument;
        Command command = (Command) COMMAND_MAP.get(optsRequest);
        try {
            if (command != null) {
                command.execute(session, context, request);
            } else {
                session.resetState();
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_502_COMMAND_NOT_IMPLEMENTED, "OPTS.not.implemented", argument));
            }
        } catch (Exception ex) {
            LOG.warn("OPTS.execute()", ex);
            session.resetState();
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_500_SYNTAX_ERROR_COMMAND_UNRECOGNIZED, "OPTS", null));
        }
    }

    static {
        COMMAND_MAP.put("OPTS_MLST", new org.apache.ftpserver.command.impl.OPTS_MLST());
        COMMAND_MAP.put("OPTS_UTF8", new org.apache.ftpserver.command.impl.OPTS_UTF8());
    }
}
