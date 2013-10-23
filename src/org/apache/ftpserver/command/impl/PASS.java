package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.UserMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PASS extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(PASS.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        boolean success = false;
        ServerFtpStatistics stat = (ServerFtpStatistics) context.getFtpStatistics();
        try {
            session.resetState();
            String password = request.getArgument();
            String userName = session.getUserArgument();
            if (userName == null && session.getUser() == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS, "PASS", null));
                return;
            }
            if (session.isLoggedIn()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_202_COMMAND_NOT_IMPLEMENTED, "PASS", null));
                return;
            }
            boolean anonymous = userName != null && userName.equals("anonymous");
            if (anonymous) {
                int currAnonLogin = stat.getCurrentAnonymousLoginNumber();
                int maxAnonLogin = context.getConnectionConfig().getMaxAnonymousLogins();
                if (maxAnonLogin == 0) {
                    LOG.debug("Currently {} anonymous users logged in, unlimited allowed", currAnonLogin);
                } else {
                    LOG.debug("Currently {} out of {} anonymous users logged in", currAnonLogin, maxAnonLogin);
                }
                if (currAnonLogin >= maxAnonLogin) {
                    LOG.debug("Too many anonymous users logged in, user will be disconnected");
                    session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_421_SERVICE_NOT_AVAILABLE_CLOSING_CONTROL_CONNECTION, "PASS.anonymous", null));
                    return;
                }
            }
            int currLogin = stat.getCurrentLoginNumber();
            int maxLogin = context.getConnectionConfig().getMaxLogins();
            if (maxLogin == 0) {
                LOG.debug("Currently {} users logged in, unlimited allowed", currLogin);
            } else {
                LOG.debug("Currently {} out of {} users logged in", currLogin, maxLogin);
            }
            if (maxLogin != 0 && currLogin >= maxLogin) {
                LOG.debug("Too many users logged in, user will be disconnected");
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_421_SERVICE_NOT_AVAILABLE_CLOSING_CONTROL_CONNECTION, "PASS.login", null));
                return;
            }
            UserManager userManager = context.getUserManager();
            User authenticatedUser = null;
            try {
                UserMetadata userMetadata = new UserMetadata();
                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    userMetadata.setInetAddress(((InetSocketAddress) session.getRemoteAddress()).getAddress());
                }
                userMetadata.setCertificateChain(session.getClientCertificates());
                Authentication auth;
                if (anonymous) {
                    auth = new AnonymousAuthentication(userMetadata);
                } else {
                    auth = new UsernamePasswordAuthentication(userName, password, userMetadata);
                }
                authenticatedUser = userManager.authenticate(auth);
            } catch (AuthenticationFailedException e) {
                authenticatedUser = null;
                LOG.warn("User failed to log in");
            } catch (Exception e) {
                authenticatedUser = null;
                LOG.warn("PASS.execute()", e);
            }
            User oldUser = session.getUser();
            String oldUserArgument = session.getUserArgument();
            int oldMaxIdleTime = session.getMaxIdleTime();
            if (authenticatedUser != null) {
                if (!authenticatedUser.getEnabled()) {
                    session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_530_NOT_LOGGED_IN, "PASS", null));
                    return;
                }
                session.setUser(authenticatedUser);
                session.setUserArgument(null);
                session.setMaxIdleTime(authenticatedUser.getMaxIdleTime());
                success = true;
            } else {
                session.setUser(null);
            }
            if (!success) {
                session.setUser(oldUser);
                session.setUserArgument(oldUserArgument);
                session.setMaxIdleTime(oldMaxIdleTime);
                delayAfterLoginFailure(context.getConnectionConfig().getLoginFailureDelay());
                LOG.warn("Login failure - " + userName);
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_530_NOT_LOGGED_IN, "PASS", userName));
                stat.setLoginFail(session);
                session.increaseFailedLogins();
                int maxAllowedLoginFailues = context.getConnectionConfig().getMaxLoginFailures();
                if (maxAllowedLoginFailues != 0 && session.getFailedLogins() >= maxAllowedLoginFailues) {
                    LOG.warn("User exceeded the number of allowed failed logins, session will be closed");
                    session.close(false).awaitUninterruptibly(10000);
                }
                return;
            }
            FileSystemFactory fmanager = context.getFileSystemManager();
            FileSystemView fsview = fmanager.createFileSystemView(authenticatedUser);
            session.setLogin(fsview);
            stat.setLogin(session);
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_230_USER_LOGGED_IN, "PASS", userName));
            if (anonymous) {
                LOG.info("Anonymous login success - " + password);
            } else {
                LOG.info("Login success - " + userName);
            }
        } finally {
            if (!success) {
                session.reinitialize();
            }
        }
    }

    private void delayAfterLoginFailure(final int loginFailureDelay) {
        if (loginFailureDelay > 0) {
            LOG.debug("Waiting for " + loginFailureDelay + " milliseconds due to login failure");
            try {
                Thread.sleep(loginFailureDelay);
            } catch (InterruptedException e) {
            }
        }
    }
}
