package org.apache.ftpserver.config.spring;

import java.io.File;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;

public class FileUserManagerConfigTest extends SpringConfigTestTemplate {

    private static final String USER_FILE_PATH = "src/test/resources/users.properties";

    private PropertiesUserManager createPropertiesUserManager(String config) {
        DefaultFtpServer server = (DefaultFtpServer) createServer(config);
        return (PropertiesUserManager) server.getUserManager();
    }

    public void testFile() throws Throwable {
        PropertiesUserManager um = createPropertiesUserManager("<file-user-manager file=\"" + USER_FILE_PATH + "\" />");
        assertEquals(new File("src/test/resources/users.properties"), um.getFile());
    }

    public void testMd5PasswordEncryptor() throws Throwable {
        PropertiesUserManager um = createPropertiesUserManager("<file-user-manager file=\"" + USER_FILE_PATH + "\" encrypt-passwords=\"md5\" />");
        assertTrue(um.getPasswordEncryptor() instanceof Md5PasswordEncryptor);
    }

    public void testTruePasswordEncryptor() throws Throwable {
        PropertiesUserManager um = createPropertiesUserManager("<file-user-manager file=\"" + USER_FILE_PATH + "\" encrypt-passwords=\"true\" />");
        assertTrue(um.getPasswordEncryptor() instanceof Md5PasswordEncryptor);
    }

    public void testNonePasswordEncryptor() throws Throwable {
        PropertiesUserManager um = createPropertiesUserManager("<file-user-manager file=\"" + USER_FILE_PATH + "\" encrypt-passwords=\"clear\" />");
        assertTrue(um.getPasswordEncryptor() instanceof ClearTextPasswordEncryptor);
    }

    public void testSaltedPasswordEncryptor() throws Throwable {
        PropertiesUserManager um = createPropertiesUserManager("<file-user-manager file=\"" + USER_FILE_PATH + "\" encrypt-passwords=\"salted\" />");
        assertTrue(um.getPasswordEncryptor() instanceof SaltedPasswordEncryptor);
    }

    public void testFalsePasswordEncryptor() throws Throwable {
        PropertiesUserManager um = createPropertiesUserManager("<file-user-manager file=\"" + USER_FILE_PATH + "\" encrypt-passwords=\"false\" />");
        assertTrue(um.getPasswordEncryptor() instanceof ClearTextPasswordEncryptor);
    }
}
