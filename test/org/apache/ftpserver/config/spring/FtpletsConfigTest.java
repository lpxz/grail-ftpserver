package org.apache.ftpserver.config.spring;

import java.util.Map;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.impl.DefaultFtpServer;

public class FtpletsConfigTest extends SpringConfigTestTemplate {

    private static final String USER_FILE_PATH = "src/test/resources/users.properties";

    private Map<String, Ftplet> createFtplets(String config) {
        DefaultFtpServer server = (DefaultFtpServer) createServer("<ftplets>" + config + "</ftplets>");
        return server.getFtplets();
    }

    public void testFtplet() throws Throwable {
        Map<String, Ftplet> ftplets = createFtplets("<ftplet name=\"foo\">" + "<beans:bean class=\"" + TestFtplet.class.getName() + "\">" + "<beans:property name=\"foo\" value=\"123\" />" + "</beans:bean></ftplet>");
        assertEquals(1, ftplets.size());
        assertEquals(123, ((TestFtplet) ftplets.get("foo")).getFoo());
    }

    public void testFtpletMap() throws Throwable {
        Map<String, Ftplet> ftplets = createFtplets("<beans:map>" + "<beans:entry key=\"foo\">" + "<beans:bean class=\"" + TestFtplet.class.getName() + "\">" + "<beans:property name=\"foo\" value=\"123\" />" + "</beans:bean>" + "</beans:entry></beans:map>");
        assertEquals(1, ftplets.size());
        assertEquals(123, ((TestFtplet) ftplets.get("foo")).getFoo());
    }
}
