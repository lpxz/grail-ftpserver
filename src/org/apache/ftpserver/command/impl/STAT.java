package org.apache.ftpserver.command.impl;

import java.io.IOException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.command.impl.listing.DirectoryLister;
import org.apache.ftpserver.command.impl.listing.LISTFileFormater;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.ListArgumentParser;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;

public class STAT extends AbstractCommand {

    private static final LISTFileFormater LIST_FILE_FORMATER = new LISTFileFormater();

    private DirectoryLister directoryLister = new DirectoryLister();

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException {
        session.resetState();
        if (request.getArgument() != null) {
            ListArgument parsedArg = ListArgumentParser.parse(request.getArgument());
            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(parsedArg.getFile());
                if (!file.doesExist()) {
                    session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN, "LIST", null));
                    return;
                }
                String dirList = directoryLister.listFiles(parsedArg, session.getFileSystemView(), LIST_FILE_FORMATER);
                int replyCode;
                if (file.isDirectory()) {
                    replyCode = FtpReply.REPLY_212_DIRECTORY_STATUS;
                } else {
                    replyCode = FtpReply.REPLY_213_FILE_STATUS;
                }
                session.write(LocalizedFtpReply.translate(session, request, context, replyCode, "STAT", dirList));
            } catch (FtpException e) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN, "STAT", null));
            }
        } else {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_211_SYSTEM_STATUS_REPLY, "STAT", null));
        }
    }
}
