package org.apache.ftpserver.command.impl.listing;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.util.RegularExpr;

public class RegexFileFilter implements FileFilter {

    private RegularExpr regex;

    private FileFilter wrappedFilter;

    public RegexFileFilter(String regex) {
        this.regex = new RegularExpr(regex);
    }

    public RegexFileFilter(String regex, FileFilter wrappedFilter) {
        this(regex);
        this.wrappedFilter = wrappedFilter;
    }

    public boolean accept(FtpFile file) {
        if (wrappedFilter != null && !wrappedFilter.accept(file)) {
            return false;
        }
        return regex.isMatch(file.getName());
    }
}
