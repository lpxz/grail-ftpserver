package org.apache.ftpserver.usermanager.impl;

import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;

public class SaltedPasswordEncryptorTest extends ClearTextPasswordEncryptorTest {

    @Override
    protected PasswordEncryptor createPasswordEncryptor() {
        return new SaltedPasswordEncryptor();
    }

    public void testMatches() {
        PasswordEncryptor encryptor = createPasswordEncryptor();
        assertTrue(encryptor.matches("foo", "71288966:F7B097C7BB2D02B8C2ACCE8A17284715"));
        assertTrue(encryptor.matches("foo", "71288966:f7b097C7BB2D02B8C2ACCE8A17284715"));
        assertFalse(encryptor.matches("foo", "bar:bar"));
    }
}
