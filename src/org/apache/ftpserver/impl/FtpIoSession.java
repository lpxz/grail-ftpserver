package org.apache.ftpserver.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import org.apache.ftpserver.ftplet.DataType;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Structure;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.listener.Listener;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.filter.ssl.SslFilter;
import org.slf4j.LoggerFactory;

public class FtpIoSession implements IoSession {

    public static final String ATTRIBUTE_PREFIX = "org.apache.ftpserver.";

    private static final String ATTRIBUTE_USER_ARGUMENT = ATTRIBUTE_PREFIX + "user-argument";

    private static final String ATTRIBUTE_SESSION_ID = ATTRIBUTE_PREFIX + "session-id";

    private static final String ATTRIBUTE_USER = ATTRIBUTE_PREFIX + "user";

    private static final String ATTRIBUTE_LANGUAGE = ATTRIBUTE_PREFIX + "language";

    private static final String ATTRIBUTE_LOGIN_TIME = ATTRIBUTE_PREFIX + "login-time";

    private static final String ATTRIBUTE_DATA_CONNECTION = ATTRIBUTE_PREFIX + "data-connection";

    private static final String ATTRIBUTE_FILE_SYSTEM = ATTRIBUTE_PREFIX + "file-system";

    private static final String ATTRIBUTE_RENAME_FROM = ATTRIBUTE_PREFIX + "rename-from";

    private static final String ATTRIBUTE_FILE_OFFSET = ATTRIBUTE_PREFIX + "file-offset";

    private static final String ATTRIBUTE_DATA_TYPE = ATTRIBUTE_PREFIX + "data-type";

    private static final String ATTRIBUTE_STRUCTURE = ATTRIBUTE_PREFIX + "structure";

    private static final String ATTRIBUTE_FAILED_LOGINS = ATTRIBUTE_PREFIX + "failed-logins";

    private static final String ATTRIBUTE_LISTENER = ATTRIBUTE_PREFIX + "listener";

    private static final String ATTRIBUTE_MAX_IDLE_TIME = ATTRIBUTE_PREFIX + "max-idle-time";

    private static final String ATTRIBUTE_LAST_ACCESS_TIME = ATTRIBUTE_PREFIX + "last-access-time";

    private static final String ATTRIBUTE_CACHED_REMOTE_ADDRESS = ATTRIBUTE_PREFIX + "cached-remote-address";

    private IoSession wrappedSession;

    private FtpServerContext context;

    private FtpReply lastReply = null;

    public CloseFuture close() {
        return wrappedSession.close();
    }

    public CloseFuture close(boolean immediately) {
        return wrappedSession.close(immediately);
    }

    public boolean containsAttribute(Object key) {
        return wrappedSession.containsAttribute(key);
    }

    @SuppressWarnings("deprecation")
    public Object getAttachment() {
        return wrappedSession.getAttachment();
    }

    public Object getAttribute(Object key) {
        return wrappedSession.getAttribute(key);
    }

    public Object getAttribute(Object key, Object defaultValue) {
        return wrappedSession.getAttribute(key, defaultValue);
    }

    public Set<Object> getAttributeKeys() {
        return wrappedSession.getAttributeKeys();
    }

    public int getBothIdleCount() {
        return wrappedSession.getBothIdleCount();
    }

    public CloseFuture getCloseFuture() {
        return wrappedSession.getCloseFuture();
    }

    public IoSessionConfig getConfig() {
        return wrappedSession.getConfig();
    }

    public long getCreationTime() {
        return wrappedSession.getCreationTime();
    }

    public IoFilterChain getFilterChain() {
        return wrappedSession.getFilterChain();
    }

    public IoHandler getHandler() {
        return wrappedSession.getHandler();
    }

    public long getId() {
        return wrappedSession.getId();
    }

    public int getIdleCount(IdleStatus status) {
        return wrappedSession.getIdleCount(status);
    }

    public long getLastBothIdleTime() {
        return wrappedSession.getLastBothIdleTime();
    }

    public long getLastIdleTime(IdleStatus status) {
        return wrappedSession.getLastIdleTime(status);
    }

    public long getLastIoTime() {
        return wrappedSession.getLastIoTime();
    }

    public long getLastReadTime() {
        return wrappedSession.getLastReadTime();
    }

