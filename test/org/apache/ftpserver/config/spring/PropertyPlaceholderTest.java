package org.apache.ftpserver.config.spring;

import junit.framework.TestCase;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class PropertyPlaceholderTest extends TestCase {

    public void test() throws Throwable {
        System.setProperty("port2", "3333");
        FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext("src/test/resources/spring-config/config-property-placeholder.xml");
        DefaultFtpServer server = (DefaultFtpServer) ctx.getBean("server");
        assertEquals(2222, server.getListener("listener0").getPort());
        assertEquals(3333, server.getListener("listener1").getPort());
    }
}
