package org.apache.ftpserver.command.impl.listing;

import org.apache.ftpserver.ftplet.FtpFile;

public interface FileFormater {

    String format(FtpFile file);
}
