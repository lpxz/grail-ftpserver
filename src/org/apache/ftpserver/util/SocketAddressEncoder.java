package org.apache.ftpserver.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class SocketAddressEncoder {

    private static int convertAndValidateNumber(String s) {
        int i = Integer.parseInt(s);
        if (i < 0) {
            throw new IllegalArgumentException("Token can not be less than 0");
        } else if (i > 255) {
            throw new IllegalArgumentException("Token can not be larger than 255");
        }
        return i;
    }

    public static InetSocketAddress decode(String str) throws UnknownHostException {
        StringTokenizer st = new StringTokenizer(str, ",");
        if (st.countTokens() != 6) {
            throw new IllegalInetAddressException("Illegal amount of tokens");
        }
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(convertAndValidateNumber(st.nextToken()));
            sb.append('.');
            sb.append(convertAndValidateNumber(st.nextToken()));
            sb.append('.');
            sb.append(convertAndValidateNumber(st.nextToken()));
            sb.append('.');
            sb.append(convertAndValidateNumber(st.nextToken()));
        } catch (IllegalArgumentException e) {
            throw new IllegalInetAddressException(e.getMessage());
        }
        InetAddress dataAddr = InetAddress.getByName(sb.toString());
        int dataPort = 0;
        try {
            int hi = convertAndValidateNumber(st.nextToken());
            int lo = convertAndValidateNumber(st.nextToken());
            dataPort = (hi << 8) | lo;
        } catch (IllegalArgumentException ex) {
            throw new IllegalPortException("Invalid data port: " + str);
        }
        return new InetSocketAddress(dataAddr, dataPort);
    }

    public static String encode(InetSocketAddress address) {
        InetAddress servAddr = address.getAddress();
        int servPort = address.getPort();
        return servAddr.getHostAddress().replace('.', ',') + ',' + (servPort >> 8) + ',' + (servPort & 0xFF);
    }
}
