package org.apache.ftpserver.impl;

public class FtpStatisticsImplTest extends ServerFtpStatisticsTestTemplate {

    protected DefaultFtpStatistics createStatistics() {
        return new DefaultFtpStatistics();
    }
}
