package org.apache.ftpserver.command.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DataType;
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

public class RETR extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(RETR.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        try {
            long skipLen = session.getFileOffset();
            String fileName = request.getArgument();
            if (fileName == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "RETR", null));
                return;
            }
            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception ex) {
                LOG.debug("Exception getting file object", ex);
            }
            if (file == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "RETR.missing", fileName));
                return;
            }
            fileName = file.getAbsolutePath();
            if (!file.doesExist()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "RETR.missing", fileName));
                return;
            }
            if (!file.isFile()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "RETR.invalid", fileName));
                return;
            }
            if (!file.isReadable()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "RETR.permission", fileName));
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
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_150_FILE_STATUS_OKAY, "RETR", null));
            boolean failure = false;
            InputStream is = null;
            DataConnection dataConnection;
            try {
                dataConnection = session.getDataConnection().openConnection();
            } catch (Exception e) {
                LOG.debug("Exception getting the output data stream", e);
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "RETR", null));
                return;
            }
            try {
                is = openInputStream(session, file, skipLen);
                long transSz = dataConnection.transferToClient(session.getFtpletSession(), is);
                if (is != null) {
                    is.close();
                }
                LOG.info("File downloaded {}", fileName);
                ServerFtpStatistics ftpStat = (ServerFtpStatistics) context.getFtpStatistics();
                if (ftpStat != null) {
                    ftpStat.setDownload(session, file, transSz);
                }
            } catch (SocketException ex) {
                LOG.debug("Socket exception during data transfer", ex);
                failure = true;
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED, "RETR", fileName));
            } catch (IOException ex) {
                LOG.debug("IOException during data transfer", ex);
                failure = true;
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN, "RETR", fileName));
            } finally {
                IoUtils.close(is);
            }
            if (!failure) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "RETR", fileName));
            }
        } finally {
            session.resetState();
            session.getDataConnection().closeDataConnection();
        }
    }

    public InputStream openInputStream(FtpIoSession session, FtpFile file, long skipLen) throws IOException {
        InputStream in;
        if (session.getDataType() == DataType.ASCII) {
            int c;
            long offset = 0L;
            in = new BufferedInputStream(file.createInputStream(0L));
            while (offset++ < skipLen) {
                if ((c = in.read()) == -1) {
                    throw new IOException("Cannot skip");
                }
                if (c == '\n') {
                    offset++;
                }
            }
        } else {
            in = file.createInputStream(skipLen);
        }
        return in;
    }
}
