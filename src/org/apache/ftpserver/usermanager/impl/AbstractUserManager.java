package org.apache.ftpserver.usermanager.impl;

import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;

public abstract class AbstractUserManager implements UserManager {

    public static final String ATTR_LOGIN = "userid";

    public static final String ATTR_PASSWORD = "userpassword";

    public static final String ATTR_HOME = "homedirectory";

    public static final String ATTR_WRITE_PERM = "writepermission";

    public static final String ATTR_ENABLE = "enableflag";

    public static final String ATTR_MAX_IDLE_TIME = "idletime";

    public static final String ATTR_MAX_UPLOAD_RATE = "uploadrate";

    public static final String ATTR_MAX_DOWNLOAD_RATE = "downloadrate";

    public static final String ATTR_MAX_LOGIN_NUMBER = "maxloginnumber";

    public static final String ATTR_MAX_LOGIN_PER_IP = "maxloginperip";

    private String adminName;

    private PasswordEncryptor passwordEncryptor = new Md5PasswordEncryptor();

    public AbstractUserManager(String adminName, PasswordEncryptor passwordEncryptor) {
        this.adminName = adminName;
        this.passwordEncryptor = passwordEncryptor;
    }

    public String getAdminName() {
        return adminName;
    }

    public boolean isAdmin(String login) throws FtpException {
        return adminName.equals(login);
    }

    public PasswordEncryptor getPasswordEncryptor() {
        return passwordEncryptor;
    }
}
