package org.apache.ftpserver.config.spring;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;

public class MockUserManager implements UserManager {

    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        return null;
    }

    public void delete(String username) throws FtpException {
    }

    public boolean doesExist(String username) throws FtpException {
        return false;
    }

    public String getAdminName() throws FtpException {
        return null;
    }

    public String[] getAllUserNames() throws FtpException {
        return null;
    }

    public User getUserByName(String username) throws FtpException {
        return null;
    }

    public boolean isAdmin(String username) throws FtpException {
        return false;
    }

    public void save(User user) throws FtpException {
    }
}
