package org.apache.ftpserver.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataType;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.usermanager.impl.TransferRateRequest;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IODataConnection implements DataConnection {

    private final Logger LOG = LoggerFactory.getLogger(IODataConnection.class);

    private static final byte[] EOL = System.getProperty("line.separator").getBytes();

    private FtpIoSession session;

    private Socket socket;

    private ServerDataConnectionFactory factory;

    public IODataConnection(final Socket socket, final FtpIoSession session, final ServerDataConnectionFactory factory) {
        this.session = session;
        this.socket = socket;
        this.factory = factory;
    }

    private InputStream getDataInputStream() throws IOException {
        try {
            Socket dataSoc = socket;
            if (dataSoc == null) {
                throw new IOException("Cannot open data connection.");
            }
            InputStream is = dataSoc.getInputStream();
            if (factory.isZipMode()) {
                is = new InflaterInputStream(is);
            }
            return is;
        } catch (IOException ex) {
            factory.closeDataConnection();
            throw ex;
        }
    }

    private OutputStream getDataOutputStream() throws IOException {
        try {
            Socket dataSoc = socket;
            if (dataSoc == null) {
                throw new IOException("Cannot open data connection.");
            }
            OutputStream os = dataSoc.getOutputStream();
            if (factory.isZipMode()) {
                os = new DeflaterOutputStream(os);
            }
            return os;
        } catch (IOException ex) {
            factory.closeDataConnection();
            throw ex;
        }
    }

    public final long transferFromClient(FtpSession session, final OutputStream out) throws IOException {
        TransferRateRequest transferRateRequest = new TransferRateRequest();
        transferRateRequest = (TransferRateRequest) session.getUser().authorize(transferRateRequest);
        int maxRate = 0;
        if (transferRateRequest != null) {
            maxRate = transferRateRequest.getMaxUploadRate();
        }
        InputStream is = getDataInputStream();
        try {
            return transfer(session, false, is, out, maxRate);
        } finally {
            IoUtils.close(is);
        }
    }

    public final long transferToClient(FtpSession session, final InputStream in) throws IOException {
        TransferRateRequest transferRateRequest = new TransferRateRequest();
        transferRateRequest = (TransferRateRequest) session.getUser().authorize(transferRateRequest);
        int maxRate = 0;
        if (transferRateRequest != null) {
            maxRate = transferRateRequest.getMaxDownloadRate();
        }
        OutputStream out = getDataOutputStream();
        try {
            return transfer(session, true, in, out, maxRate);
        } finally {
            IoUtils.close(out);
        }
    }

    public final void transferToClient(FtpSession session, final String str) throws IOException {
        OutputStream out = getDataOutputStream();
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(str);
            if (session instanceof DefaultFtpSession) {
                ((DefaultFtpSession) session).increaseWrittenDataBytes(str.getBytes("UTF-8").length);
            }
        } finally {
            if (writer != null) {
                writer.flush();
            }
            IoUtils.close(writer);
        }
    }

    private final long transfer(FtpSession session, boolean isWrite, final InputStream in, final OutputStream out, final int maxRate) throws IOException {
        long transferredSize = 0L;
        boolean isAscii = session.getDataType() == DataType.ASCII;
        long startTime = System.currentTimeMillis();
        byte[] buff = new byte[4096];
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = IoUtils.getBufferedInputStream(in);
            bos = IoUtils.getBufferedOutputStream(out);
            DefaultFtpSession defaultFtpSession = null;
            if (session instanceof DefaultFtpSession) {
                defaultFtpSession = (DefaultFtpSession) session;
            }
            byte lastByte = 0;
            while (true) {
                if (maxRate > 0) {
                    long interval = System.currentTimeMillis() - startTime;
                    if (interval == 0) {
                        interval = 1;
                    }
                    long currRate = (transferredSize * 1000L) / interval;
                    if (currRate > maxRate) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            break;
                        }
                        continue;
                    }
                }
                int count = bis.read(buff);
                if (count == -1) {
                    break;
                }
                if (defaultFtpSession != null) {
                    if (isWrite) {
                        defaultFtpSession.increaseWrittenDataBytes(count);
                    } else {
                        defaultFtpSession.increaseReadDataBytes(count);
                    }
                }
                if (isAscii) {
                    for (int i = 0; i < count; ++i) {
                        byte b = buff[i];
                        if (isWrite) {
                            if (b == '\n' && lastByte != '\r') {
                                bos.write('\r');
                            }
                            bos.write(b);
                        } else {
                            if (b == '\n') {
                                if (lastByte != '\r') {
                                    bos.write(EOL);
                                }
                            } else if (b == '\r') {
                                bos.write(EOL);
                            } else {
                                bos.write(b);
                            }
                        }
                        lastByte = b;
                    }
                } else {
                    bos.write(buff, 0, count);
                }
                transferredSize += count;
                notifyObserver();
            }
        } catch (IOException e) {
            LOG.warn("Exception during data transfer, closing data connection socket", e);
            factory.closeDataConnection();
            throw e;
        } catch (RuntimeException e) {
            LOG.warn("Exception during data transfer, closing data connection socket", e);
            factory.closeDataConnection();
            throw e;
        } finally {
            if (bos != null) {
                bos.flush();
            }
        }
        return transferredSize;
    }

    protected void notifyObserver() {
        session.updateLastAccessTime();
    }
}
