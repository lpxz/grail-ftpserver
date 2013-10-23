package org.apache.ftpserver.usermanager;

import org.apache.ftpserver.ftplet.UserManager;

public interface UserManagerFactory {

    UserManager createUserManager();
}
