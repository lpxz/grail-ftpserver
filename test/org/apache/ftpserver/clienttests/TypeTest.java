package org.apache.ftpserver.clienttests;

import org.apache.commons.net.ftp.FTPReply;
import org.apache.ftpserver.ftplet.DataType;
import org.apache.ftpserver.ftplet.FtpSession;

public class TypeTest extends ClientTestTemplate {

    protected void setUp() throws Exception {
        super.setUp();
        client.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    private FtpSession getFtpSession() {
        return server.getListener("default").getActiveSessions().iterator().next().getFtpletSession();
    }

    public void testTypeIAndA() throws Exception {
        assertEquals(DataType.ASCII, getFtpSession().getDataType());
        assertTrue(FTPReply.isPositiveCompletion(client.type(2)));
        assertEquals(DataType.BINARY, getFtpSession().getDataType());
        assertTrue(FTPReply.isPositiveCompletion(client.type(0)));
        assertEquals(DataType.ASCII, getFtpSession().getDataType());
    }

    public void testUnknownType() throws Exception {
        assertEquals(DataType.ASCII, getFtpSession().getDataType());
        assertTrue(FTPReply.isNegativePermanent(client.type(4)));
        assertEquals(DataType.ASCII, getFtpSession().getDataType());
    }

    public void testTypeNoArgument() throws Exception {
        assertEquals(DataType.ASCII, getFtpSession().getDataType());
        assertTrue(FTPReply.isNegativePermanent(client.sendCommand("TYPE")));
        assertEquals(DataType.ASCII, getFtpSession().getDataType());
    }
}
