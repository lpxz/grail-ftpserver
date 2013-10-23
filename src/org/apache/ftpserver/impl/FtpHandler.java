package org.apache.ftpserver.impl;

import java.io.IOException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.listener.Listener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public interface FtpHandler {

    void init(FtpServerContext context, Listener listener);

    void sessionCreated(FtpIoSession session) throws Exception;

    void sessionOpened(FtpIoSession session) throws Exception;

    void sessionClosed(FtpIoSession session) throws Exception;

    void sessionIdle(FtpIoSession session, IdleStatus status) throws Exception;

    void exceptionCaught(FtpIoSession session, Throwable cause) throws Exception;

    void messageReceived(FtpIoSession session, FtpRequest request) throws Exception;

    void messageSent(FtpIoSession session, FtpReply reply) throws Exception;
}
