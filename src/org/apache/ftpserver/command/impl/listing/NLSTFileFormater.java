package org.apache.ftpserver.command.impl.listing;

import org.apache.ftpserver.ftplet.FtpFile;

public class NLSTFileFormater implements FileFormater {

    private static final char[] NEWLINE = { '\r', '\n' };

    public String format(FtpFile file) {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getName());
        sb.append(NEWLINE);
        return sb.toString();
    }
}
