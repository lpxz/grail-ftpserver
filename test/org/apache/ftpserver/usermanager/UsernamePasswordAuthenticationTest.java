package org.apache.ftpserver.usermanager;

import junit.framework.TestCase;

public class UsernamePasswordAuthenticationTest extends TestCase {

    public void testConstructor() {
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication("user", "pass");
        assertEquals("user", auth.getUsername());
        assertEquals("pass", auth.getPassword());
    }

    public void testConstructorNullUsername() {
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication(null, "pass");
        assertNull(auth.getUsername());
        assertEquals("pass", auth.getPassword());
    }

    public void testConstructorNullPassword() {
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication("user", null);
        assertEquals("user", auth.getUsername());
        assertNull(auth.getPassword());
    }
}
