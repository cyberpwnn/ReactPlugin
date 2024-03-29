package org.cyberpwn.react.file;

import org.cyberpwn.react.util.GList;

public class FileHack
{
	private GList<FOP> queue;
	
	public FileHack()
	{
		queue = new GList<FOP>();
	}
	
	public void queue(FOP f)
	{
		f.operate();
	}
	
	public void execute()
	{
		while(!queue.isEmpty())
		{
			queue.pop().operate();
		}
	}
}
