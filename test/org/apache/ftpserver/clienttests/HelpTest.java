package org.apache.ftpserver.clienttests;

public class HelpTest extends ClientTestTemplate {

    protected void setUp() throws Exception {
        super.setUp();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testHelp() throws Exception {
        assertEquals(214, client.help());
        assertTrue(client.getReplyString().indexOf("The following commands are implemented") > -1);
    }

    public void testHelpForCWD() throws Exception {
        assertEquals(214, client.help("CWD"));
        assertTrue(client.getReplyString().indexOf("Syntax: CWD") > -1);
    }

    public void testHelpForCWDLowerCase() throws Exception {
        assertEquals(214, client.help("cwd"));
        assertTrue(client.getReplyString().indexOf("Syntax: CWD") > -1);
    }

    public void testHelpForUnknownCommand() throws Exception {
        assertEquals(214, client.help("foo"));
        assertTrue(client.getReplyString().indexOf("The following commands are implemented") > -1);
    }
}
