package org.apache.ftpserver.impl;

import org.apache.ftpserver.ftplet.FtpRequest;

public class DefaultFtpRequest implements FtpRequest {

    private String line;

    private String command;

    private String argument;

    public DefaultFtpRequest(final String requestLine) {
        parse(requestLine);
    }

    private void parse(final String lineToParse) {
        line = lineToParse.trim();
        command = null;
        argument = null;
        int spInd = line.indexOf(' ');
        if (spInd != -1) {
            argument = line.substring(spInd + 1);
            if (argument.equals("")) {
                argument = null;
            }
            command = line.substring(0, spInd).toUpperCase();
        } else {
            command = line.toUpperCase();
        }
        if ((command.length() > 0) && (command.charAt(0) == 'X')) {
            command = command.substring(1);
        }
    }

    public String getCommand() {
        return command;
    }

    public String getArgument() {
        return argument;
    }

    public String getRequestLine() {
        return line;
    }

    public boolean hasArgument() {
        return getArgument() != null;
    }

    public String toString() {
        return getRequestLine();
    }
}
