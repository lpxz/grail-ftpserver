package org.apache.ftpserver.command.impl.listing;

public class ListArgument {

    private String file;

    private String pattern;

    private char[] options;

    public ListArgument(String file, String pattern, char[] options) {
        this.file = file;
        this.pattern = pattern;
        if (options == null) {
            this.options = new char[0];
        } else {
            this.options = options.clone();
        }
    }

    public char[] getOptions() {
        return options.clone();
    }

    public String getPattern() {
        return pattern;
    }

    public boolean hasOption(char option) {
        for (int i = 0; i < options.length; i++) {
            if (option == options[i]) {
                return true;
            }
        }
        return false;
    }

    public String getFile() {
        return file;
    }
}
