package org.cyberpwn.react.util;

import java.io.File;

public class FSMap extends Thread
{
	private File root;
	private Callback<GMap<File, Long>> c;
	private GMap<File, Long> folders;
	
	public FSMap(File root, Callback<GMap<File, Long>> c)
	{
		this.root = root;
		this.folders = new GMap<File, Long>();
		this.c = c;
	}
	
	public void run()
	{
		for(File i : root.listFiles())
		{
			if(i.isDirectory())
			{
				folders.put(i, total(i));
			}
		}
		
		c.run(folders);
	}
	
	public long total(File f)
	{
		if(f.isDirectory())
		{
			long total = 0;
			
			for(File i : f.listFiles())
			{
				total += total(i);
			}
			
			return total;
		}
		
		else
		{
			return f.length();
		}
	}
}
