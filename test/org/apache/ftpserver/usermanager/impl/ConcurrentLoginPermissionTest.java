package org.apache.ftpserver.usermanager.impl;

import junit.framework.TestCase;

public class ConcurrentLoginPermissionTest extends TestCase {

    public void testCanAuthorize() {
        ConcurrentLoginPermission permission = new ConcurrentLoginPermission(4, 2);
        ConcurrentLoginRequest request = new ConcurrentLoginRequest(1, 1);
        assertTrue(permission.canAuthorize(request));
    }

    public void testAllowBoth() {
        ConcurrentLoginPermission permission = new ConcurrentLoginPermission(4, 2);
        ConcurrentLoginRequest request = new ConcurrentLoginRequest(1, 1);
        assertNotNull(permission.authorize(request));
    }

    public void testMaxValuesBoth() {
        ConcurrentLoginPermission permission = new ConcurrentLoginPermission(4, 2);
        ConcurrentLoginRequest request = new ConcurrentLoginRequest(4, 2);
        assertNotNull(permission.authorize(request));
    }

    public void testMaxLoginsTooLarge() {
        ConcurrentLoginPermission permission = new ConcurrentLoginPermission(4, 2);
        ConcurrentLoginRequest request = new ConcurrentLoginRequest(5, 2);
        assertNull(permission.authorize(request));
    }

    public void testMaxLoginsPerIPTooLarge() {
        ConcurrentLoginPermission permission = new ConcurrentLoginPermission(4, 2);
        ConcurrentLoginRequest request = new ConcurrentLoginRequest(3, 3);
        assertNull(permission.authorize(request));
    }

    public void testAllowAnyMaxLogins() {
        ConcurrentLoginPermission permission = new ConcurrentLoginPermission(0, 2);
        ConcurrentLoginRequest request = new ConcurrentLoginRequest(5, 2);
        assertNotNull(permission.authorize(request));
    }

    public void testAllowAnyMaxLoginsPerIP() {
        ConcurrentLoginPermission permission = new ConcurrentLoginPermission(4, 0);
        ConcurrentLoginRequest request = new ConcurrentLoginRequest(3, 3);
        assertNotNull(permission.authorize(request));
    }
}
