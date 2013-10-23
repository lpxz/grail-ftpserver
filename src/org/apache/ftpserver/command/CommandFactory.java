package org.apache.ftpserver.command;

public interface CommandFactory {

    Command getCommand(String commandName);
}
