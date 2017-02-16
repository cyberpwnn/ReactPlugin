package org.cyberpwn.react.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class HijackedConsole extends Thread
{
	public static boolean hijacked = false;
	public static GList<String> out = new GList<String>();
	
	@Override
	public void run()
	{
		while(hijacked)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			PrintStream old = System.out;
			System.setOut(ps);
			
			try
			{
				Thread.sleep(50);
			}
			
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			System.out.flush();
			System.setOut(old);
			
			if(baos.toString().length() > 0)
			{
				for(String i : baos.toString().split("\n"))
				{
					System.out.println(i);
					
					String f = i.trim();
					
					out.add(f);
				}
				
				while(out.size() > 256)
				{
					out.pop();
				}
			}
		}
	}
}
