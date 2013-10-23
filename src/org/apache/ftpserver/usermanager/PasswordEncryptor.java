package org.apache.ftpserver.usermanager;

public interface PasswordEncryptor {

    String encrypt(String password);

    boolean matches(String passwordToCheck, String storedPassword);
}
