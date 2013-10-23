package org.apache.ftpserver.impl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;

public class DefaultFtpStatistics implements ServerFtpStatistics {

    private StatisticsObserver observer = null;

    private FileObserver fileObserver = null;

    private Date startTime = new Date();

    private AtomicInteger uploadCount = new AtomicInteger(0);

    private AtomicInteger downloadCount = new AtomicInteger(0);

    private AtomicInteger deleteCount = new AtomicInteger(0);

    private AtomicInteger mkdirCount = new AtomicInteger(0);

    private AtomicInteger rmdirCount = new AtomicInteger(0);

    private AtomicInteger currLogins = new AtomicInteger(0);

    private AtomicInteger totalLogins = new AtomicInteger(0);

    private AtomicInteger totalFailedLogins = new AtomicInteger(0);

    private AtomicInteger currAnonLogins = new AtomicInteger(0);

    private AtomicInteger totalAnonLogins = new AtomicInteger(0);

    private AtomicInteger currConnections = new AtomicInteger(0);

    private AtomicInteger totalConnections = new AtomicInteger(0);

    private AtomicLong bytesUpload = new AtomicLong(0L);

    private AtomicLong bytesDownload = new AtomicLong(0L);

    private static class UserLogins {

        private Map<InetAddress, AtomicInteger> perAddress = new ConcurrentHashMap<InetAddress, AtomicInteger>();

        public UserLogins(InetAddress address) {
            totalLogins = new AtomicInteger(1);
            perAddress.put(address, new AtomicInteger(1));
        }

        public AtomicInteger loginsFromInetAddress(InetAddress address) {
            AtomicInteger logins = perAddress.get(address);
            if (logins == null) {
                logins = new AtomicInteger(0);
                perAddress.put(address, logins);
            }
            return logins;
        }

        public AtomicInteger totalLogins;
    }

    private Map<String, UserLogins> userLoginTable = new ConcurrentHashMap<String, UserLogins>();

    public static final String LOGIN_NUMBER = "login_number";

    public void setObserver(final StatisticsObserver observer) {
        this.observer = observer;
    }

    public void setFileObserver(final FileObserver observer) {
        fileObserver = observer;
    }

    public Date getStartTime() {
        if (startTime != null) {
            return (Date) startTime.clone();
        } else {
            return null;
        }
    }

    public int getTotalUploadNumber() {
        return uploadCount.get();
    }

    public int getTotalDownloadNumber() {
        return downloadCount.get();
    }

    public int getTotalDeleteNumber() {
        return deleteCount.get();
    }

    public long getTotalUploadSize() {
        return bytesUpload.get();
    }

    public long getTotalDownloadSize() {
        return bytesDownload.get();
    }

    public int getTotalDirectoryCreated() {
        return mkdirCount.get();
    }

    public int getTotalDirectoryRemoved() {
        return rmdirCount.get();
    }

    public int getTotalConnectionNumber() {
        return totalConnections.get();
    }

    public int getCurrentConnectionNumber() {
        return currConnections.get();
    }

    public int getTotalLoginNumber() {
        return totalLogins.get();
    }

    public int getTotalFailedLoginNumber() {
        return totalFailedLogins.get();
    }

    public int getCurrentLoginNumber() {
        return currLogins.get();
    }

    public int getTotalAnonymousLoginNumber() {
        return totalAnonLogins.get();
    }

    public int getCurrentAnonymousLoginNumber() {
        return currAnonLogins.get();
    }

    public synchronized int getCurrentUserLoginNumber(final User user) {
        UserLogins userLogins = userLoginTable.get(user.getName());
        if (userLogins == null) {
            return 0;
        } else {
            return userLogins.totalLogins.get();
        }
    }

    public synchronized int getCurrentUserLoginNumber(final User user, final InetAddress ipAddress) {
        UserLogins userLogins = userLoginTable.get(user.getName());
        if (userLogins == null) {
            return 0;
        } else {
            return userLogins.loginsFromInetAddress(ipAddress).get();
        }
    }

    public synchronized void setUpload(final FtpIoSession session, final FtpFile file, final long size) {
        uploadCount.incrementAndGet();
        bytesUpload.addAndGet(size);
        notifyUpload(session, file, size);
    }

    public synchronized void setDownload(final FtpIoSession session, final FtpFile file, final long size) {
        downloadCount.incrementAndGet();
        bytesDownload.addAndGet(size);
        notifyDownload(session, file, size);
    }

    public synchronized void setDelete(final FtpIoSession session, final FtpFile file) {
        deleteCount.incrementAndGet();
        notifyDelete(session, file);
    }

