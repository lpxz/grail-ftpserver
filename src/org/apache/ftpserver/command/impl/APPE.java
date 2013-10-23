package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.IODataConnectionFactory;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APPE extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(APPE.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        try {
            session.resetState();
            String fileName = request.getArgument();
            if (fileName == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "APPE", null));
                return;
            }
            DataConnectionFactory connFactory = session.getDataConnection();
            if (connFactory instanceof IODataConnectionFactory) {
                InetAddress address = ((IODataConnectionFactory) connFactory).getInetAddress();
                if (address == null) {
                    session.write(new DefaultFtpReply(FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS, "PORT or PASV must be issued first"));
                    return;
                }
            }
            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception e) {
                LOG.debug("File system threw exception", e);
            }
            if (file == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "APPE.invalid", fileName));
                return;
            }
            fileName = file.getAbsolutePath();
            if (file.doesExist() && !file.isFile()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "APPE.invalid", fileName));
                return;
            }
            if (!file.isWritable()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "APPE.permission", fileName));
                return;
            }
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_150_FILE_STATUS_OKAY, "APPE", fileName));
            DataConnection dataConnection;
            try {
                dataConnection = session.getDataConnection().openConnection();
            } catch (Exception e) {
                LOG.debug("Exception when getting data input stream", e);
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "APPE", fileName));
                return;
            }
            boolean failure = false;
            OutputStream os = null;
            try {
                long offset = 0L;
                if (file.doesExist()) {
                    offset = file.getSize();
                }
                os = file.createOutputStream(offset);
                long transSz = dataConnection.transferFromClient(session.getFtpletSession(), os);
                if (os != null) {
                    os.close();
                }
                LOG.info("File uploaded {}", fileName);
                ServerFtpStatistics ftpStat = (ServerFtpStatistics) context.getFtpStatistics();
                ftpStat.setUpload(session, file, transSz);
            } catch (SocketException e) {
                LOG.debug("SocketException during file upload", e);
                failure = true;
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED, "APPE", fileName));
            } catch (IOException e) {
                LOG.debug("IOException during file upload", e);
                failure = true;
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN, "APPE", fileName));
            } finally {
                IoUtils.close(os);
            }
            if (!failure) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "APPE", fileName));
            }
        } finally {
            session.getDataConnection().closeDataConnection();
        }
    }
}