    public long getLastReaderIdleTime() {
        return wrappedSession.getLastReaderIdleTime();
    }

    public long getLastWriteTime() {
        return wrappedSession.getLastWriteTime();
    }

    public long getLastWriterIdleTime() {
        return wrappedSession.getLastWriterIdleTime();
    }

    public SocketAddress getLocalAddress() {
        return wrappedSession.getLocalAddress();
    }

    public long getReadBytes() {
        return wrappedSession.getReadBytes();
    }

    public double getReadBytesThroughput() {
        return wrappedSession.getReadBytesThroughput();
    }

    public long getReadMessages() {
        return wrappedSession.getReadMessages();
    }

    public double getReadMessagesThroughput() {
        return wrappedSession.getReadMessagesThroughput();
    }

    public int getReaderIdleCount() {
        return wrappedSession.getReaderIdleCount();
    }

    public SocketAddress getRemoteAddress() {
        SocketAddress address = wrappedSession.getRemoteAddress();
        if (address == null && containsAttribute(ATTRIBUTE_CACHED_REMOTE_ADDRESS)) {
            return (SocketAddress) getAttribute(ATTRIBUTE_CACHED_REMOTE_ADDRESS);
        } else {
            setAttribute(ATTRIBUTE_CACHED_REMOTE_ADDRESS, address);
            return address;
        }
    }

    public long getScheduledWriteBytes() {
        return wrappedSession.getScheduledWriteBytes();
    }

    public int getScheduledWriteMessages() {
        return wrappedSession.getScheduledWriteMessages();
    }

    public IoService getService() {
        return wrappedSession.getService();
    }

    public SocketAddress getServiceAddress() {
        return wrappedSession.getServiceAddress();
    }

    public TransportMetadata getTransportMetadata() {
        return wrappedSession.getTransportMetadata();
    }

    public int getWriterIdleCount() {
        return wrappedSession.getWriterIdleCount();
    }

    public long getWrittenBytes() {
        return wrappedSession.getWrittenBytes();
    }

    public double getWrittenBytesThroughput() {
        return wrappedSession.getWrittenBytesThroughput();
    }

    public long getWrittenMessages() {
        return wrappedSession.getWrittenMessages();
    }

    public double getWrittenMessagesThroughput() {
        return wrappedSession.getWrittenMessagesThroughput();
    }

    public boolean isClosing() {
        return wrappedSession.isClosing();
    }

    public boolean isConnected() {
        return wrappedSession.isConnected();
    }

    public boolean isIdle(IdleStatus status) {
        return wrappedSession.isIdle(status);
    }

    public ReadFuture read() {
        return wrappedSession.read();
    }

    public Object removeAttribute(Object key) {
        return wrappedSession.removeAttribute(key);
    }

    public boolean removeAttribute(Object key, Object value) {
        return wrappedSession.removeAttribute(key, value);
    }

    public boolean replaceAttribute(Object key, Object oldValue, Object newValue) {
        return wrappedSession.replaceAttribute(key, oldValue, newValue);
    }

    public void resumeRead() {
        wrappedSession.resumeRead();
    }

    public void resumeWrite() {
        wrappedSession.resumeWrite();
    }

    @SuppressWarnings("deprecation")
    public Object setAttachment(Object attachment) {
        return wrappedSession.setAttachment(attachment);
    }

    public Object setAttribute(Object key) {
        return wrappedSession.setAttribute(key);
    }

    public Object setAttribute(Object key, Object value) {
        return wrappedSession.setAttribute(key, value);
    }

    public Object setAttributeIfAbsent(Object key) {
        return wrappedSession.setAttributeIfAbsent(key);
    }

    public Object setAttributeIfAbsent(Object key, Object value) {
        return wrappedSession.setAttributeIfAbsent(key, value);
    }

    public void suspendRead() {
        wrappedSession.suspendRead();
    }

    public void suspendWrite() {
        wrappedSession.suspendWrite();
    }

    public WriteFuture write(Object message) {
        WriteFuture future = wrappedSession.write(message);
        this.lastReply = (FtpReply) message;
        return future;
    }

    public WriteFuture write(Object message, SocketAddress destination) {
        WriteFuture future = wrappedSession.write(message, destination);
        this.lastReply = (FtpReply) message;
        return future;
    }

