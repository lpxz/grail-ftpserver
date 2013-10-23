package org.apache.ftpserver.impl;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftpletcontainer.FtpletContainer;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.message.MessageResource;

public interface FtpServerContext extends FtpletContext {

    ConnectionConfig getConnectionConfig();

    MessageResource getMessageResource();

    FtpletContainer getFtpletContainer();

    Listener getListener(String name);

    Map<String, Listener> getListeners();

    CommandFactory getCommandFactory();

    void dispose();

    ThreadPoolExecutor getThreadPoolExecutor();
}
