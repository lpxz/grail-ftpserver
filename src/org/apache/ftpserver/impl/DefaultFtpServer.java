package org.apache.ftpserver.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.message.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFtpServer implements FtpServer {

    private final Logger LOG = LoggerFactory.getLogger(DefaultFtpServer.class);

    private FtpServerContext serverContext;

    private boolean suspended = false;

    private boolean started = false;

    public DefaultFtpServer(final FtpServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void start() throws FtpException {
        if (serverContext == null) {
            throw new IllegalStateException("FtpServer has been stopped. Restart is not supported");
        }
        List<Listener> startedListeners = new ArrayList<Listener>();
        try {
            Map<String, Listener> listeners = serverContext.getListeners();
            for (Listener listener : listeners.values()) {
                listener.start(serverContext);
                startedListeners.add(listener);
            }
            serverContext.getFtpletContainer().init(serverContext);
            started = true;
            LOG.info("FTP server started");
        } catch (Exception e) {
            for (Listener listener : startedListeners) {
                listener.stop();
            }
            if (e instanceof FtpException) {
                throw (FtpException) e;
            } else {
                throw (RuntimeException) e;
            }
        }
    }

    public void stop() {
        if (serverContext == null) {
            return;
        }
        Map<String, Listener> listeners = serverContext.getListeners();
        for (Listener listener : listeners.values()) {
            listener.stop();
        }
        serverContext.getFtpletContainer().destroy();
        if (serverContext != null) {
            serverContext.dispose();
            serverContext = null;
        }
        started = false;
    }

    public boolean isStopped() {
        return !started;
    }

    public void suspend() {
        if (!started) {
            return;
        }
        LOG.debug("Suspending server");
        Map<String, Listener> listeners = serverContext.getListeners();
        for (Listener listener : listeners.values()) {
            listener.suspend();
        }
        suspended = true;
        LOG.debug("Server suspended");
    }

    public void resume() {
        if (!suspended) {
            return;
        }
        LOG.debug("Resuming server");
        Map<String, Listener> listeners = serverContext.getListeners();
        for (Listener listener : listeners.values()) {
            listener.resume();
        }
        suspended = false;
        LOG.debug("Server resumed");
    }

    public boolean isSuspended() {
        return suspended;
    }

    public FtpServerContext getServerContext() {
        return serverContext;
    }

    public Map<String, Listener> getListeners() {
        return getServerContext().getListeners();
    }

    public Listener getListener(final String name) {
        return getServerContext().getListener(name);
    }

    public Map<String, Ftplet> getFtplets() {
        return getServerContext().getFtpletContainer().getFtplets();
    }

    public UserManager getUserManager() {
        return getServerContext().getUserManager();
    }

    public FileSystemFactory getFileSystem() {
        return getServerContext().getFileSystemManager();
    }

    public CommandFactory getCommandFactory() {
        return getServerContext().getCommandFactory();
    }

    public MessageResource getMessageResource() {
        return getServerContext().getMessageResource();
    }

    public ConnectionConfig getConnectionConfig() {
        return getServerContext().getConnectionConfig();
    }
}
