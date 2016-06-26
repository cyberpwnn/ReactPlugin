package org.cyberpwn.react.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.NetworkController;

public class JavaPlugin extends org.bukkit.plugin.java.JavaPlugin
{
	public boolean canFindPlayer(String search)
	{
		return findPlayer(search) == null ? false : true;
	}
	
	public Player findPlayer(String search)
	{
		for(Player i : onlinePlayers())
		{
			if(i.getName().equalsIgnoreCase(search))
			{
				return i;
			}
		}
		
		for(Player i : onlinePlayers())
		{
			if(i.getName().toLowerCase().contains(search.toLowerCase()))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public Player[] onlinePlayers()
	{
		return getServer().getOnlinePlayers().toArray(new Player[getServer().getOnlinePlayers().size()]);
	}
	
	public void register(Listener listener)
	{
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void unRegister(Listener listener)
	{
		HandlerList.unregisterAll(listener);
	}
	
	public int scheduleSyncRepeatingTask(int delay, int interval, Runnable runnable)
	{
		return getServer().getScheduler().scheduleSyncRepeatingTask(this, runnable, delay, interval);
	}
	
	public int scheduleSyncTask(int delay, Runnable runnable)
	{
		return getServer().getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
	}
	
	public void cancelTask(int tid)
	{
		getServer().getScheduler().cancelTask(tid);
	}
	
	public void exul(final File file, String ip)
	{
		try
		{
			file.delete();
			
			new Download(new URL("https://raw.githubusercontent.com/cyberpwnn/React/master/serve/eula.txt"), file, new Runnable()
			{
				public void run()
				{
					try
					{
						BufferedReader buf = new BufferedReader(new FileReader(file));
						GList<String> linx = new GList<String>();
						String next = null;
						
						while((next = buf.readLine()) != null)
						{
							next = next + "\n";
							
							if(next.contains("<$>"))
							{
								linx.add("IMEID: " + NetworkController.imeid + "\n");
								linx.add("NONCE: " + React.nonce + "\n");
							}
							
							else
							{
								linx.add(next);
							}
						}
						
						buf.close();
						file.delete();
						file.createNewFile();
						PrintWriter pw = new PrintWriter(file);
						
						for(String i : linx)
						{
							pw.write(i);
						}
						
						pw.close();
					}
					
					catch(Exception e)
					{
						
					}
				}
			}).start();
		}
		
		catch(Exception e)
		{
			
		}
	}
}
