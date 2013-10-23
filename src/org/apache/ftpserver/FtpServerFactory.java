package org.apache.ftpserver;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.ftpletcontainer.impl.DefaultFtpletContainer;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.impl.DefaultFtpServerContext;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.message.MessageResource;

public class FtpServerFactory {

    private DefaultFtpServerContext serverContext;

    public FtpServerFactory() {
        serverContext = new DefaultFtpServerContext();
    }

    public FtpServer createServer() {
        return new DefaultFtpServer(serverContext);
    }

    public Map<String, Listener> getListeners() {
        return serverContext.getListeners();
    }

    public Listener getListener(final String name) {
        return serverContext.getListener(name);
    }

    public void addListener(final String name, final Listener listener) {
        serverContext.addListener(name, listener);
    }

    public void setListeners(final Map<String, Listener> listeners) {
        serverContext.setListeners(listeners);
    }

    public Map<String, Ftplet> getFtplets() {
        return serverContext.getFtpletContainer().getFtplets();
    }

    public void setFtplets(final Map<String, Ftplet> ftplets) {
        serverContext.setFtpletContainer(new DefaultFtpletContainer(ftplets));
    }

    public UserManager getUserManager() {
        return serverContext.getUserManager();
    }

    public void setUserManager(final UserManager userManager) {
        serverContext.setUserManager(userManager);
    }

    public FileSystemFactory getFileSystem() {
        return serverContext.getFileSystemManager();
    }

    public void setFileSystem(final FileSystemFactory fileSystem) {
        serverContext.setFileSystemManager(fileSystem);
    }

    public CommandFactory getCommandFactory() {
        return serverContext.getCommandFactory();
    }

    public void setCommandFactory(final CommandFactory commandFactory) {
        serverContext.setCommandFactory(commandFactory);
    }

    public MessageResource getMessageResource() {
        return serverContext.getMessageResource();
    }

    public void setMessageResource(final MessageResource messageResource) {
        serverContext.setMessageResource(messageResource);
    }

    public ConnectionConfig getConnectionConfig() {
        return serverContext.getConnectionConfig();
    }

    public void setConnectionConfig(final ConnectionConfig connectionConfig) {
        serverContext.setConnectionConfig(connectionConfig);
    }
}
