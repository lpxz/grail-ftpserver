package org.apache.ftpserver.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import junit.framework.TestCase;
import org.apache.ftpserver.util.IoUtils;

public class TestUtil {

    private static final int DEFAULT_PORT = 12321;

    public static File getBaseDir() {
        String basedir = System.getProperty("basedir");
        if (basedir != null) {
            return new File(basedir);
        } else {
            return new File(".");
        }
    }

    public static int findFreePort() throws IOException {
        return findFreePort(DEFAULT_PORT);
    }

    public static int findFreePort(int initPort) throws IOException {
        int port = -1;
        ServerSocket tmpSocket = null;
        try {
            tmpSocket = new ServerSocket(initPort);
            port = initPort;
            System.out.println("Using default port: " + port);
        } catch (IOException e) {
            System.out.println("Failed to use specified port");
            try {
                int attempts = 0;
                while (port < 1024 && attempts < 2000) {
                    attempts++;
                    tmpSocket = new ServerSocket(0);
                    port = tmpSocket.getLocalPort();
                }
            } catch (IOException e1) {
                throw new IOException("Failed to find a port to use for testing: " + e1.getMessage());
            }
        } finally {
            if (tmpSocket != null) {
                try {
                    tmpSocket.close();
                } catch (IOException e) {
                }
                tmpSocket = null;
            }
        }
        return port;
    }

    public static String[] getHostAddresses() throws Exception {
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        List<String> hostIps = new ArrayList<String>();
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = (NetworkInterface) nifs.nextElement();
            Enumeration<InetAddress> ips = nif.getInetAddresses();
            while (ips.hasMoreElements()) {
                InetAddress ip = (InetAddress) ips.nextElement();
                if (ip instanceof java.net.Inet4Address) {
                    hostIps.add(ip.getHostAddress());
                } else {
                }
            }
        }
        return hostIps.toArray(new String[0]);
    }

    public static InetAddress findNonLocalhostIp() throws Exception {
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = (NetworkInterface) nifs.nextElement();
            Enumeration<InetAddress> ips = nif.getInetAddresses();
            while (ips.hasMoreElements()) {
                InetAddress ip = (InetAddress) ips.nextElement();
                if (ip instanceof java.net.Inet4Address && !ip.isLoopbackAddress()) {
                    return ip;
                } else {
                }
            }
        }
        return null;
    }

    public static void writeDataToFile(File file, byte[] data) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
        } finally {
            IoUtils.close(fos);
        }
    }

    public static void assertFileEqual(byte[] expected, File file) throws Exception {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        try {
            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);
            IoUtils.copy(fis, baos, 1024);
            byte[] actual = baos.toByteArray();
            assertArraysEqual(expected, actual);
        } finally {
            IoUtils.close(fis);
            IoUtils.close(baos);
        }
    }

    public static void assertInArrays(Object expected, Object[] actual) {
        boolean found = false;
        for (int i = 0; i < actual.length; i++) {
            Object object = actual[i];
            if (object.equals(expected)) {
                found = true;
                break;
            }
        }
        if (!found) {
            TestCase.fail("Expected value not in array");
        }
    }

    public static void assertArraysEqual(byte[] expected, byte[] actual) {
        if (actual.length != expected.length) {
            TestCase.fail("Arrays are of different length");
        }
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] != expected[i]) {
                TestCase.fail("Arrays differ at position " + i);
            }
        }
    }
}
