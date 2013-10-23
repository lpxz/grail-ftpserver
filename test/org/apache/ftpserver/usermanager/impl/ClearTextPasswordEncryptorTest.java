package org.apache.ftpserver.usermanager.impl;

import junit.framework.TestCase;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;

public class ClearTextPasswordEncryptorTest extends TestCase {

    protected PasswordEncryptor createPasswordEncryptor() {
        return new ClearTextPasswordEncryptor();
    }

    public void testMatches() {
        PasswordEncryptor encryptor = createPasswordEncryptor();
        assertTrue(encryptor.matches("foo", "foo"));
        assertFalse(encryptor.matches("foo", "bar"));
    }

    public void testMatchesNullPasswordToCheck() {
        PasswordEncryptor encryptor = createPasswordEncryptor();
        try {
            encryptor.matches(null, "bar");
            fail("Must throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    public void testMatchesNullStoredPassword() {
        PasswordEncryptor encryptor = createPasswordEncryptor();
        try {
            encryptor.matches("foo", null);
            fail("Must throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }
}
