package org.apache.ftpserver.usermanager;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.usermanager.impl.UserMetadata;

public class UsernamePasswordAuthentication implements Authentication {

    private String username;

    private String password;

    private UserMetadata userMetadata;

    public UsernamePasswordAuthentication(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public UsernamePasswordAuthentication(final String username, final String password, final UserMetadata userMetadata) {
        this(username, password);
        this.userMetadata = userMetadata;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public UserMetadata getUserMetadata() {
        return userMetadata;
    }
}
