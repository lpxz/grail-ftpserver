package org.apache.ftpserver.impl;

import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.UUID;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DataType;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Structure;
import org.apache.ftpserver.ftplet.User;

public class DefaultFtpSession implements FtpSession {

    private FtpIoSession ioSession;

    public DefaultFtpSession(final FtpIoSession ioSession) {
        this.ioSession = ioSession;
    }

    public boolean isLoggedIn() {
        return ioSession.isLoggedIn();
    }

    public DataConnectionFactory getDataConnection() {
        return ioSession.getDataConnection();
    }

    public FileSystemView getFileSystemView() {
        return ioSession.getFileSystemView();
    }

    public Date getConnectionTime() {
        return new Date(ioSession.getCreationTime());
    }

    public Date getLoginTime() {
        return ioSession.getLoginTime();
    }

    public Date getLastAccessTime() {
        return ioSession.getLastAccessTime();
    }

    public long getFileOffset() {
        return ioSession.getFileOffset();
    }

    public FtpFile getRenameFrom() {
        return ioSession.getRenameFrom();
    }

    public String getUserArgument() {
        return ioSession.getUserArgument();
    }

    public String getLanguage() {
        return ioSession.getLanguage();
    }

    public User getUser() {
        return ioSession.getUser();
    }

    public InetSocketAddress getClientAddress() {
        if (ioSession.getRemoteAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress) ioSession.getRemoteAddress());
        } else {
            return null;
        }
    }

    public Object getAttribute(final String name) {
        if (name.startsWith(FtpIoSession.ATTRIBUTE_PREFIX)) {
            throw new IllegalArgumentException("Illegal lookup of internal attribute");
        }
        return ioSession.getAttribute(name);
    }

    public void setAttribute(final String name, final Object value) {
        if (name.startsWith(FtpIoSession.ATTRIBUTE_PREFIX)) {
            throw new IllegalArgumentException("Illegal setting of internal attribute");
        }
        ioSession.setAttribute(name, value);
    }

    public int getMaxIdleTime() {
        return ioSession.getMaxIdleTime();
    }

    public void setMaxIdleTime(final int maxIdleTime) {
        ioSession.setMaxIdleTime(maxIdleTime);
    }

    public DataType getDataType() {
        return ioSession.getDataType();
    }

    public Structure getStructure() {
        return ioSession.getStructure();
    }

    public Certificate[] getClientCertificates() {
        return ioSession.getClientCertificates();
    }

    public InetSocketAddress getServerAddress() {
        if (ioSession.getLocalAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress) ioSession.getLocalAddress());
        } else {
            return null;
        }
    }

    public int getFailedLogins() {
        return ioSession.getFailedLogins();
    }

    public void removeAttribute(final String name) {
        if (name.startsWith(FtpIoSession.ATTRIBUTE_PREFIX)) {
            throw new IllegalArgumentException("Illegal removal of internal attribute");
        }
        ioSession.removeAttribute(name);
    }

    public void write(FtpReply reply) throws FtpException {
        ioSession.write(reply);
    }

    public boolean isSecure() {
        return ioSession.isSecure();
    }

    public void increaseWrittenDataBytes(int increment) {
        ioSession.increaseWrittenDataBytes(increment);
    }

    public void increaseReadDataBytes(int increment) {
        ioSession.increaseReadDataBytes(increment);
    }

    public UUID getSessionId() {
        return ioSession.getSessionId();
    }
}
