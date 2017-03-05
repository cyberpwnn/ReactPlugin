package org.cyberpwn.react.controller;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.LOAD;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.TaskLater;

public class ConfigurationController extends Controller implements Configurable
{
	private GMap<File, Configurable> configurations;
	private GMap<File, ClusterConfig> cache;
	private GMap<Configurable, Long> mods;
	private ClusterConfig cc;
	private boolean scanning;
	
	public ConfigurationController(React react)
	{
		super(react);
		
		scanning = false;
		configurations = new GMap<File, Configurable>();
		cache = new GMap<File, ClusterConfig>();
		mods = new GMap<Configurable, Long>();
		cc = new ClusterConfig();
	}
	
	public void rebuildConfigurations()
	{
		s("Rebuilding Configuration Map");
		
		int s = configurations.size();
		int c = 0;
		
		for(File i : configurations.k())
		{
			React.instance().getDataController().load(i, configurations.get(i));
			c++;
			o("Rebuilding (" + F.pc((double) c / (double) s) + ") -> " + ChatColor.RED + i.getName());
		}
	}
	
	@Override
	public void start()
	{
		if(cc.getBoolean("configuration.mechanics.auto-inject"))
		{
			new Task(5)
			{
				@Override
				public void run()
				{
					scan();
				}
			};
		}
	}
	
	public void scan()
	{
		if(scanning)
		{
			return;
		}
		
		if(!LOAD.INTENSE.min())
		{
			return;
		}
		
		new ASYNC()
		{
			@Override
			public void async()
			{
				for(File i : configurations.k())
				{
					if(React.STOPPING)
					{
						break;
					}
					
					if(!i.exists())
					{
						new TaskLater(0)
						{
							@Override
							public void run()
							{
								notif("File Deleted: " + i.getName() + " (Regenerating Defaults)");
								getReact().getDataController().load(i, configurations.get(i));
							}
						};
					}
					
					if(modified(configurations.get(i)))
					{
						Configurable c = configurations.get(i);
						mods.put(configurations.get(i), i.lastModified());
						int changes = getReact().getDataController().updateConfigurableSettings(i, c.getConfiguration());
						
						if(changes > 0)
						{
							new TaskLater(0)
							{
								@Override
								public void run()
								{
									c.onReadConfig();
									notif("Injected " + changes + " change(s) to " + i.getName());
								}
							};
						}
					}
				}
				
				if(React.STOPPING)
				{
					return;
				}
				
				try
				{
					Thread.sleep(2000);
				}
				
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				
				scanning = false;
			}
		};
	}
	
	public void notif(String s)
	{
		if(cc.getBoolean("configuration.mechanics.notify.console"))
		{
			Bukkit.getConsoleSender().sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + s);
		}
		
		if(cc.getBoolean("configuration.mechanics.notify.react-players"))
		{
			for(Player i : getReact().onlinePlayers())
			{
				if(i.hasPermission(Info.PERM_MONITOR))
				{
					i.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + s);
				}
			}
		}
	}
	
	public boolean modified(Configurable c)
	{
		return mods.get(c) != configurations.findKey(c).lastModified();
	}
	
	public void flush(Player p)
	{
		p.sendMessage(String.format(Info.HRN, "Saving Changes"));
		p.sendMessage(Info.TAG + ChatColor.GOLD + "> Checking Diff in " + cache.size() + " file(s)");
		p.sendMessage(Info.TAG + ChatColor.GOLD + "> Applying Changes Internally...");
		
		for(File i : cache.k())
		{
			configurations.get(i).getConfiguration().setData(cache.get(i).getData());
		}
		
		p.sendMessage(Info.TAG + ChatColor.GOLD + "> Applying Changes to Disk...");
		
		for(File i : configurations.k())
		{
			getReact().getDataController().saveFileConfig(i, configurations.get(i).getConfiguration().toYaml(), configurations.get(i));
		}
		
		p.sendMessage(Info.TAG + ChatColor.GOLD + "> Reloading React...");
		React.instance().onReload(p);
		p.sendMessage(Info.TAG + ChatColor.GREEN + "Complete!");
		p.sendMessage(Info.HR);
	}
	
	public void registerConfiguration(Configurable c, File file)
	{
		configurations.put(file, c);
		mods.put(c, file.lastModified());
	}
	
	public Object getDefaultValue(Configurable c, String key)
	{
		ClusterConfig cc = new ClusterConfig();
		c.onNewConfig(cc);
		
		if(!cc.contains(key))
		{
			return "??";
		}
		
		return cc.getAbstract(key);
	}
	
	public GMap<File, Configurable> getConfigurations()
	{
		return configurations;
	}
	
	public void setConfigurations(GMap<File, Configurable> configurations)
	{
		this.configurations = configurations;
	}
	
	public GMap<File, ClusterConfig> getCache()
	{
		return cache;
	}
	
	public void setCache(GMap<File, ClusterConfig> cache)
	{
		this.cache = cache;
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		
		cc.set("configuration.mechanics.auto-inject", true, "Automatically inject changes from config files into react.");
		cc.set("configuration.mechanics.inject-delay-seconds", 15, "How often (in seconds) should react check the filesystem for any changes?");
		cc.set("configuration.mechanics.notify.console", true, "Notify the console when files are changed and injected?");
		cc.set("configuration.mechanics.notify.react-players", false, "Notifiy players who have the permission react.monitor?");
		cc.set("configuration.enhancements.add-comments", true, "If you can see this, this setting is enabled :P");
		cc.set("configuration.enhancements.add-default-comments", true, "This shows the Default value as a comment. If its on, below is an example :P");
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
		return "configuration-settings";
	}
}
