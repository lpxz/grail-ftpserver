package org.apache.ftpserver.usermanager;

import org.apache.ftpserver.util.EncryptUtils;

public class Md5PasswordEncryptor implements PasswordEncryptor {

    public String encrypt(String password) {
        return EncryptUtils.encryptMD5(password);
    }

    public boolean matches(String passwordToCheck, String storedPassword) {
        if (storedPassword == null) {
            throw new NullPointerException("storedPassword can not be null");
        }
        if (passwordToCheck == null) {
            throw new NullPointerException("passwordToCheck can not be null");
        }
        return encrypt(passwordToCheck).equalsIgnoreCase(storedPassword);
    }
}
