package org.apache.ftpserver.command;

import java.io.IOException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;

public interface Command {

    void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException;
}
