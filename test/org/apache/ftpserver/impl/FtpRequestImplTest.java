package org.apache.ftpserver.impl;

import junit.framework.TestCase;

public class FtpRequestImplTest extends TestCase {

    public void testCommandOnly() {
        DefaultFtpRequest request = new DefaultFtpRequest("foo");
        assertEquals("foo", request.getRequestLine());
        assertEquals("FOO", request.getCommand());
        assertFalse(request.hasArgument());
        assertNull(request.getArgument());
    }

    public void testCommandWithLeadingWhitespace() {
        DefaultFtpRequest request = new DefaultFtpRequest("\rfoo");
        assertEquals("foo", request.getRequestLine());
        assertEquals("FOO", request.getCommand());
        assertFalse(request.hasArgument());
        assertNull(request.getArgument());
    }

    public void testCommandWithTrailingWhitespace() {
        DefaultFtpRequest request = new DefaultFtpRequest("foo\r");
        assertEquals("foo", request.getRequestLine());
        assertEquals("FOO", request.getCommand());
        assertFalse(request.hasArgument());
        assertNull(request.getArgument());
    }

    public void testCommandAndSingleArgument() {
        DefaultFtpRequest request = new DefaultFtpRequest("foo bar");
        assertEquals("foo bar", request.getRequestLine());
        assertEquals("FOO", request.getCommand());
        assertTrue(request.hasArgument());
        assertEquals("bar", request.getArgument());
    }

    public void testCommandAndMultipleArguments() {
        DefaultFtpRequest request = new DefaultFtpRequest("foo bar baz");
        assertEquals("foo bar baz", request.getRequestLine());
        assertEquals("FOO", request.getCommand());
        assertTrue(request.hasArgument());
        assertEquals("bar baz", request.getArgument());
    }
}
