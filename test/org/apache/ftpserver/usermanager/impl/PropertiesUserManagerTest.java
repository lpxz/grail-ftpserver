package org.apache.ftpserver.usermanager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.UserManagerFactory;
import org.apache.ftpserver.util.IoUtils;

public class PropertiesUserManagerTest extends UserManagerTestTemplate {

    private static final File TEST_DIR = new File("test-tmp");

    private static final File USERS_FILE = new File(TEST_DIR, "users.props");

    private void createUserFile() throws IOException {
        Properties users = new Properties();
        users.setProperty("ftpserver.user.user1.userpassword", "pw1");
        users.setProperty("ftpserver.user.user1.homedirectory", "home");
        users.setProperty("ftpserver.user.user2.userpassword", "pw2");
        users.setProperty("ftpserver.user.user2.homedirectory", "home");
        users.setProperty("ftpserver.user.user2.writepermission", "true");
        users.setProperty("ftpserver.user.user2.enableflag", "false");
        users.setProperty("ftpserver.user.user2.idletime", "2");
        users.setProperty("ftpserver.user.user2.uploadrate", "5");
        users.setProperty("ftpserver.user.user2.downloadrate", "1");
        users.setProperty("ftpserver.user.user2.maxloginnumber", "3");
        users.setProperty("ftpserver.user.user2.maxloginperip", "4");
        users.setProperty("ftpserver.user.user3.userpassword", "");
        users.setProperty("ftpserver.user.user3.homedirectory", "home");
        FileOutputStream fos = new FileOutputStream(USERS_FILE);
        users.store(fos, null);
        fos.close();
    }

    protected UserManagerFactory createUserManagerFactory() throws FtpException {
        PropertiesUserManagerFactory um = new PropertiesUserManagerFactory();
        um.setFile(USERS_FILE);
        um.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        return um;
    }

    protected void setUp() throws Exception {
        TEST_DIR.mkdirs();
        createUserFile();
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        IoUtils.delete(TEST_DIR);
    }

    public void testRefresh() throws FileNotFoundException, IOException {
        Properties users = new Properties();
        users.load(new FileInputStream(USERS_FILE));
        String originalSetting = users.getProperty("ftpserver.user.user1.homedirectory");
        users.setProperty("ftpserver.user.user1.homedirectory", "refresh_test");
        users.store(new FileOutputStream(USERS_FILE), null);
        PropertiesUserManager pum = (PropertiesUserManager) userManager;
        pum.refresh();
        User modifiedUser = pum.getUserByName("user1");
        assertEquals("Home directory should have been \"refresh_test\" after call to refresh().", "refresh_test", modifiedUser.getHomeDirectory());
        users.load(new FileInputStream(USERS_FILE));
        users.setProperty("ftpserver.user.user1.homedirectory", originalSetting);
        users.store(new FileOutputStream(USERS_FILE), null);
        pum.refresh();
        modifiedUser = pum.getUserByName("user1");
        assertEquals("Home directory should have reset back to \"" + originalSetting + "\" after second call to refresh().", originalSetting, modifiedUser.getHomeDirectory());
    }
}
