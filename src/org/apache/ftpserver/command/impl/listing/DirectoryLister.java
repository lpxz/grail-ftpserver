package org.apache.ftpserver.command.impl.listing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

public class DirectoryLister {

    private String traverseFiles(final List<FtpFile> files, final FileFilter filter, final FileFormater formater) {
        StringBuilder sb = new StringBuilder();
        sb.append(traverseFiles(files, filter, formater, true));
        sb.append(traverseFiles(files, filter, formater, false));
        return sb.toString();
    }

    private String traverseFiles(final List<FtpFile> files, final FileFilter filter, final FileFormater formater, boolean matchDirs) {
        StringBuilder sb = new StringBuilder();
        for (FtpFile file : files) {
            if (file == null) {
                continue;
            }
            if (filter == null || filter.accept(file)) {
                if (file.isDirectory() == matchDirs) {
                    sb.append(formater.format(file));
                }
            }
        }
        return sb.toString();
    }

    public String listFiles(final ListArgument argument, final FileSystemView fileSystemView, final FileFormater formater) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<FtpFile> files = listFiles(fileSystemView, argument.getFile());
        if (files != null) {
            FileFilter filter = null;
            if (!argument.hasOption('a')) {
                filter = new VisibleFileFilter();
            }
            if (argument.getPattern() != null) {
                filter = new RegexFileFilter(argument.getPattern(), filter);
            }
            sb.append(traverseFiles(files, filter, formater));
        }
        return sb.toString();
    }

    private List<FtpFile> listFiles(FileSystemView fileSystemView, String file) {
        List<FtpFile> files = null;
        try {
            FtpFile virtualFile = fileSystemView.getFile(file);
            if (virtualFile.isFile()) {
                files = new ArrayList<FtpFile>();
                files.add(virtualFile);
            } else {
                files = virtualFile.listFiles();
            }
        } catch (FtpException ex) {
        }
        return files;
    }
}
