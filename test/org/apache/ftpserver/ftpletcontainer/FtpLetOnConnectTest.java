package org.apache.ftpserver.ftpletcontainer;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.clienttests.ClientTestTemplate;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletResult;

public class FtpLetOnConnectTest extends ClientTestTemplate {

    private static final byte[] TESTDATA = "TESTDATA".getBytes();

    private static final byte[] DOUBLE_TESTDATA = "TESTDATATESTDATA".getBytes();

    private static final File TEST_FILE1 = new File(ROOT_DIR, "test1.txt");

    private static final File TEST_FILE2 = new File(ROOT_DIR, "test2.txt");

    private static final File TEST_DIR1 = new File(ROOT_DIR, "dir1");

    protected FtpletResult mockReturnValue = FtpletResult.DISCONNECT;

    protected void setUp() throws Exception {
        MockFtplet.callback = new MockFtpletCallback();
        initDirs();
        initServer();
    }

    protected FtpServerFactory createServer() throws Exception {
        FtpServerFactory server = super.createServer();
        Map<String, Ftplet> ftplets = new LinkedHashMap<String, Ftplet>();
        ftplets.put("f1", new MockFtplet());
        server.setFtplets(ftplets);
        return server;
    }

    public void testDisconnectOnConnect() throws Exception {
        MockFtplet.callback = new MockFtpletCallback() {

            public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
                return mockReturnValue;
            }
        };
        try {
            connectClient();
            fail("Must throw FTPConnectionClosedException");
        } catch (FTPConnectionClosedException e) {
        } catch (SocketException e) {
        }
    }

    public void testExceptionOnConnect() throws Exception {
        MockFtplet.callback = new MockFtpletCallback() {

            public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
                throw new FtpException();
            }
        };
        try {
            connectClient();
            fail("Must throw FTPConnectionClosedException");
        } catch (FTPConnectionClosedException e) {
        } catch (SocketException e) {
        }
    }

    protected void doConnect() throws Exception {
        client.connect("localhost", getListenerPort());
    }
}
