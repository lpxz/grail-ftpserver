package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.ssl.ClientAuth;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.mina.filter.ssl.SslFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AUTH extends AbstractCommand {

    private static final String SSL_SESSION_FILTER_NAME = "sslSessionFilter";

    private final Logger LOG = LoggerFactory.getLogger(AUTH.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException, FtpException {
        session.resetState();
        if (!request.hasArgument()) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "AUTH", null));
            return;
        }
        if (session.getListener().getSslConfiguration() == null) {
            session.write(LocalizedFtpReply.translate(session, request, context, 431, "AUTH", null));
            return;
        }
        if (session.getFilterChain().contains(SslFilter.class)) {
            session.write(LocalizedFtpReply.translate(session, request, context, 534, "AUTH", null));
            return;
        }
        String authType = request.getArgument().toUpperCase();
        if (authType.equals("SSL")) {
            try {
                secureSession(session, "SSL");
                session.write(LocalizedFtpReply.translate(session, request, context, 234, "AUTH.SSL", null));
            } catch (FtpException ex) {
                throw ex;
            } catch (Exception ex) {
                LOG.warn("AUTH.execute()", ex);
                throw new FtpException("AUTH.execute()", ex);
            }
        } else if (authType.equals("TLS")) {
            try {
                secureSession(session, "TLS");
                session.write(LocalizedFtpReply.translate(session, request, context, 234, "AUTH.TLS", null));
            } catch (FtpException ex) {
                throw ex;
            } catch (Exception ex) {
                LOG.warn("AUTH.execute()", ex);
                throw new FtpException("AUTH.execute()", ex);
            }
        } else {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_502_COMMAND_NOT_IMPLEMENTED, "AUTH", null));
        }
    }

    private void secureSession(final FtpIoSession session, final String type) throws GeneralSecurityException, FtpException {
        SslConfiguration ssl = session.getListener().getSslConfiguration();
        if (ssl != null) {
            session.setAttribute(SslFilter.DISABLE_ENCRYPTION_ONCE);
            SslFilter sslFilter = new SslFilter(ssl.getSSLContext());
            if (ssl.getClientAuth() == ClientAuth.NEED) {
                sslFilter.setNeedClientAuth(true);
            } else if (ssl.getClientAuth() == ClientAuth.WANT) {
                sslFilter.setWantClientAuth(true);
            }
            if (ssl.getEnabledCipherSuites() != null) {
                sslFilter.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
            }
            session.getFilterChain().addFirst(SSL_SESSION_FILTER_NAME, sslFilter);
        } else {
            throw new FtpException("Socket factory SSL not configured");
        }
    }
}
