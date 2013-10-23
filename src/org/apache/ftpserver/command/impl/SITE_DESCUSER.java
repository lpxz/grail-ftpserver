package org.apache.ftpserver.command.impl;

import java.io.IOException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.usermanager.impl.TransferRateRequest;
import org.apache.ftpserver.usermanager.impl.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SITE_DESCUSER extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(SITE_DESCUSER.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        session.resetState();
        UserManager userManager = context.getUserManager();
        boolean isAdmin = userManager.isAdmin(session.getUser().getName());
        if (!isAdmin) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_530_NOT_LOGGED_IN, "SITE", null));
            return;
        }
        String argument = request.getArgument();
        int spIndex = argument.indexOf(' ');
        if (spIndex == -1) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS, "SITE.DESCUSER", null));
            return;
        }
        String userName = argument.substring(spIndex + 1);
        UserManager usrManager = context.getUserManager();
        User user = null;
        try {
            if (usrManager.doesExist(userName)) {
                user = usrManager.getUserByName(userName);
            }
        } catch (FtpException ex) {
            LOG.debug("Exception trying to get user from user manager", ex);
            user = null;
        }
        if (user == null) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "SITE.DESCUSER", userName));
            return;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("\n");
        sb.append("userid          : ").append(user.getName()).append("\n");
        sb.append("userpassword    : ********\n");
        sb.append("homedirectory   : ").append(user.getHomeDirectory()).append("\n");
        sb.append("writepermission : ").append(user.authorize(new WriteRequest()) != null).append("\n");
        sb.append("enableflag      : ").append(user.getEnabled()).append("\n");
        sb.append("idletime        : ").append(user.getMaxIdleTime()).append("\n");
        TransferRateRequest transferRateRequest = new TransferRateRequest();
        transferRateRequest = (TransferRateRequest) session.getUser().authorize(transferRateRequest);
        if (transferRateRequest != null) {
            sb.append("uploadrate      : ").append(transferRateRequest.getMaxUploadRate()).append("\n");
            sb.append("downloadrate    : ").append(transferRateRequest.getMaxDownloadRate()).append("\n");
        } else {
            sb.append("uploadrate      : 0\n");
            sb.append("downloadrate    : 0\n");
        }
        sb.append('\n');
        session.write(new DefaultFtpReply(FtpReply.REPLY_200_COMMAND_OKAY, sb.toString()));
    }
}
