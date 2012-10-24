package driver;

import java.io.File;
import java.io.IOException;

import org.apache.ftpserver.clienttests.ClientTestTemplate;

public class FTPMainDriver 
{
    private static final File TEST_TMP_DIR = new File("test-tmp");
    protected static final File ROOT_DIR = new File(TEST_TMP_DIR, "ftproot");
	public static final File TEST_FILE1 = new File(ROOT_DIR, "test1.txt");
	public static ClientTestTemplate cttinstance = new ClientTestTemplate() ;
	public static void main(String[] args)
	{
		try
		{
		Thread t1 = new Thread() 
		{
			public void run()
			{
				try {
					cttinstance.initDirs();
					cttinstance.initServer();

					cttinstance.connectClient();
					 TEST_FILE1.createNewFile();
				} catch (Exception e) {
					// XXX Auto-generated catch block
					e.printStackTrace();
				}

				
			//	org.apache.ftpserver.main.CommandLine.main(new String[0]);
			}
		};
		
		Thread t2 = new Thread()
		{
			public void run()
			{
				
				driver.FTPClient0.main(new String[0]);
			}
		};
		
		t1.start();
		Thread.sleep(1000);
		t2.start();
		Thread.sleep(10000);

	}catch(Exception e)
	{
		
	}
	}
}
