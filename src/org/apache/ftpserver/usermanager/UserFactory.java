package org.apache.ftpserver.usermanager;

import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.BaseUser;

public class UserFactory {

    private String name = null;

    private String password = null;

    private int maxIdleTimeSec = 0;

    private String homeDir = null;

    private boolean isEnabled = true;

    private List<Authority> authorities = new ArrayList<Authority>();

    public User createUser() {
        BaseUser user = new BaseUser();
        user.setName(name);
        user.setPassword(password);
        user.setHomeDirectory(homeDir);
        user.setEnabled(isEnabled);
        user.setAuthorities(authorities);
        user.setMaxIdleTime(maxIdleTimeSec);
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxIdleTime() {
        return maxIdleTimeSec;
    }

    public void setMaxIdleTime(int maxIdleTimeSec) {
        this.maxIdleTimeSec = maxIdleTimeSec;
    }

    public String getHomeDirectory() {
        return homeDir;
    }

    public void setHomeDirectory(String homeDir) {
        this.homeDir = homeDir;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public List<? extends Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
