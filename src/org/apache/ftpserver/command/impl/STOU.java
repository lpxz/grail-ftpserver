package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FileSystemView;
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

public class STOU extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(STOU.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        try {
            DataConnectionFactory connFactory = session.getDataConnection();
            if (connFactory instanceof IODataConnectionFactory) {
                InetAddress address = ((IODataConnectionFactory) connFactory).getInetAddress();
                if (address == null) {
                    session.write(new DefaultFtpReply(FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS, "PORT or PASV must be issued first"));
                    return;
                }
            }
            session.resetState();
            String pathName = request.getArgument();
            FtpFile file = null;
            try {
                String filePrefix;
                if (pathName == null) {
                    filePrefix = "ftp.dat";
                } else {
                    FtpFile dir = session.getFileSystemView().getFile(pathName);
                    if (dir.isDirectory()) {
                        filePrefix = pathName + "/ftp.dat";
                    } else {
                        filePrefix = pathName;
                    }
                }
                file = session.getFileSystemView().getFile(filePrefix);
                if (file != null) {
                    file = getUniqueFile(session, file);
                }
            } catch (Exception ex) {
                LOG.debug("Exception getting file object", ex);
            }
            if (file == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "STOU", null));
                return;
            }
            String fileName = file.getAbsolutePath();
            if (!file.isWritable()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "STOU.permission", fileName));
                return;
            }
            session.write(new DefaultFtpReply(FtpReply.REPLY_150_FILE_STATUS_OKAY, "FILE: " + fileName));
            boolean failure = false;
            OutputStream os = null;
            DataConnection dataConnection;
            try {
                dataConnection = session.getDataConnection().openConnection();
            } catch (Exception e) {
                LOG.debug("Exception getting the input data stream", e);
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "STOU", fileName));
                return;
            }
            try {
                os = file.createOutputStream(0L);
                long transSz = dataConnection.transferFromClient(session.getFtpletSession(), os);
                if (os != null) {
                    os.close();
                }
                LOG.info("File uploaded {}", fileName);
                ServerFtpStatistics ftpStat = (ServerFtpStatistics) context.getFtpStatistics();
                if (ftpStat != null) {
                    ftpStat.setUpload(session, file, transSz);
                }
            } catch (SocketException ex) {
                LOG.debug("Socket exception during data transfer", ex);
                failure = true;
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED, "STOU", fileName));
            } catch (IOException ex) {
                LOG.debug("IOException during data transfer", ex);
                failure = true;
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN, "STOU", fileName));
            } finally {
                IoUtils.close(os);
            }
            if (!failure) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "STOU", fileName));
            }
        } finally {
            session.getDataConnection().closeDataConnection();
        }
    }

    protected FtpFile getUniqueFile(FtpIoSession session, FtpFile oldFile) throws FtpException {
        FtpFile newFile = oldFile;
        FileSystemView fsView = session.getFileSystemView();
        String fileName = newFile.getAbsolutePath();
        while (newFile.doesExist()) {
            newFile = fsView.getFile(fileName + '.' + System.currentTimeMillis());
            if (newFile == null) {
                break;
            }
        }
        return newFile;
    }
}
