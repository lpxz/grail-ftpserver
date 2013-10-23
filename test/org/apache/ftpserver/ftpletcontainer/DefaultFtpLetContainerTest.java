package org.apache.ftpserver.ftpletcontainer;

import java.util.Map;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftpletcontainer.impl.DefaultFtpletContainer;

public class DefaultFtpLetContainerTest extends FtpLetContainerTestTemplate {

    protected FtpletContainer createFtpletContainer(Map<String, Ftplet> ftplets) {
        return new DefaultFtpletContainer(ftplets);
    }
}
