package org.apache.ftpserver.ftpletcontainer;

import java.io.IOException;

public class FtpLetThrowRuntimeExceptionTest extends FtpLetThrowFtpExceptionTest {

    protected void throwException() throws IOException {
        throw new IOException();
    }
}
