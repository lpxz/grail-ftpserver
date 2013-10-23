package org.apache.ftpserver.command.impl;

import java.io.IOException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.message.MessageResource;

public class HELP extends AbstractCommand {

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException {
        session.resetState();
        if (!request.hasArgument()) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_214_HELP_MESSAGE, null, null));
            return;
        }
        String ftpCmd = request.getArgument().toUpperCase();
        MessageResource resource = context.getMessageResource();
        if (resource.getMessage(FtpReply.REPLY_214_HELP_MESSAGE, ftpCmd, session.getLanguage()) == null) {
            ftpCmd = null;
        }
        session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_214_HELP_MESSAGE, ftpCmd, null));
    }
}
