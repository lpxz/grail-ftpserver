package driver;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.ftpserver.clienttests.ClientTestTemplate;



public class FTPClient0{
    private static InetAddress server;
  
    private static int NUM = 2;
    public static void main(String[] args)  
    {    	
    	
    	try
    	{
    		server = InetAddress.getByName("localhost");
	   
		    Thread[] threads = new Thread[NUM];
	        for (int i = 0; i < NUM; i++) {   
	        	threads[i] = new MyThread(server,FTPMainDriver.cttinstance.serverport );
	        	threads[i].start();
	        }
	        for (int i = 0; i < NUM; i++) {   
	        	threads[i].join();
	        }
	        
    	 }catch(Exception e)
	 	    {
	 	    	e.printStackTrace();
	 	    }finally{
	 	    	System.exit(0);
	 	    }
    }
}

class MyThread extends Thread {
	private InetAddress server;
	private int port;

    public MyThread(InetAddress server, int port) {
        super();
        this.server = server;
        this.port = port;
    }
    public void run() {
        testUpload(ClientTestTemplate.ADMIN_USERNAME, ClientTestTemplate.ADMIN_PASSWORD);
    }
    private void testUpload(String username, String password) {

        FTPClient ftp = new FTPClient();
        try 
        {
        	
            ftp.connect(server , port );
            
            System.out.println("Connected to " + server + ".");
            ftp.login(username, password);
            if(FTPMainDriver.TEST_FILE1.exists())
                {
            	System.out.println("deleting files");
            	ftp.deleteFile(FTPMainDriver.TEST_FILE1.getName());
            	System.out.println("now: " + FTPMainDriver.TEST_FILE1.exists());
                }
            
            
/*
            int reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }

            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalActiveMode();
            ftp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            InputStream input = new FileInputStream(localFile);
            BufferedInputStream bin = new BufferedInputStream(input);

            boolean flag = ftp.storeFile(localFile.getName(), bin);

            if (flag) {
                System.out.println("success�?cost:<"
                        + (System.currentTimeMillis() - starttime) + " ms>");
            } else {
                System.out.println("fail�?cost:<"
                        + (System.currentTimeMillis() - starttime) + " ms>");
            }
*/
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }

    }
}