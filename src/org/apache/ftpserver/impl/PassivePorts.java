package org.apache.ftpserver.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassivePorts {

    private Logger log = LoggerFactory.getLogger(PassivePorts.class);

    private static final int MAX_PORT = 65535;

    private static final Integer MAX_PORT_INTEGER = Integer.valueOf(MAX_PORT);

    private List<Integer> freeList;

    private Set<Integer> usedList;

    private Random r = new Random();

    private String passivePortsString;

    private boolean checkIfBound;

    private static Set<Integer> parse(final String portsString) {
        Set<Integer> passivePortsList = new HashSet<Integer>();
        boolean inRange = false;
        Integer lastPort = Integer.valueOf(1);
        StringTokenizer st = new StringTokenizer(portsString, ",;-", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (",".equals(token) || ";".equals(token)) {
                if (inRange) {
                    fillRange(passivePortsList, lastPort, MAX_PORT_INTEGER);
                }
                lastPort = Integer.valueOf(1);
                inRange = false;
            } else if ("-".equals(token)) {
                inRange = true;
            } else if (token.length() == 0) {
            } else {
                Integer port = Integer.valueOf(token);
                verifyPort(port);
                if (inRange) {
                    fillRange(passivePortsList, lastPort, port);
                    inRange = false;
                }
                addPort(passivePortsList, port);
                lastPort = port;
            }
        }
        if (inRange) {
            fillRange(passivePortsList, lastPort, MAX_PORT_INTEGER);
        }
        return passivePortsList;
    }

    private static void fillRange(final Set<Integer> passivePortsList, final Integer beginPort, final Integer endPort) {
        for (int i = beginPort; i <= endPort; i++) {
            addPort(passivePortsList, Integer.valueOf(i));
        }
    }

    private static void addPort(final Set<Integer> passivePortsList, final Integer port) {
        passivePortsList.add(port);
    }

    private static void verifyPort(final int port) {
        if (port < 0) {
            throw new IllegalArgumentException("Port can not be negative: " + port);
        } else if (port > MAX_PORT) {
            throw new IllegalArgumentException("Port too large: " + port);
        }
    }

    public PassivePorts(final String passivePorts, boolean checkIfBound) {
        this(parse(passivePorts), checkIfBound);
        this.passivePortsString = passivePorts;
    }

    public PassivePorts(Set<Integer> passivePorts, boolean checkIfBound) {
        if (passivePorts == null) {
            throw new NullPointerException("passivePorts can not be null");
        } else if (passivePorts.isEmpty()) {
            passivePorts = new HashSet<Integer>();
            passivePorts.add(0);
        }
        this.freeList = new ArrayList<Integer>(passivePorts);
        this.usedList = new HashSet<Integer>(passivePorts.size());
        this.checkIfBound = checkIfBound;
    }

    private boolean checkPortUnbound(int port) {
        if (!checkIfBound) {
            return true;
        }
        if (port == 0) {
            return true;
        }
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }
    }

    public synchronized int reserveNextPort() {
        List<Integer> freeCopy = new ArrayList<Integer>(freeList);
        while (freeCopy.size() > 0) {
            int i = r.nextInt(freeCopy.size());
            Integer ret = freeCopy.get(i);
            if (ret == 0) {
                return 0;
            } else if (checkPortUnbound(ret)) {
                freeList.remove(i);
                usedList.add(ret);
                return ret;
            } else {
                freeCopy.remove(i);
                log.warn("Passive port in use by another process: " + ret);
            }
        }
        return -1;
    }

    public synchronized void releasePort(final int port) {
        if (port == 0) {
        } else if (usedList.remove(port)) {
            freeList.add(port);
        } else {
            log.warn("Releasing unreserved passive port: " + port);
        }
    }

    @Override
    public String toString() {
        if (passivePortsString != null) {
            return passivePortsString;
        }
        StringBuilder sb = new StringBuilder();
        for (Integer port : freeList) {
            sb.append(port);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