    public void resetState() {
        removeAttribute(ATTRIBUTE_RENAME_FROM);
        removeAttribute(ATTRIBUTE_FILE_OFFSET);
    }

    public synchronized ServerDataConnectionFactory getDataConnection() {
        if (containsAttribute(ATTRIBUTE_DATA_CONNECTION)) {
            return (ServerDataConnectionFactory) getAttribute(ATTRIBUTE_DATA_CONNECTION);
        } else {
            IODataConnectionFactory dataCon = new IODataConnectionFactory(context, this);
            dataCon.setServerControlAddress(((InetSocketAddress) getLocalAddress()).getAddress());
            setAttribute(ATTRIBUTE_DATA_CONNECTION, dataCon);
            return dataCon;
        }
    }

    public FileSystemView getFileSystemView() {
        return (FileSystemView) getAttribute(ATTRIBUTE_FILE_SYSTEM);
    }

    public User getUser() {
        return (User) getAttribute(ATTRIBUTE_USER);
    }

    public boolean isLoggedIn() {
        return containsAttribute(ATTRIBUTE_USER);
    }

    public Listener getListener() {
        return (Listener) getAttribute(ATTRIBUTE_LISTENER);
    }

    public void setListener(Listener listener) {
        setAttribute(ATTRIBUTE_LISTENER, listener);
    }

    public FtpSession getFtpletSession() {
        return new DefaultFtpSession(this);
    }

    public String getLanguage() {
        return (String) getAttribute(ATTRIBUTE_LANGUAGE);
    }

    public void setLanguage(String language) {
        setAttribute(ATTRIBUTE_LANGUAGE, language);
    }

    public String getUserArgument() {
        return (String) getAttribute(ATTRIBUTE_USER_ARGUMENT);
    }

    public void setUser(User user) {
        setAttribute(ATTRIBUTE_USER, user);
    }

    public void setUserArgument(String userArgument) {
        setAttribute(ATTRIBUTE_USER_ARGUMENT, userArgument);
    }

    public int getMaxIdleTime() {
        return (Integer) getAttribute(ATTRIBUTE_MAX_IDLE_TIME, 0);
    }

    public void setMaxIdleTime(int maxIdleTime) {
        setAttribute(ATTRIBUTE_MAX_IDLE_TIME, maxIdleTime);
        int listenerTimeout = getListener().getIdleTimeout();
        if (listenerTimeout <= 0 || (maxIdleTime > 0 && maxIdleTime < listenerTimeout)) {
            wrappedSession.getConfig().setBothIdleTime(maxIdleTime);
        }
    }

    public synchronized void increaseFailedLogins() {
        int failedLogins = (Integer) getAttribute(ATTRIBUTE_FAILED_LOGINS, 0);
        failedLogins++;
        setAttribute(ATTRIBUTE_FAILED_LOGINS, failedLogins);
    }

    public int getFailedLogins() {
        return (Integer) getAttribute(ATTRIBUTE_FAILED_LOGINS, 0);
    }

    public void setLogin(FileSystemView fsview) {
        setAttribute(ATTRIBUTE_LOGIN_TIME, new Date());
        setAttribute(ATTRIBUTE_FILE_SYSTEM, fsview);
    }

    public void reinitialize() {
        logoutUser();
        removeAttribute(ATTRIBUTE_USER);
        removeAttribute(ATTRIBUTE_USER_ARGUMENT);
        removeAttribute(ATTRIBUTE_LOGIN_TIME);
        removeAttribute(ATTRIBUTE_FILE_SYSTEM);
        removeAttribute(ATTRIBUTE_RENAME_FROM);
        removeAttribute(ATTRIBUTE_FILE_OFFSET);
    }

    public void logoutUser() {
        ServerFtpStatistics stats = ((ServerFtpStatistics) context.getFtpStatistics());
        if (stats != null) {
            stats.setLogout(this);
            LoggerFactory.getLogger(this.getClass()).debug("Statistics login decreased due to user logout");
        } else {
            LoggerFactory.getLogger(this.getClass()).warn("Statistics not available in session, can not decrease login  count");
        }
    }

