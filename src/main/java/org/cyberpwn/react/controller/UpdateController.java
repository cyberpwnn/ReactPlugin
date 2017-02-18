package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.util.Download;
import org.cyberpwn.react.util.FM;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.N;
import org.cyberpwn.react.util.PluginUtil;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.TaskLater;

public class UpdateController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private Boolean failed;
	private File updateFolder;
	private File updateJarFile;
	private File tempFile;
	private File tempFolder;
	private FileConfiguration fc;
	private boolean updated;
	private static boolean needsRestart;
	
	public UpdateController(React react)
	{
		super(react);
		
		needsRestart = false;
		cc = new ClusterConfig();
		failed = false;
		updated = false;
		updateFolder = new File(React.instance().getDataFolder().getParentFile(), "update");
		updateJarFile = new File(updateFolder, PluginUtil.getPluginFileName("React"));
		tempFolder = new File(React.instance().getDataFolder(), "temp");
		tempFile = new File(tempFolder, "react.kex.tmp");
	}
	
	@Override
	public void tick()
	{
		if(needsRestart)
		{
			needsRestart = false;
			PluginUtil.reload(Bukkit.getPluginManager().getPlugin("React"));
		}
	}
	
	@Override
	public void start()
	{
		cleanup();
		
		if(cc.getBoolean("update-checking.enable"))
		{
			new TaskLater(200)
			{
				@Override
				public void run()
				{
					if(!cc.getBoolean("update-checking.enable"))
					{
						return;
					}
					
					new Task(20 * cc.getInt("update-checking.interval-seconds"))
					{
						@Override
						public void run()
						{
							if(!cc.getBoolean("update-checking.enable"))
							{
								cancel();
								return;
							}
							
							if(failed)
							{
								cancel();
								f("Last Check Failed. Breaking Checker until reload.");
								return;
							}
							
							if(updated)
							{
								cancel();
								return;
							}
							
							try
							{
								getData(new FCCallback()
								{
									@Override
									public void run()
									{
										if(fc().getInt("package.version-code") > Version.C)
										{
											s("Update Found.");
											
											if(cc.getBoolean("updater.auto-update.enabled"))
											{
												s("Cleaning Files");
												cleanup();
												s("Downloading Update");
												update(null);
											}
										}
									}
								});
							}
							
							catch(Exception e)
							{
								failed = true;
							}
						}
					};
				}
			};
		}
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("update-checking.enable", true, "Enable update checking?");
		cc.set("update-checking.interval-seconds", 30, "How often (in seconds) should we check for an update?\nThis is async, so if we dont get a connection, no freezing.");
		cc.set("updater.auto-update.enabled", true, "Should we automatically download the update if we find it?\nWe wont redownload each update check.");
		cc.set("updater.allow-update-command", true, "Allow the update command to be used?");
		cc.set("updater.only-update-on-reboot", true, "Only update (moving the downloaded plugin into the plugis folder)\nWARNING: Turning this off will inject the downloaded plugin and reload react.\nTHIS HAS CAUSED RARE CRASHES IN THE PAST! YOU HAVE BEEN WARNED!");
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
		return "updater";
	}
	
	public void broadcast(String msg)
	{
		if(cc.getBoolean("update-checking.enable"))
		{
			for(Player i : getReact().onlinePlayers())
			{
				if(i.hasPermission(Info.PERM_RELOAD))
				{
					i.sendMessage(msg);
				}
			}
			
			Bukkit.getConsoleSender().sendMessage(msg);
		}
	}
	
	public void cleanup()
	{
		if(!updateFolder.exists())
		{
			updateFolder.mkdir();
		}
		
		for(File i : updateFolder.listFiles())
		{
			i.delete();
		}
		
		if(!tempFolder.exists())
		{
			return;
		}
		
		for(File i : tempFolder.listFiles())
		{
			i.delete();
		}
		
		tempFolder.delete();
	}
	
	public void update(final CommandSender sender)
	{
		if(!cc.getBoolean("update-checking.enable"))
		{
			return;
		}
		
		if(sender != null)
		{
			sender.sendMessage(Info.TAG + ChatColor.GREEN + "Checking Metadata...");
		}
		
		getUpdate(new Runnable()
		{
			@Override
			public void run()
			{
				if(sender != null)
				{
					sender.sendMessage(Info.TAG + ChatColor.GREEN + "Reading...");
					
					if(cc.getBoolean("updater.only-update-on-reboot") && updated)
					{
						sender.sendMessage(Info.TAG + ChatColor.RED + "You already have the latest version downloaded. You need to reboot/reload to apply the update.");
					}
				}
				
				if(cc.getBoolean("updater.only-update-on-reboot") && updated)
				{
					return;
				}
				
				decode(sender);
			}
		});
	}
	
	public void decode(final CommandSender sender)
	{
		if(!cc.getBoolean("update-checking.enable"))
		{
			return;
		}
		
		updateJarFile.delete();
		
		try
		{
			FM.parse(tempFile, updateJarFile);
			
			getUpdate(new Runnable()
			{
				@Override
				public void run()
				{
					fc = null;
					
					getData(new FCCallback()
					{
						@Override
						public void run()
						{
							fc = fc();
							
							if(fc.getInt("package.version-code") <= Version.C)
							{
								if(sender != null)
								{
									sender.sendMessage(Info.TAG + ChatColor.RED + "You already have the latest version!");
								}
								
								return;
							}
							
							N.t("Update Downloaded: " + fc.getString("package.version"), "version-downloaded", fc.getString("package.version"));
							
							broadcast(String.format(Info.HRN, "Updated"));
							broadcast(Info.TAG + ChatColor.LIGHT_PURPLE + "Version " + fc.getString("package.version"));
							
							for(String i : fc.getStringList("package.description"))
							{
								broadcast(Info.TAG + ChatColor.GREEN + " > " + i);
							}
							
							updated = true;
							
							if(cc.getBoolean("updater.only-update-on-reboot"))
							{
								broadcast(Info.TAG + ChatColor.RED + "You need to reboot/reload to apply the update.");
							}
							
							else
							{
								new TaskLater(0)
								{
									@Override
									public void run()
									{
										for(Controllable i : getReact().getControllers())
										{
											try
											{
												i.stop();
											}
											
											catch(Exception e)
											{
												
											}
										}
										
										getReact().getControllers().clear();
										PluginUtil.reload(Bukkit.getPluginManager().getPlugin("React"));
									}
								};
								
								broadcast(Info.HR);
							}
						}
					});
				}
			});
		}
		
		catch(IOException e)
		{
			
		}
	}
	
	public void getData(FCCallback fc)
	{
		if(!cc.getBoolean("update-checking.enable"))
		{
			return;
		}
		
		new Fetcher("https://raw.githubusercontent.com/cyberpwnn/React/master/serve/package.yml", fc).start();
	}
	
	public void getUpdate(Runnable ru)
	{
		if(!cc.getBoolean("update-checking.enable"))
		{
			return;
		}
		
		try
		{
			if(!tempFolder.exists())
			{
				tempFolder.mkdirs();
			}
			
			if(tempFile.exists())
			{
				tempFile.delete();
			}
			
			new Download(new URL("https://github.com/cyberpwnn/React/raw/master/serve/pack/React.jar"), tempFile, ru).start();
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void restart()
	{
		needsRestart = true;
	}
	
	public void checkVersion(final CommandSender sender)
	{
		sender.sendMessage(Info.TAG + ChatColor.DARK_GRAY + "React " + ChatColor.AQUA + "v" + Version.V);
		
		getData(new FCCallback()
		{
			@Override
			public void run()
			{
				int vc = fc().getInt("package.version-code");
				String v = fc().getString("package.version");
				GList<String> s = new GList<String>(fc().getStringList("package.description"));
				
				if(vc <= Version.C)
				{
					sender.sendMessage(Info.TAG + ChatColor.DARK_GRAY + "You have the latest version (" + Version.V + ")");
				}
				
				else
				{
					sender.sendMessage(Info.TAG + ChatColor.DARK_GRAY + "Update Found (v" + v + ")");
					
					if(cc.getBoolean("updater.only-update-on-reboot") && updated)
					{
						sender.sendMessage(Info.TAG + ChatColor.RED + "You already have this version downloaded. You need to reboot/reload to apply the update.");
					}
					
					for(String i : s)
					{
						sender.sendMessage(Info.TAG + ChatColor.DARK_GRAY + " > " + i);
					}
				}
			}
		});
	}
}
