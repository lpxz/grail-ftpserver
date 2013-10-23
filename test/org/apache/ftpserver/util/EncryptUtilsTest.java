package org.apache.ftpserver.util;

import junit.framework.TestCase;
import org.apache.ftpserver.ftplet.FtpException;

public class EncryptUtilsTest extends TestCase {

    public void testEncryptMd5() throws FtpException {
        assertEquals("21232F297A57A5A743894A0E4A801FC3", EncryptUtils.encryptMD5("admin"));
    }

    public void testEncryptSha() throws FtpException {
        assertEquals("D033E22AE348AEB5660FC2140AEC35850C4DA997", EncryptUtils.encryptSHA("admin"));
    }
}
