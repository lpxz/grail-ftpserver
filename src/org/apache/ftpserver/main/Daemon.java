package org.apache.ftpserver.main;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Daemon {

    private static final Logger LOG = LoggerFactory.getLogger(Daemon.class);

    private static FtpServer server;

    private static Object lock = new Object();

    public static void main(String[] args) throws Exception {
        try {
            if (server == null) {
                server = getConfiguration(args);
                if (server == null) {
                    LOG.error("No configuration provided");
                    throw new FtpException("No configuration provided");
                }
            }
            String command = "start";
            if (args != null && args.length > 0) {
                command = args[0];
            }
            if (command.equals("start")) {
                LOG.info("Starting FTP server daemon");
                server.start();
                synchronized (lock) {
                    lock.wait();
                }
            } else if (command.equals("stop")) {
                synchronized (lock) {
                    lock.notify();
                }
                LOG.info("Stopping FTP server daemon");
                server.stop();
            }
        } catch (Throwable t) {
            LOG.error("Daemon error", t);
        }
    }

    private static FtpServer getConfiguration(String[] args) throws Exception {
        FtpServer server = null;
        if (args == null || args.length < 2) {
            LOG.info("Using default configuration....");
            server = new FtpServerFactory().createServer();
        } else if ((args.length == 2) && args[1].equals("-default")) {
            System.out.println("The -default switch is deprecated, please use --default instead");
            LOG.info("Using default configuration....");
            server = new FtpServerFactory().createServer();
        } else if ((args.length == 2) && args[1].equals("--default")) {
            LOG.info("Using default configuration....");
            server = new FtpServerFactory().createServer();
        } else if (args.length == 2) {
            LOG.info("Using xml configuration file " + args[1] + "...");
            FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(args[1]);
            if (ctx.containsBean("server")) {
                server = (FtpServer) ctx.getBean("server");
            } else {
                String[] beanNames = ctx.getBeanNamesForType(FtpServer.class);
                if (beanNames.length == 1) {
                    server = (FtpServer) ctx.getBean(beanNames[0]);
                } else if (beanNames.length > 1) {
                    System.out.println("Using the first server defined in the configuration, named " + beanNames[0]);
                    server = (FtpServer) ctx.getBean(beanNames[0]);
                } else {
                    System.err.println("XML configuration does not contain a server configuration");
                }
            }
        } else {
            throw new FtpException("Invalid configuration option");
        }
        return server;
    }
}
