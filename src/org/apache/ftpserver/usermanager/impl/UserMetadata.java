package org.apache.ftpserver.usermanager.impl;

import java.net.InetAddress;
import java.security.cert.Certificate;

public class UserMetadata {

    private Certificate[] certificateChain;

    private InetAddress inetAddress;

    public Certificate[] getCertificateChain() {
        if (certificateChain != null) {
            return certificateChain.clone();
        } else {
            return null;
        }
    }

    public void setCertificateChain(final Certificate[] certificateChain) {
        if (certificateChain != null) {
            this.certificateChain = certificateChain.clone();
        } else {
            this.certificateChain = null;
        }
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
}