    public void setFileOffset(long fileOffset) {
        setAttribute(ATTRIBUTE_FILE_OFFSET, fileOffset);
    }

    public void setRenameFrom(FtpFile renFr) {
        setAttribute(ATTRIBUTE_RENAME_FROM, renFr);
    }

    public FtpFile getRenameFrom() {
        return (FtpFile) getAttribute(ATTRIBUTE_RENAME_FROM);
    }

    public long getFileOffset() {
        return (Long) getAttribute(ATTRIBUTE_FILE_OFFSET, 0L);
    }

    public void setStructure(Structure structure) {
        setAttribute(ATTRIBUTE_STRUCTURE, structure);
    }

    public void setDataType(DataType dataType) {
        setAttribute(ATTRIBUTE_DATA_TYPE, dataType);
    }

    public UUID getSessionId() {
        synchronized (wrappedSession) {
            if (!wrappedSession.containsAttribute(ATTRIBUTE_SESSION_ID)) {
                wrappedSession.setAttribute(ATTRIBUTE_SESSION_ID, UUID.randomUUID());
            }
            return (UUID) wrappedSession.getAttribute(ATTRIBUTE_SESSION_ID);
        }
    }

    public FtpIoSession(IoSession wrappedSession, FtpServerContext context) {
        this.wrappedSession = wrappedSession;
        this.context = context;
    }

    public Structure getStructure() {
        return (Structure) getAttribute(ATTRIBUTE_STRUCTURE, Structure.FILE);
    }

    public DataType getDataType() {
        return (DataType) getAttribute(ATTRIBUTE_DATA_TYPE, DataType.ASCII);
    }

    public Date getLoginTime() {
        return (Date) getAttribute(ATTRIBUTE_LOGIN_TIME);
    }

    public Date getLastAccessTime() {
        return (Date) getAttribute(ATTRIBUTE_LAST_ACCESS_TIME);
    }

    public Certificate[] getClientCertificates() {
        if (getFilterChain().contains(SslFilter.class)) {
            SslFilter sslFilter = (SslFilter) getFilterChain().get(SslFilter.class);
            SSLSession sslSession = sslFilter.getSslSession(this);
            if (sslSession != null) {
                try {
                    return sslSession.getPeerCertificates();
                } catch (SSLPeerUnverifiedException e) {
                }
            }
        }
        return null;
    }

    public void updateLastAccessTime() {
        setAttribute(ATTRIBUTE_LAST_ACCESS_TIME, new Date());
    }

    public Object getCurrentWriteMessage() {
        return wrappedSession.getCurrentWriteMessage();
    }

    public WriteRequest getCurrentWriteRequest() {
        return wrappedSession.getCurrentWriteRequest();
    }

    public boolean isBothIdle() {
        return wrappedSession.isBothIdle();
    }

    public boolean isReaderIdle() {
        return wrappedSession.isReaderIdle();
    }

    public boolean isWriterIdle() {
        return wrappedSession.isWriterIdle();
    }

    public boolean isSecure() {
        return getFilterChain().contains(SslFilter.class);
    }

    public void increaseWrittenDataBytes(int increment) {
        if (wrappedSession instanceof AbstractIoSession) {
            ((AbstractIoSession) wrappedSession).increaseScheduledWriteBytes(increment);
            ((AbstractIoSession) wrappedSession).increaseWrittenBytes(increment, System.currentTimeMillis());
        }
    }

    public void increaseReadDataBytes(int increment) {
        if (wrappedSession instanceof AbstractIoSession) {
            ((AbstractIoSession) wrappedSession).increaseReadBytes(increment, System.currentTimeMillis());
        }
    }

    public FtpReply getLastReply() {
        return lastReply;
    }

    public WriteRequestQueue getWriteRequestQueue() {
        return wrappedSession.getWriteRequestQueue();
    }

    public boolean isReadSuspended() {
        return wrappedSession.isReadSuspended();
    }

    public boolean isWriteSuspended() {
        return wrappedSession.isWriteSuspended();
    }

    public void setCurrentWriteRequest(WriteRequest currentWriteRequest) {
        wrappedSession.setCurrentWriteRequest(currentWriteRequest);
    }

    public void updateThroughput(long currentTime, boolean force) {
        wrappedSession.updateThroughput(currentTime, force);
    }
}
