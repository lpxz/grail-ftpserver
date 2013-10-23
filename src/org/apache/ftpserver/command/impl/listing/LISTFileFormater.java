package org.apache.ftpserver.command.impl.listing;

import java.util.Arrays;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.util.DateUtils;

public class LISTFileFormater implements FileFormater {

    private static final char DELIM = ' ';

    private static final char[] NEWLINE = { '\r', '\n' };

    public String format(FtpFile file) {
        StringBuilder sb = new StringBuilder();
        sb.append(getPermission(file));
        sb.append(DELIM);
        sb.append(DELIM);
        sb.append(DELIM);
        sb.append(String.valueOf(file.getLinkCount()));
        sb.append(DELIM);
        sb.append(file.getOwnerName());
        sb.append(DELIM);
        sb.append(file.getGroupName());
        sb.append(DELIM);
        sb.append(getLength(file));
        sb.append(DELIM);
        sb.append(getLastModified(file));
        sb.append(DELIM);
        sb.append(file.getName());
        sb.append(NEWLINE);
        return sb.toString();
    }

    private String getLength(FtpFile file) {
        String initStr = "            ";
        long sz = 0;
        if (file.isFile()) {
            sz = file.getSize();
        }
        String szStr = String.valueOf(sz);
        if (szStr.length() > initStr.length()) {
            return szStr;
        }
        return initStr.substring(0, initStr.length() - szStr.length()) + szStr;
    }

    private String getLastModified(FtpFile file) {
        return DateUtils.getUnixDate(file.getLastModified());
    }

    private char[] getPermission(FtpFile file) {
        char permission[] = new char[10];
        Arrays.fill(permission, '-');
        permission[0] = file.isDirectory() ? 'd' : '-';
        permission[1] = file.isReadable() ? 'r' : '-';
        permission[2] = file.isWritable() ? 'w' : '-';
        permission[3] = file.isDirectory() ? 'x' : '-';
        return permission;
    }
}
