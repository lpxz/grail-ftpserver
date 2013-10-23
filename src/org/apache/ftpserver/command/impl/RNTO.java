package org.apache.ftpserver.command.impl;

import java.io.IOException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RNTO extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(RNTO.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        try {
            String toFileStr = request.getArgument();
            if (toFileStr == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "RNTO", null));
                return;
            }
            FtpFile frFile = session.getRenameFrom();
            if (frFile == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS, "RNTO", null));
                return;
            }
            FtpFile toFile = null;
            try {
                toFile = session.getFileSystemView().getFile(toFileStr);
            } catch (Exception ex) {
                LOG.debug("Exception getting file object", ex);
            }
            if (toFile == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_553_REQUESTED_ACTION_NOT_TAKEN_FILE_NAME_NOT_ALLOWED, "RNTO.invalid", null));
                return;
            }
            toFileStr = toFile.getAbsolutePath();
            if (!toFile.isWritable()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_553_REQUESTED_ACTION_NOT_TAKEN_FILE_NAME_NOT_ALLOWED, "RNTO.permission", null));
                return;
            }
            if (!frFile.doesExist()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_553_REQUESTED_ACTION_NOT_TAKEN_FILE_NAME_NOT_ALLOWED, "RNTO.missing", null));
                return;
            }
            String logFrFileAbsolutePath = frFile.getAbsolutePath();
            if (frFile.move(toFile)) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "RNTO", toFileStr));
                LOG.info("File rename from \"{}\" to \"{}\"", logFrFileAbsolutePath, toFile.getAbsolutePath());
            } else {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_553_REQUESTED_ACTION_NOT_TAKEN_FILE_NAME_NOT_ALLOWED, "RNTO", toFileStr));
            }
        } finally {
            session.resetState();
        }
    }
}
