package org.apache.ftpserver.util;

import java.net.InetSocketAddress;
import junit.framework.TestCase;

public class SocketAddressEncoderTest extends TestCase {

    public void testEncodeLowPort() {
        InetSocketAddress address = new InetSocketAddress("localhost", 21);
        assertEquals("127,0,0,1,0,21", SocketAddressEncoder.encode(address));
    }

    public void testEncodeHighPort() {
        InetSocketAddress address = new InetSocketAddress("localhost", 21123);
        assertEquals("127,0,0,1,82,131", SocketAddressEncoder.encode(address));
    }

    public void testEncodeIpNumber() {
        InetSocketAddress address = new InetSocketAddress("1.2.3.4", 21);
        assertEquals("1,2,3,4,0,21", SocketAddressEncoder.encode(address));
    }

    public void testDecodeLowPort() throws Exception {
        InetSocketAddress address = new InetSocketAddress("1.2.3.4", 21);
        assertEquals(address, SocketAddressEncoder.decode("1,2,3,4,0,21"));
    }

    public void testDecodeHighPort() throws Exception {
        InetSocketAddress address = new InetSocketAddress("1.2.3.4", 21123);
        assertEquals(address, SocketAddressEncoder.decode("1,2,3,4,82,131"));
    }

    public void testDecodeTooFewTokens() throws Exception {
        try {
            SocketAddressEncoder.decode("1,2,3,4,82");
            fail("Must throw IllegalInetAddressException");
        } catch (IllegalInetAddressException e) {
        } catch (Exception e) {
            fail("Must throw IllegalInetAddressException");
        }
    }

    public void testDecodeTooManyTokens() throws Exception {
        try {
            SocketAddressEncoder.decode("1,2,3,4,82,1,2");
            fail("Must throw IllegalInetAddressException");
        } catch (IllegalInetAddressException e) {
        } catch (Exception e) {
            fail("Must throw IllegalInetAddressException");
        }
    }

    public void testDecodeToHighPort() {
        try {
            SocketAddressEncoder.decode("1,2,3,4,820,2");
            fail("Must throw IllegalPortException");
        } catch (IllegalPortException e) {
        } catch (Exception e) {
            fail("Must throw IllegalPortException");
        }
    }

    public void testDecodeIPTokenNotANumber() {
        try {
            SocketAddressEncoder.decode("foo,2,3,4,5,6");
            fail("Must throw IllegalInetAddressException");
        } catch (IllegalInetAddressException e) {
        } catch (Exception e) {
            fail("Must throw IllegalInetAddressException");
        }
    }

    public void testDecodePortTokenNotANumber() {
        try {
            SocketAddressEncoder.decode("1,2,3,4,foo,6");
            fail("Must throw IllegalPortException");
        } catch (IllegalPortException e) {
        } catch (Exception e) {
            fail("Must throw IllegalPortException");
        }
    }
}
