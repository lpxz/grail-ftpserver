package org.apache.ftpserver.command.impl;

import java.io.IOException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;

public class SYST extends AbstractCommand {

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException {
        session.resetState();
        String systemName = System.getProperty("os.name");
        if (systemName == null) {
            systemName = "UNKNOWN";
        } else {
            systemName = systemName.toUpperCase();
            systemName = systemName.replace(' ', '-');
        }
        session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_215_NAME_SYSTEM_TYPE, "SYST", systemName));
    }
}
