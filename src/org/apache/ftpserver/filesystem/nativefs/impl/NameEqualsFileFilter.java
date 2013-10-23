package org.apache.ftpserver.filesystem.nativefs.impl;

import java.io.File;
import java.io.FileFilter;

public class NameEqualsFileFilter implements FileFilter {

    private String nameToMatch;

    private boolean caseInsensitive = false;

    public NameEqualsFileFilter(final String nameToMatch, final boolean caseInsensitive) {
        this.nameToMatch = nameToMatch;
        this.caseInsensitive = caseInsensitive;
    }

    public boolean accept(final File file) {
        if (caseInsensitive) {
            return file.getName().equalsIgnoreCase(nameToMatch);
        } else {
            return file.getName().equals(nameToMatch);
        }
    }
}
