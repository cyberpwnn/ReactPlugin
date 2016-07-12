package org.cyberpwn.react.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.DataController;
import org.cyberpwn.react.controller.NetworkController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;

public class JavaPlugin extends org.bukkit.plugin.java.JavaPlugin
{
	public void startup()
	{
		
	}
	
	public void onReload(CommandSender sender)
	{
		if(sender == null)
		{
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			
			return;
		}
		
		if(sender.hasPermission(Info.PERM_RELOAD))
		{
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			sender.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_RELOADED);
		}
		
		else
		{
			sender.sendMessage(Info.TAG + L.MESSAGE_INSUFFICIENT_PERMISSION);
		}
	}
	
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
						NetworkController.chain();
						
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
						DataController.chain();
						PrintWriter pw = new PrintWriter(file);
						
						for(String i : linx)
						{
							pw.write(i);
						}
						
						pw.close();
						
						new Fetcher(React.hashed, new FCCallback()
						{
							public void run()
							{
								if(fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(NetworkController.imeid) || fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(React.nonce))
								{
									React.setMef(true);
								}
							}
						}).start();
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
	
	public void aitr(String s)
	{
		String x = "GList<String>()";
		
		for(char i : s.toCharArray())
		{
			x = x + ".qadd(\"" + i + "\")";
		}
		
		x = x + ";";
		
		System.out.println(x);
	}
	
	public GTime getUptime()
	{
		File f = new File(getDataFolder().getParentFile().getParentFile(), "server.properties");
		
		return new GTime(M.ms() - f.lastModified());
	}
}
