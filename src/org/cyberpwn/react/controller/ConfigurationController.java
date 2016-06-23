package org.cyberpwn.react.controller;

import java.io.File;

import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.util.GMap;

import net.md_5.bungee.api.ChatColor;

public class ConfigurationController extends Controller
{
	private GMap<File, Configurable> configurations;
	private GMap<File, ClusterConfig> cache;
	
	public ConfigurationController(React react)
	{
		super(react);
		
		configurations = new GMap<File, Configurable>();
		cache = new GMap<File, ClusterConfig>();
		
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
			getReact().getDataController().saveFileConfig(i, configurations.get(i).getConfiguration().toYaml());
		}
		
		p.sendMessage(Info.TAG + ChatColor.GOLD + "> Reloading React...");
		React.instance().onReload(p);
		p.sendMessage(Info.TAG + ChatColor.GREEN + "Complete!");
		p.sendMessage(Info.HR);
	}
	
	public void registerConfiguration(Configurable c, File file)
	{
		configurations.put(file, c);
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
}
