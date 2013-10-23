package org.apache.ftpserver.usermanager.impl;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;

public class BaseUserTest extends TestCase {

    private static final Authority ALWAYS_ALLOW_AUTHORITY = new Authority() {

        public AuthorizationRequest authorize(AuthorizationRequest request) {
            return request;
        }

        public boolean canAuthorize(AuthorizationRequest request) {
            return true;
        }
    };

    private static final Authority NEVER_ALLOW_AUTHORITY = new Authority() {

        public AuthorizationRequest authorize(AuthorizationRequest request) {
            return null;
        }

        public boolean canAuthorize(AuthorizationRequest request) {
            return true;
        }
    };

    private static final Authority CANT_AUTHORITY = new Authority() {

        public AuthorizationRequest authorize(AuthorizationRequest request) {
            return null;
        }

        public boolean canAuthorize(AuthorizationRequest request) {
            return false;
        }
    };

    private static final AuthorizationRequest REQUEST = new AuthorizationRequest() {
    };

    private BaseUser user = new BaseUser();

    public void testAllow() {
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(ALWAYS_ALLOW_AUTHORITY);
        user.setAuthorities(authorities);
        assertSame(REQUEST, user.authorize(REQUEST));
    }

    public void testDisallow() {
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(NEVER_ALLOW_AUTHORITY);
        user.setAuthorities(authorities);
        assertNull(user.authorize(REQUEST));
    }

    public void testMultipleDisallowLast() {
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(ALWAYS_ALLOW_AUTHORITY);
        authorities.add(NEVER_ALLOW_AUTHORITY);
        user.setAuthorities(authorities);
        assertNull(user.authorize(REQUEST));
    }

    public void testMultipleAllowLast() {
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(NEVER_ALLOW_AUTHORITY);
        authorities.add(ALWAYS_ALLOW_AUTHORITY);
        user.setAuthorities(authorities);
        assertNull(user.authorize(REQUEST));
    }

    public void testNonCanAuthorize() {
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(CANT_AUTHORITY);
        user.setAuthorities(authorities);
        assertNull(user.authorize(REQUEST));
    }
}
