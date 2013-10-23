package org.apache.ftpserver.listener.nio;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpLoggingFilter extends LoggingFilter {

    private boolean maskPassword = true;

    private final Logger logger;

    public FtpLoggingFilter() {
        this(FtpLoggingFilter.class.getName());
    }

    public FtpLoggingFilter(Class<?> clazz) {
        this(clazz.getName());
    }

    public FtpLoggingFilter(String name) {
        super(name);
        logger = LoggerFactory.getLogger(name);
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        String request = (String) message;
        String logMessage;
        if (maskPassword) {
            if (request.trim().toUpperCase().startsWith("PASS ")) {
                logMessage = "PASS *****";
            } else {
                logMessage = request;
            }
        } else {
            logMessage = request;
        }
        logger.info("RECEIVED: {}", logMessage);
        nextFilter.messageReceived(session, message);
    }

    public boolean isMaskPassword() {
        return maskPassword;
    }

    public void setMaskPassword(boolean maskPassword) {
        this.maskPassword = maskPassword;
    }
}
