package org.apache.ftpserver.usermanager;

import java.io.File;
import java.net.URL;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;

public class PropertiesUserManagerFactory implements UserManagerFactory {

    private String adminName = "admin";

    private File userDataFile;

    private URL userDataURL;

    private PasswordEncryptor passwordEncryptor = new Md5PasswordEncryptor();

    public UserManager createUserManager() {
        if (userDataURL != null) {
            return new PropertiesUserManager(passwordEncryptor, userDataURL, adminName);
        } else {
            return new PropertiesUserManager(passwordEncryptor, userDataFile, adminName);
        }
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public File getFile() {
        return userDataFile;
    }

    public void setFile(File propFile) {
        this.userDataFile = propFile;
    }

    public URL getUrl() {
        return userDataURL;
    }

    public void setUrl(URL userDataURL) {
        this.userDataURL = userDataURL;
    }

    public PasswordEncryptor getPasswordEncryptor() {
        return passwordEncryptor;
    }

    public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
    }
}
