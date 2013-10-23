package org.apache.ftpserver.usermanager.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;

public class BaseUser implements User {

    private String name = null;

    private String password = null;

    private int maxIdleTimeSec = 0;

    private String homeDir = null;

    private boolean isEnabled = true;

    private List<Authority> authorities = new ArrayList<Authority>();

    public BaseUser() {
    }

    public BaseUser(User user) {
        name = user.getName();
        password = user.getPassword();
        authorities = user.getAuthorities();
        maxIdleTimeSec = user.getMaxIdleTime();
        homeDir = user.getHomeDirectory();
        isEnabled = user.getEnabled();
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

    public void setPassword(String pass) {
        password = pass;
    }

    public List<Authority> getAuthorities() {
        if (authorities != null) {
            return Collections.unmodifiableList(authorities);
        } else {
            return null;
        }
    }

    public void setAuthorities(List<Authority> authorities) {
        if (authorities != null) {
            this.authorities = Collections.unmodifiableList(authorities);
        } else {
            this.authorities = null;
        }
    }

    public int getMaxIdleTime() {
        return maxIdleTimeSec;
    }

    public void setMaxIdleTime(int idleSec) {
        maxIdleTimeSec = idleSec;
        if (maxIdleTimeSec < 0) {
            maxIdleTimeSec = 0;
        }
    }

    public boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enb) {
        isEnabled = enb;
    }

    public String getHomeDirectory() {
        return homeDir;
    }

    public void setHomeDirectory(String home) {
        homeDir = home;
    }

    public String toString() {
        return name;
    }

    public AuthorizationRequest authorize(AuthorizationRequest request) {
        if (authorities == null) {
            return null;
        }
        boolean someoneCouldAuthorize = false;
        for (Authority authority : authorities) {
            if (authority.canAuthorize(request)) {
                someoneCouldAuthorize = true;
                request = authority.authorize(request);
                if (request == null) {
                    return null;
                }
            }
        }
        if (someoneCouldAuthorize) {
            return request;
        } else {
            return null;
        }
    }

    public List<Authority> getAuthorities(Class<? extends Authority> clazz) {
        List<Authority> selected = new ArrayList<Authority>();
        for (Authority authority : authorities) {
            if (authority.getClass().equals(clazz)) {
                selected.add(authority);
            }
        }
        return selected;
    }
}
