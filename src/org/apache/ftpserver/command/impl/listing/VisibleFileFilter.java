package org.apache.ftpserver.command.impl.listing;

import org.apache.ftpserver.ftplet.FtpFile;

public class VisibleFileFilter implements FileFilter {

    private FileFilter wrappedFilter;

    public VisibleFileFilter() {
    }

    public VisibleFileFilter(FileFilter wrappedFilter) {
        this.wrappedFilter = wrappedFilter;
    }

    public boolean accept(FtpFile file) {
        if (wrappedFilter != null && !wrappedFilter.accept(file)) {
            return false;
        }
        return !file.isHidden();
    }
}
