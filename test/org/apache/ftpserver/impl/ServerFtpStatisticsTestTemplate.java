package org.apache.ftpserver.impl;

import java.util.Date;
import junit.framework.TestCase;

public abstract class ServerFtpStatisticsTestTemplate extends TestCase {

    public void testConnectionCount() {
        ServerFtpStatistics stats = createStatistics();
        assertEquals(0, stats.getTotalConnectionNumber());
        assertEquals(0, stats.getCurrentConnectionNumber());
        stats.setOpenConnection(new FtpIoSession(null, null));
        assertEquals(1, stats.getTotalConnectionNumber());
        assertEquals(1, stats.getCurrentConnectionNumber());
        stats.setOpenConnection(new FtpIoSession(null, null));
        assertEquals(2, stats.getTotalConnectionNumber());
        assertEquals(2, stats.getCurrentConnectionNumber());
        stats.setCloseConnection(new FtpIoSession(null, null));
        assertEquals(2, stats.getTotalConnectionNumber());
        assertEquals(1, stats.getCurrentConnectionNumber());
        stats.setCloseConnection(new FtpIoSession(null, null));
        assertEquals(2, stats.getTotalConnectionNumber());
        assertEquals(0, stats.getCurrentConnectionNumber());
        stats.setCloseConnection(new FtpIoSession(null, null));
        assertEquals(2, stats.getTotalConnectionNumber());
        assertEquals(0, stats.getCurrentConnectionNumber());
    }

    @SuppressWarnings("deprecation")
    public void testStartDateImmutable() {
        ServerFtpStatistics stats = createStatistics();
        Date date = stats.getStartTime();
        date.setYear(1);
        Date actual = stats.getStartTime();
        assertFalse(1 == actual.getYear());
    }

    protected abstract DefaultFtpStatistics createStatistics();
}