    public synchronized void setMkdir(final FtpIoSession session, final FtpFile file) {
        mkdirCount.incrementAndGet();
        notifyMkdir(session, file);
    }

    public synchronized void setRmdir(final FtpIoSession session, final FtpFile file) {
        rmdirCount.incrementAndGet();
        notifyRmdir(session, file);
    }

    public synchronized void setOpenConnection(final FtpIoSession session) {
        currConnections.incrementAndGet();
        totalConnections.incrementAndGet();
        notifyOpenConnection(session);
    }

    public synchronized void setCloseConnection(final FtpIoSession session) {
        if (currConnections.get() > 0) {
            currConnections.decrementAndGet();
        }
        notifyCloseConnection(session);
    }

    public synchronized void setLogin(final FtpIoSession session) {
        currLogins.incrementAndGet();
        totalLogins.incrementAndGet();
        User user = session.getUser();
        if ("anonymous".equals(user.getName())) {
            currAnonLogins.incrementAndGet();
            totalAnonLogins.incrementAndGet();
        }
        synchronized (user) {
            UserLogins statisticsTable = userLoginTable.get(user.getName());
            if (statisticsTable == null) {
                InetAddress address = null;
                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                }
                statisticsTable = new UserLogins(address);
                userLoginTable.put(user.getName(), statisticsTable);
            } else {
                statisticsTable.totalLogins.incrementAndGet();
                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    InetAddress address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                    statisticsTable.loginsFromInetAddress(address).incrementAndGet();
                }
            }
        }
        notifyLogin(session);
    }

    public synchronized void setLoginFail(final FtpIoSession session) {
        totalFailedLogins.incrementAndGet();
        notifyLoginFail(session);
    }

    public synchronized void setLogout(final FtpIoSession session) {
        User user = session.getUser();
        if (user == null) {
            return;
        }
        currLogins.decrementAndGet();
        if ("anonymous".equals(user.getName())) {
            currAnonLogins.decrementAndGet();
        }
        synchronized (user) {
            UserLogins userLogins = userLoginTable.get(user.getName());
            if (userLogins != null) {
                userLogins.totalLogins.decrementAndGet();
                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    InetAddress address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                    userLogins.loginsFromInetAddress(address).decrementAndGet();
                }
            }
        }
        notifyLogout(session);
    }

    private void notifyUpload(final FtpIoSession session, final FtpFile file, long size) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            observer.notifyUpload();
        }
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.notifyUpload(session, file, size);
        }
    }

    private void notifyDownload(final FtpIoSession session, final FtpFile file, final long size) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            observer.notifyDownload();
        }
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.notifyDownload(session, file, size);
        }
    }

    private void notifyDelete(final FtpIoSession session, final FtpFile file) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            observer.notifyDelete();
        }
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.notifyDelete(session, file);
        }
    }

    private void notifyMkdir(final FtpIoSession session, final FtpFile file) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            observer.notifyMkdir();
        }
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.notifyMkdir(session, file);
        }
    }

    private void notifyRmdir(final FtpIoSession session, final FtpFile file) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            observer.notifyRmdir();
        }
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.notifyRmdir(session, file);
        }
    }

    private void notifyOpenConnection(final FtpIoSession session) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            observer.notifyOpenConnection();
        }
    }

    private void notifyCloseConnection(final FtpIoSession session) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            observer.notifyCloseConnection();
        }
    }

    private void notifyLogin(final FtpIoSession session) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            User user = session.getUser();
            boolean anonymous = false;
            if (user != null) {
                String login = user.getName();
                anonymous = (login != null) && login.equals("anonymous");
            }
            observer.notifyLogin(anonymous);
        }
    }

    private void notifyLoginFail(final FtpIoSession session) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            if (session.getRemoteAddress() instanceof InetSocketAddress) {
                observer.notifyLoginFail(((InetSocketAddress) session.getRemoteAddress()).getAddress());
            }
        }
    }

    private void notifyLogout(final FtpIoSession session) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            User user = session.getUser();
            boolean anonymous = false;
            if (user != null) {
                String login = user.getName();
                anonymous = (login != null) && login.equals("anonymous");
            }
            observer.notifyLogout(anonymous);
        }
    }

    public synchronized void resetStatisticsCounters() {
        startTime = new Date();
        uploadCount.set(0);
        downloadCount.set(0);
        deleteCount.set(0);
        mkdirCount.set(0);
        rmdirCount.set(0);
        totalLogins.set(0);
        totalFailedLogins.set(0);
        totalAnonLogins.set(0);
        totalConnections.set(0);
        bytesUpload.set(0);
        bytesDownload.set(0);
    }
}
