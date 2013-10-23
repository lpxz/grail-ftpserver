package org.apache.ftpserver.usermanager;

public class ClearTextPasswordEncryptor implements PasswordEncryptor {

    public String encrypt(String password) {
        return password;
    }

    public boolean matches(String passwordToCheck, String storedPassword) {
        if (storedPassword == null) {
            throw new NullPointerException("storedPassword can not be null");
        }
        if (passwordToCheck == null) {
            throw new NullPointerException("passwordToCheck can not be null");
        }
        return passwordToCheck.equals(storedPassword);
    }
}
