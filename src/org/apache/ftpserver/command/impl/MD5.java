package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5 extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(MD5.class);

    public void execute(final FtpIoSession session, final FtpServerContext context, final FtpRequest request) throws IOException {
        session.resetState();
        boolean isMMD5 = false;
        if ("MMD5".equals(request.getCommand())) {
            isMMD5 = true;
        }
        String argument = request.getArgument();
        if (argument == null || argument.trim().length() == 0) {
            session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_504_COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER, "MD5.invalid", null));
            return;
        }
        String[] fileNames = null;
        if (isMMD5) {
            fileNames = argument.split(",");
        } else {
            fileNames = new String[] { argument };
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i].trim();
            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception ex) {
                LOG.debug("Exception getting the file object: " + fileName, ex);
            }
            if (file == null) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_504_COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER, "MD5.invalid", fileName));
                return;
            }
            if (!file.isFile()) {
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_504_COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER, "MD5.invalid", fileName));
                return;
            }
            InputStream is = null;
            try {
                is = file.createInputStream(0);
                String md5Hash = md5(is);
                if (i > 0) {
                    sb.append(", ");
                }
                boolean nameHasSpaces = fileName.indexOf(' ') >= 0;
                if (nameHasSpaces) {
                    sb.append('"');
                }
                sb.append(fileName);
                if (nameHasSpaces) {
                    sb.append('"');
                }
                sb.append(' ');
                sb.append(md5Hash);
            } catch (NoSuchAlgorithmException e) {
                LOG.debug("MD5 algorithm not available", e);
                session.write(LocalizedFtpReply.translate(session, request, context, FtpReply.REPLY_502_COMMAND_NOT_IMPLEMENTED, "MD5.notimplemened", null));
            } finally {
                IoUtils.close(is);
            }
        }
        if (isMMD5) {
            session.write(LocalizedFtpReply.translate(session, request, context, 252, "MMD5", sb.toString()));
        } else {
            session.write(LocalizedFtpReply.translate(session, request, context, 251, "MD5", sb.toString()));
        }
    }

    private String md5(InputStream is) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        DigestInputStream dis = new DigestInputStream(is, digest);
        byte[] buffer = new byte[1024];
        int read = dis.read(buffer);
        while (read > -1) {
            read = dis.read(buffer);
        }
        return new String(encodeHex(dis.getMessageDigest().digest()));
    }

    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
}
