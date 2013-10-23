package org.apache.ftpserver.usermanager.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.UserManagerFactory;

public class VolatilePropertiesUserManagerTest extends UserManagerTestTemplate {

    protected UserManagerFactory createUserManagerFactory() throws FtpException {
        PropertiesUserManagerFactory um = new PropertiesUserManagerFactory();
        um.setFile(null);
        um.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        return um;
    }

    protected void setUp() throws Exception {
        super.setUp();
        BaseUser user1 = new BaseUser();
        user1.setName("user1");
        user1.setPassword("pw1");
        user1.setHomeDirectory("home");
        userManager.save(user1);
        BaseUser user2 = new BaseUser();
        user2.setName("user2");
        user2.setPassword("pw2");
        user2.setHomeDirectory("home");
        user2.setEnabled(false);
        user2.setMaxIdleTime(2);
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        authorities.add(new TransferRatePermission(1, 5));
        authorities.add(new ConcurrentLoginPermission(3, 4));
        user2.setAuthorities(authorities);
        userManager.save(user2);
        BaseUser user3 = new BaseUser();
        user3.setName("user3");
        user3.setPassword("");
        user3.setHomeDirectory("home");
        userManager.save(user3);
    }

    public void testSavePersistent() {
    }
}
