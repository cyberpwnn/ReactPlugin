package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
	private static boolean needsRestart;
	
	public UpdateController(React react)
	{
		super(react);
		
		needsRestart = false;
		cc = new ClusterConfig();
		failed = false;
		updateFolder = new File(React.instance().getDataFolder().getParentFile(), "update");
		updateJarFile = new File(updateFolder, PluginUtil.getPluginFileName("React"));
		tempFolder = new File(React.instance().getDataFolder(), "temp");
		tempFile = new File(tempFolder, "react.kex.tmp");
	}
	
	public void tick()
	{
		if(needsRestart)
		{
			needsRestart = false;
			PluginUtil.reload(Bukkit.getPluginManager().getPlugin("React"));
		}
	}
	
	public void start()
	{
		cleanup();
		
		if(cc.getBoolean("update-checking.enabled"))
		{
			s("Starting Update Checker");
			new TaskLater(200)
			{
				public void run()
				{
					new Task(20 * cc.getInt("update-checking.interval-seconds"))
					{
						public void run()
						{
							if(failed)
							{
								cancel();
								f("Last Check Failed. Breaking Checker until reload.");
								return;
							}
							
							try
							{
								getData(new FCCallback()
								{
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
												update();
											}
										}
									}
								});
							}
							
							catch(Exception e)
							{
								failed = true;
								React.fail(e, "Failed to check for updates. You must reload react to start the checker again.");
							}
						}
					};
				}
			};
		}
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("update-checking.enabled", true);
		cc.set("update-checking.interval-seconds", 30);
		cc.set("updater.auto-update.enabled", false);
		cc.set("updater.allow-update-command", true);
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
		for(Player i : getReact().onlinePlayers())
		{
			if(i.hasPermission(Info.PERM_RELOAD))
			{
				i.sendMessage(msg);
			}
		}
		
		Bukkit.getConsoleSender().sendMessage(msg);
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
	
	public void update()
	{
		cleanup();
		
		getUpdate(new Runnable()
		{
			@Override
			public void run()
			{
				decode();
			}
		});
	}
	
	public void decode()
	{
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
						public void run()
						{
							fc = fc();
							
							if(fc.getInt("package.version-code") <= Version.C)
							{
								return;
							}
							
							broadcast(String.format(Info.HRN, "Updated"));
							broadcast(Info.TAG + ChatColor.LIGHT_PURPLE + "Version " + fc.getString("package.version"));
							
							for(String i : fc.getStringList("package.description"))
							{
								broadcast(Info.TAG + ChatColor.GREEN + " > " + i);
							}
							
							broadcast(Info.HR);
							
							new TaskLater(0)
							{
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
		try
		{
			new Fetcher(new URL("https://raw.githubusercontent.com/cyberpwnn/React/master/serve/package.yml"), fc).start();
		}
		
		catch(MalformedURLException e)
		{
			
		}
	}
	
	public void getUpdate(Runnable ru)
	{
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
		sender.sendMessage(Info.TAG + ChatColor.AQUA + "React " + ChatColor.LIGHT_PURPLE + "v" + Version.V);
		
		getData(new FCCallback()
		{
			public void run()
			{
				int vc = fc().getInt("package.version-code");
				String v = fc().getString("package.version");
				GList<String> s = new GList<String>(fc().getStringList("package.description"));
				
				if(vc <= Version.C)
				{
					sender.sendMessage(Info.TAG + ChatColor.GREEN + "You have the latest version (" + Version.V + ")");
				}
				
				else
				{
					sender.sendMessage(Info.TAG + ChatColor.GREEN + "Update Found (v" + v + ")");
					
					for(String i : s)
					{
						sender.sendMessage(Info.TAG + ChatColor.GREEN + " > " + i);
					}
				}
			}
		});
				
	}
}
