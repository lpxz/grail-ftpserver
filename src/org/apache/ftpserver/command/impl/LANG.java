package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.util.List;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.message.MessageResource;

public class LANG extends AbstractCommand {

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        session.resetState();
        String language = request.getArgument();
        if (language == null) {
            session.setLanguage(null);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "LANG", null));
            return;
        }
        language = language.toLowerCase();
        MessageResource msgResource = context.getMessageResource();
        List<String> availableLanguages = msgResource.getAvailableLanguages();
        if (availableLanguages != null) {
            for (int i = 0; i < availableLanguages.size(); ++i) {
                if (availableLanguages.get(i).equals(language)) {
                    session.setLanguage(language);
                    session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_200_COMMAND_OKAY, "LANG", null));
                    return;
                }
            }
        }
        session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_504_COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER, "LANG", null));
    }
}
