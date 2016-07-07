package org.cyberpwn.react.util;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.sampler.Samplable;

public class Dump implements Configurable
{
	private ClusterConfig cc;
	private React pl;
	
	public Dump(React pl)
	{
		this.pl = pl;
		this.cc = new ClusterConfig();
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("react.ai.version-name", Version.V);
		cc.set("react.ai.version-code", Version.C);
		cc.set("react.ai.digest.md5sha256sha512", pl.getNetworkController().getImeid());
		
		cc.set("machine.processor.cores", Runtime.getRuntime().availableProcessors());
		
		for(Failure i : React.instance().getFailureController().getFailures())
		{
			cc.set("react.failures.all." + "time-" + i.getTime() + ".message", i.getMessage());
			cc.set("react.failures.all." + "time-" + i.getTime() + ".type", i.getType());
			cc.set("react.failures.all." + "time-" + i.getTime() + ".trace", i.getStackTraceStrings());
		}
		
		for(Samplable i : pl.getSampleController().getSamples().k())
		{
			Configurable c = (Configurable) i;
			ClusterConfig cx = c.getConfiguration();
			
			cc.set("react.controller.sample-controller." + c.getCodeName() + ".value", i.get().getDouble());
			cc.set("react.controller.sample-controller." + c.getCodeName() + ".fvalue", ChatColor.stripColor(i.formatted(true)));
			
			for(String j : cx.getData().k())
			{
				cc.set("react.controller.sample-controller." + c.getCodeName() + ".config." + j, cx.getAbstract(j).toString());
			}
		}
		
		for(Actionable i : pl.getActionController().getActions().k())
		{
			Configurable c = (Configurable) i;
			ClusterConfig cx = c.getConfiguration();
			
			cc.set("react.controller.action-controller." + c.getCodeName() + ".tickrate", i.getIdealTick());
			
			for(String j : cx.getData().k())
			{
				cc.set("react.controller.action-controller." + c.getCodeName() + ".config." + j, cx.getAbstract(j).toString());
			}
		}
		
		for(Plugin i : Bukkit.getServer().getPluginManager().getPlugins())
		{
			cc.set("server.plugins." + i.getName() + ".version", i.getDescription().getVersion());
			cc.set("server.plugins." + i.getName() + ".description", i.getDescription().getDescription());
			cc.set("server.plugins." + i.getName() + ".main", i.getDescription().getMain());
			cc.set("server.plugins." + i.getName() + ".depend", new GList<String>(i.getDescription().getDepend()));
			cc.set("server.plugins." + i.getName() + ".soft-depend", new GList<String>(i.getDescription().getSoftDepend()));
		}
		
		for(World i : Bukkit.getServer().getWorlds())
		{
			cc.set("server.worlds." + i.getName() + ".loaded-chunks", i.getLoadedChunks().length);
			cc.set("server.worlds." + i.getName() + ".loaded-entities", i.getEntities().size());
			cc.set("server.worlds." + i.getName() + ".players", i.getPlayers().size());
		}
		
		cc.set("react.ai.server.version", Bukkit.getVersion());
		cc.set("react.ai.server.bukkit-version", Bukkit.getBukkitVersion());
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
	@SuppressWarnings("deprecation")
	public String getCodeName()
	{
		Date d = new Date();
		return (d.getHours() + 1) + "-" + (d.getMinutes() + 1) + "-" + (d.getSeconds() + 1) + "-" + d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getYear();
	}
}
