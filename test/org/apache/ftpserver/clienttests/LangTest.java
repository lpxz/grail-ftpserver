package org.apache.ftpserver.clienttests;

import java.util.Arrays;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.message.MessageResourceFactory;

public class LangTest extends ClientTestTemplate {

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        MessageResourceFactory factory = new MessageResourceFactory();
        factory.setLanguages(Arrays.asList(new String[] { "en", "zh-tw" }));
        server.setMessageResource(factory.createMessageResource());
        return server;
    }

    protected void setUp() throws Exception {
        super.setUp();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public void testLangDefault() throws Exception {
        assertEquals(200, client.sendCommand("LANG"));
    }

    public void testLangEn() throws Exception {
        assertEquals(200, client.sendCommand("LANG EN"));
    }

    public void testLangZHTW() throws Exception {
        assertEquals(200, client.sendCommand("LANG ZH-TW"));
    }

    public void testLangZHTWLowerCase() throws Exception {
        assertEquals(200, client.sendCommand("LANG zh-tw"));
    }

    public void testLangUnknownLang() throws Exception {
        assertEquals(504, client.sendCommand("LANG FOO"));
    }
}
