package org.apache.ftpserver.commands.impl;

import junit.framework.TestCase;
import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.command.impl.NOOP;
import org.apache.ftpserver.command.impl.STOR;

public class DefaultCommandFactoryTest extends TestCase {

    public void testReturnFromDefaultUpper() {
        CommandFactory factory = new CommandFactoryFactory().createCommandFactory();
        Command command = factory.getCommand("STOR");
        assertNotNull(command);
        assertTrue(command instanceof STOR);
    }

    public void testReturnFromDefaultLower() {
        CommandFactory factory = new CommandFactoryFactory().createCommandFactory();
        Command command = factory.getCommand("stor");
        assertNotNull(command);
        assertTrue(command instanceof STOR);
    }

    public void testReturnFromDefaultUnknown() {
        CommandFactory factory = new CommandFactoryFactory().createCommandFactory();
        Command command = factory.getCommand("dummy");
        assertNull(command);
    }

    public void testOverride() {
        CommandFactoryFactory factoryFactory = new CommandFactoryFactory();
        factoryFactory.addCommand("stor", new NOOP());
        CommandFactory factory = factoryFactory.createCommandFactory();
        Command command = factory.getCommand("Stor");
        assertTrue(command instanceof NOOP);
    }

    public void testAppend() {
        CommandFactoryFactory factoryFactory = new CommandFactoryFactory();
        factoryFactory.addCommand("foo", new NOOP());
        CommandFactory factory = factoryFactory.createCommandFactory();
        assertTrue(factory.getCommand("FOO") instanceof NOOP);
        assertTrue(factory.getCommand("stor") instanceof STOR);
    }

    public void testAppendWithoutDefault() {
        CommandFactoryFactory factoryFactory = new CommandFactoryFactory();
        factoryFactory.addCommand("foo", new NOOP());
        factoryFactory.setUseDefaultCommands(false);
        CommandFactory factory = factoryFactory.createCommandFactory();
        assertTrue(factory.getCommand("FOO") instanceof NOOP);
        assertNull(factory.getCommand("stor"));
    }
}
