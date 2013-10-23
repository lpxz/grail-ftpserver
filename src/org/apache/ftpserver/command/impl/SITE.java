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

public class SITE extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(SITE.class);

    private static final HashMap<String, Command> COMMAND_MAP = new HashMap<String, Command>(16);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        String argument = request.getArgument();
        if (argument != null) {
            int spaceIndex = argument.indexOf(' ');
            if (spaceIndex != -1) {
                argument = argument.substring(0, spaceIndex);
            }
            argument = argument.toUpperCase();
        }
        if (argument == null) {
            session.resetState();
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "SITE", null));
            return;
        }
        String siteRequest = "SITE_" + argument;
        Command command = (Command) COMMAND_MAP.get(siteRequest);
        try {
            if (command != null) {
                command.execute(session, context, request);
            } else {
                session.resetState();
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_502_COMMAND_NOT_IMPLEMENTED, "SITE", argument));
            }
        } catch (Exception ex) {
            LOG.warn("SITE.execute()", ex);
            session.resetState();
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_500_SYNTAX_ERROR_COMMAND_UNRECOGNIZED, "SITE", null));
        }
    }

    static {
        COMMAND_MAP.put("SITE_DESCUSER", new org.apache.ftpserver.command.impl.SITE_DESCUSER());
        COMMAND_MAP.put("SITE_HELP", new org.apache.ftpserver.command.impl.SITE_HELP());
        COMMAND_MAP.put("SITE_STAT", new org.apache.ftpserver.command.impl.SITE_STAT());
        COMMAND_MAP.put("SITE_WHO", new org.apache.ftpserver.command.impl.SITE_WHO());
        COMMAND_MAP.put("SITE_ZONE", new org.apache.ftpserver.command.impl.SITE_ZONE());
    }
}
