package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.nms.NMSX;
import org.cyberpwn.react.util.Platform;
import org.cyberpwn.react.util.Task;

public class RebootController extends Controller implements Configurable
{
	private File script;
	private ClusterConfig cc;
	
	public RebootController(React react)
	{
		super(react);
		
		cc = new ClusterConfig();
		react.getDataController().load((String) null, this);
		script = new File(getRoot(), cc.getString("script-name"));
		generateScript();
	}
	
	public void generateScript()
	{
		if(script.exists())
		{
			return;
		}
		
		PrintWriter f;
		
		try
		{
			f = new PrintWriter(script);
			
			if(Platform.ENVIRONMENT.canRunBatch())
			{
				generateWindowsBatchFile(f);
			}
			
			else
			{
				generateShellFile(f);
			}
			
			f.close();
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void generateWindowsBatchFile(PrintWriter w)
	{
		w.println("@echo off");
		w.println(":: Customize your java executable here. Or rewrite this script completley.");
		w.println("java -Xmx2G -Xms1M -jar SERVERJAR.jar");
		w.println("pause");
	}
	
	public void generateShellFile(PrintWriter w)
	{
		w.println("#!/bin/sh");
		w.println("java -Xmx2G -Xms1M -jar SERVERJAR.jar");
	}
	
	public File getRoot()
	{
		return getReact().getDataFolder().getParentFile().getParentFile();
	}
	
	@Override
	public void start()
	{
		
	}
	
	@Override
	public void stop()
	{
		
	}
	
	public void restart(int ticks, boolean warn)
	{
		if(ticks < 1)
		{
			restart(warn);
		}
		
		else
		{
			int[] tm = new int[] {ticks};
			
			new Task(0)
			{
				@Override
				public void run()
				{
					tm[0]--;
					
					if(tm[0] < 1)
					{
						cancel();
						restart(0, warn);
					}
					
					else
					{
						int time = tm[0];
						
						if(time % 2 == 0)
						{
							for(Player i : React.instance().onlinePlayers())
							{
								NMSX.sendTitle(i, 0, 20, 20, ChatColor.RED + "Rebooting", ChatColor.GRAY + "Rebooting in " + time / 20 + " second" + (time / 20 == 1 ? "" : "s"));
							}
						}
					}
				}
			};
		}
	}
	
	public void restart(boolean warn)
	{
		try
		{
			restart();
		}
		
		catch(IOException e)
		{
			f("Failed to reboot... Check your script path, and the contents.");
			e.printStackTrace();
		}
	}
	
	public void restart() throws IOException
	{
		if(script.exists())
		{
			if(Platform.ENVIRONMENT.canRunBatch())
			{
				Runtime.getRuntime().exec("cmd /c start \"\" \"" + script.toString() + "\"");
				Bukkit.shutdown();
			}
			
			else
			{
				Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "sh " + script.toString()});
				Bukkit.shutdown();
			}
		}
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("command-defaults.default-reboot-delay-seconds", 10, "The default. Command can override this (use /reboot -h)");
		cc.set("command-defaults.default-reboot-warning", true, "The default. Command can override this (use /reboot -h)");
		cc.set("enabled", false);
		cc.set("script-name", "react-reboot.script", "The name of the script in your server path. Set the extention to either bat or sh\nWrite your script in batch language if you are on windows\nWrite your script in shell (bin/bash) if you are on linux/macos");
	}
	
	public boolean isEnabled()
	{
		return cc.getBoolean("enabled");
	}
	
	public boolean command(CommandSender sender, String cmd)
	{
		if(cmd.startsWith("/reboot") || cmd.startsWith("/restart"))
		{
			if(sender.hasPermission("bukkit.command.reload"))
			{
				if(cmd.toLowerCase().contains("-h"))
				{
					sender.sendMessage(ChatColor.GRAY + "-h This Help Page");
					sender.sendMessage(ChatColor.GRAY + "-s Silent Reboot. Dont inform players");
					sender.sendMessage(ChatColor.GRAY + "-t:[seconds] Reboot delay in seconds.");
				}
				
				else if(!isEnabled())
				{
					sender.sendMessage(Info.TAG + ChatColor.RED + "Rebooter not setup");
				}
				
				else
				{
					int ticks = cc.getInt("command-defaults.default-reboot-delay-seconds");
					boolean warn = cc.getBoolean("command-defaults.default-reboot-warning");
					
					String[] argv = cmd.split(" ");
					
					for(String i : argv)
					{
						if(i.startsWith("-s"))
						{
							warn = false;
						}
						
						if(i.startsWith("-t:"))
						{
							String[] amv = i.split(":");
							
							try
							{
								Integer s = Integer.valueOf(amv[1]);
								ticks = s * 20;
							}
							
							catch(Exception ex)
							{
								ticks = 0;
							}
						}
					}
					
					if(ticks < 0)
					{
						ticks = 0;
					}
					
					if(ticks == 0)
					{
						warn = false;
					}
					
					React.instance().getMonitorController().getMonitors().clear();
					sender.sendMessage(Info.TAG + ChatColor.GRAY + "Rebooting " + (ticks == 0 ? "NOW" : "in " + ticks / 20 + " seconds") + (warn ? ", informing players" : ", silently."));
					restart(ticks, warn);
				}
			}
			
			else
			{
				sender.sendMessage(Info.TAG + ChatColor.RED + "No Permission");
			}
			
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	public void on(ServerCommandEvent e)
	{
		e.setCancelled(command(e.getSender(), "/" + e.getCommand()));
	}
	
	@EventHandler
	public void on(PlayerCommandPreprocessEvent e)
	{
		e.setCancelled(command(e.getPlayer(), e.getMessage()));
	}
	
	@Override
	public void onReadConfig()
	{
		
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "rebooter";
	}
}
