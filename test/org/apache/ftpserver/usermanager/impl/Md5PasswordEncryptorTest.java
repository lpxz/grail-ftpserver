package org.apache.ftpserver.usermanager.impl;

import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;

public class Md5PasswordEncryptorTest extends ClearTextPasswordEncryptorTest {

    @Override
    protected PasswordEncryptor createPasswordEncryptor() {
        return new Md5PasswordEncryptor();
    }

    public void testMatches() {
        PasswordEncryptor encryptor = createPasswordEncryptor();
        assertTrue(encryptor.matches("foo", "ACBD18DB4CC2F85CEDEF654FCCC4A4D8"));
        assertTrue(encryptor.matches("foo", "acbd18DB4CC2F85CEDEF654FCCC4A4D8"));
        assertFalse(encryptor.matches("foo", "bar"));
    }
}
