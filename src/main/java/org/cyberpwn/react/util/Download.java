package org.cyberpwn.react.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Download extends Thread
{
	private URL url;
	private File path;
	private Runnable callback;
	
	public Download(URL url, File path, Runnable callback)
	{
		this.url = url;
		this.path = path;
		this.callback = callback;
	}
	
	@Override
	public void run()
	{
		try
		{
			if(!path.exists())
			{
				path.getParentFile().mkdirs();
				path.createNewFile();
			}
			
			FU.copyURLToFile(url, path);
			callback.run();
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
