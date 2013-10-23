package org.apache.ftpserver.main;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CommandLine {

    protected CommandLine() {
    }

    public static void main(String args[]) {
        CommandLine cli = new CommandLine();
        try {
            FtpServer server = cli.getConfiguration(args);
            if (server == null) {
                return;
            }
            server.start();
            System.out.println("FtpServer started");
            cli.addShutdownHook(server);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addShutdownHook(final FtpServer engine) {
        Runnable shutdownHook = new Runnable() {

            public void run() {
                System.out.println("Stopping server...");
                engine.stop();
            }
        };
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(shutdownHook));
    }

    protected void usage() {
        System.err.println("Usage: java org.apache.ftpserver.main.CommandLine [OPTION] [CONFIGFILE]");
        System.err.println("Starts FtpServer using the default configuration of the ");
        System.err.println("configuration file if provided.");
        System.err.println("");
        System.err.println("      --default              use the default configuration, ");
        System.err.println("                             also used if no command line argument is given ");
        System.err.println("  -?, --help                 print this message");
    }

    protected FtpServer getConfiguration(String[] args) throws Exception {
        FtpServer server = null;
        if (args.length == 0) {
            System.out.println("Using default configuration");
            server = new FtpServerFactory().createServer();
        } else if ((args.length == 1) && args[0].equals("-default")) {
            System.out.println("The -default switch is deprecated, please use --default instead");
            System.out.println("Using default configuration");
            server = new FtpServerFactory().createServer();
        } else if ((args.length == 1) && args[0].equals("--default")) {
            System.out.println("Using default configuration");
            server = new FtpServerFactory().createServer();
        } else if ((args.length == 1) && args[0].equals("--help")) {
            usage();
        } else if ((args.length == 1) && args[0].equals("-?")) {
            usage();
        } else if (args.length == 1) {
            System.out.println("Using XML configuration file " + args[0] + "...");
            FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(args[0]);
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
            usage();
        }
        return server;
    }
}
