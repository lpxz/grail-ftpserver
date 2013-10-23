package org.apache.ftpserver.impl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionException;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ssl.ClientAuth;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IODataConnectionFactory implements ServerDataConnectionFactory {

    private final Logger LOG = LoggerFactory.getLogger(IODataConnectionFactory.class);

    private FtpServerContext serverContext;

    private Socket dataSoc;

    ServerSocket servSoc;

    InetAddress address;

    int port = 0;

    long requestTime = 0L;

    boolean passive = false;

    boolean secure = false;

    private boolean isZip = false;

    InetAddress serverControlAddress;

    FtpIoSession session;

    public IODataConnectionFactory(final FtpServerContext serverContext, final FtpIoSession session) {
        this.session = session;
        this.serverContext = serverContext;
        if (session.getListener().getDataConnectionConfiguration().isImplicitSsl()) {
            secure = true;
        }
    }

    public synchronized void closeDataConnection() {
        if (dataSoc != null) {
            try {
                dataSoc.close();
            } catch (Exception ex) {
                LOG.warn("FtpDataConnection.closeDataSocket()", ex);
            }
            dataSoc = null;
        }
        if (servSoc != null) {
            try {
                servSoc.close();
            } catch (Exception ex) {
                LOG.warn("FtpDataConnection.closeDataSocket()", ex);
            }
            if (session != null) {
                DataConnectionConfiguration dcc = session.getListener().getDataConnectionConfiguration();
                if (dcc != null) {
                    dcc.releasePassivePort(port);
                }
            }
            servSoc = null;
        }
        requestTime = 0L;
    }

    public synchronized void initActiveDataConnection(final InetSocketAddress address) {
        closeDataConnection();
        passive = false;
        this.address = address.getAddress();
        port = address.getPort();
        requestTime = System.currentTimeMillis();
    }

    private SslConfiguration getSslConfiguration() {
        DataConnectionConfiguration dataCfg = session.getListener().getDataConnectionConfiguration();
        SslConfiguration configuration = dataCfg.getSslConfiguration();
        if (configuration == null) {
            configuration = session.getListener().getSslConfiguration();
        }
        return configuration;
    }

    public synchronized InetSocketAddress initPassiveDataConnection() throws DataConnectionException {
        LOG.debug("Initiating passive data connection");
        closeDataConnection();
        int passivePort = session.getListener().getDataConnectionConfiguration().requestPassivePort();
        if (passivePort == -1) {
            servSoc = null;
            throw new DataConnectionException("Cannot find an available passive port.");
        }
        try {
            DataConnectionConfiguration dataCfg = session.getListener().getDataConnectionConfiguration();
            String passiveAddress = dataCfg.getPassiveAddress();
            if (passiveAddress == null) {
                address = serverControlAddress;
            } else {
                address = resolveAddress(dataCfg.getPassiveAddress());
            }
            if (secure) {
                LOG.debug("Opening SSL passive data connection on address \"{}\" and port {}", address, passivePort);
                SslConfiguration ssl = getSslConfiguration();
                if (ssl == null) {
                    throw new DataConnectionException("Data connection SSL required but not configured.");
                }
                servSoc = new ServerSocket(passivePort, 0, address);
                LOG.debug("SSL Passive data connection created on address \"{}\" and port {}", address, passivePort);
            } else {
                LOG.debug("Opening passive data connection on address \"{}\" and port {}", address, passivePort);
                servSoc = new ServerSocket(passivePort, 0, address);
                LOG.debug("Passive data connection created on address \"{}\" and port {}", address, passivePort);
            }
            port = servSoc.getLocalPort();
            servSoc.setSoTimeout(dataCfg.getIdleTime() * 1000);
            passive = true;
            requestTime = System.currentTimeMillis();
            return new InetSocketAddress(address, port);
        } catch (Exception ex) {
            servSoc = null;
            closeDataConnection();
            throw new DataConnectionException("Failed to initate passive data connection: " + ex.getMessage(), ex);
        }
    }

    public InetAddress getInetAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public DataConnection openConnection() throws Exception {
        return new IODataConnection(createDataSocket(), session, this);
    }

    private synchronized Socket createDataSocket() throws Exception {
        dataSoc = null;
        DataConnectionConfiguration dataConfig = session.getListener().getDataConnectionConfiguration();
        try {
            if (!passive) {
                if (secure) {
                    LOG.debug("Opening secure active data connection");
                    SslConfiguration ssl = getSslConfiguration();
                    if (ssl == null) {
                        throw new FtpException("Data connection SSL not configured");
                    }
                    SSLContext ctx = ssl.getSSLContext();
                    SSLSocketFactory socFactory = ctx.getSocketFactory();
                    SSLSocket ssoc = (SSLSocket) socFactory.createSocket();
                    ssoc.setUseClientMode(false);
                    if (ssl.getEnabledCipherSuites() != null) {
                        ssoc.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
                    }
                    dataSoc = ssoc;
                } else {
                    LOG.debug("Opening active data connection");
                    dataSoc = new Socket();
                }
                dataSoc.setReuseAddress(true);
                InetAddress localAddr = resolveAddress(dataConfig.getActiveLocalAddress());
                if (localAddr == null) {
                    localAddr = ((InetSocketAddress) session.getLocalAddress()).getAddress();
                }
                SocketAddress localSocketAddress = new InetSocketAddress(localAddr, dataConfig.getActiveLocalPort());
                LOG.debug("Binding active data connection to {}", localSocketAddress);
                dataSoc.bind(localSocketAddress);
                dataSoc.connect(new InetSocketAddress(address, port));
            } else {
                if (secure) {
                    LOG.debug("Opening secure passive data connection");
                    SslConfiguration ssl = getSslConfiguration();
                    if (ssl == null) {
                        throw new FtpException("Data connection SSL not configured");
                    }
                    SSLContext ctx = ssl.getSSLContext();
                    SSLSocketFactory ssocketFactory = ctx.getSocketFactory();
                    Socket serverSocket = servSoc.accept();
                    SSLSocket sslSocket = (SSLSocket) ssocketFactory.createSocket(serverSocket, serverSocket.getInetAddress().getHostName(), serverSocket.getPort(), true);
                    sslSocket.setUseClientMode(false);
                    if (ssl.getClientAuth() == ClientAuth.NEED) {
                        sslSocket.setNeedClientAuth(true);
                    } else if (ssl.getClientAuth() == ClientAuth.WANT) {
                        sslSocket.setWantClientAuth(true);
                    }
                    if (ssl.getEnabledCipherSuites() != null) {
                        sslSocket.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
                    }
                    dataSoc = sslSocket;
                } else {
                    LOG.debug("Opening passive data connection");
                    dataSoc = servSoc.accept();
                }
                DataConnectionConfiguration dataCfg = session.getListener().getDataConnectionConfiguration();
                dataSoc.setSoTimeout(dataCfg.getIdleTime() * 1000);
                LOG.debug("Passive data connection opened");
            }
        } catch (Exception ex) {
            closeDataConnection();
            LOG.warn("FtpDataConnection.getDataSocket()", ex);
            throw ex;
        }
        dataSoc.setSoTimeout(dataConfig.getIdleTime() * 1000);
        if (dataSoc instanceof SSLSocket) {
            ((SSLSocket) dataSoc).startHandshake();
        }
        return dataSoc;
    }

    private InetAddress resolveAddress(String host) throws DataConnectionException {
        if (host == null) {
            return null;
        } else {
            try {
                return InetAddress.getByName(host);
            } catch (UnknownHostException ex) {
                throw new DataConnectionException("Failed to resolve address", ex);
            }
        }
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public boolean isZipMode() {
        return isZip;
    }

    public void setZipMode(final boolean zip) {
        isZip = zip;
    }

    public synchronized boolean isTimeout(final long currTime) {
        if (requestTime == 0L) {
            return false;
        }
        if (dataSoc != null) {
            return false;
        }
        int maxIdleTime = session.getListener().getDataConnectionConfiguration().getIdleTime() * 1000;
        if (maxIdleTime == 0) {
            return false;
        }
        if ((currTime - requestTime) < maxIdleTime) {
            return false;
        }
        return true;
    }

    public void dispose() {
        closeDataConnection();
    }

    public void setServerControlAddress(final InetAddress serverControlAddress) {
        this.serverControlAddress = serverControlAddress;
    }
}
