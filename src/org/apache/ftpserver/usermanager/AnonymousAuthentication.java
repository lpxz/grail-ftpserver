package org.apache.ftpserver.usermanager;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.usermanager.impl.UserMetadata;

public class AnonymousAuthentication implements Authentication {

    private UserMetadata userMetadata;

    public AnonymousAuthentication() {
    }

    public AnonymousAuthentication(UserMetadata userMetadata) {
        this.userMetadata = userMetadata;
    }

    public UserMetadata getUserMetadata() {
        return userMetadata;
    }
}
