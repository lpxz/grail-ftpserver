package org.apache.ftpserver.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileRegularFilter implements FilenameFilter {

    private RegularExpr regularExpr = null;

    public FileRegularFilter(String pattern) {
        if ((pattern == null) || pattern.equals("") || pattern.equals("*")) {
            regularExpr = null;
        } else {
            regularExpr = new RegularExpr(pattern);
        }
    }

    public boolean accept(File dir, String name) {
        if (regularExpr == null) {
            return true;
        }
        return regularExpr.isMatch(name);
    }
}
