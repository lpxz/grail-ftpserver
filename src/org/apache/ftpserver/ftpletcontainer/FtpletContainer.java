package org.apache.ftpserver.ftpletcontainer;

import java.util.Map;
import org.apache.ftpserver.ftplet.Ftplet;

public interface FtpletContainer extends Ftplet {

    Ftplet getFtplet(String name);

    Map<String, Ftplet> getFtplets();
}
