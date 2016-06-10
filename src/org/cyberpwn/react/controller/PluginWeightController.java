package org.cyberpwn.react.controller;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.object.GList;
import org.cyberpwn.react.object.GMap;
import org.cyberpwn.react.timings.TimingsReport;
import org.cyberpwn.react.util.F;

public class PluginWeightController extends Controller implements Configurable
{
	private GMap<String, Double> times;
	private GList<String> hit;
	private int wait;
	private double high;
	private boolean notify;
	private ClusterConfig cc;
	
	public PluginWeightController(React react)
	{
		super(react);
		
		cc = new ClusterConfig();
		hit = new GList<String>();
		times = new GMap<String, Double>();
		wait = 1200;
		high = 9.5;
		notify = true;
	}
	
	public void start()
	{
		react.getDataController().load(null, this);
	}
	
	public void reportProblems()
	{
		GList<String> order = new GList<String>();
		GList<Double> sortd = times.v();
		Collections.sort(sortd);
		
		for(Double i : sortd)
		{
			for(String j : times.k())
			{
				if(times.get(j).equals(i))
				{
					order.add(j);
					break;
				}
			}
		}
		
		if(order.isEmpty())
		{
			return;
		}
		
		Collections.reverse(order);
		
		for(String i : order)
		{
			if(times.get(i) >= high)
			{
				if(hit.contains(i))
				{
					continue;
				}
				
				for(Player j : getReact().onlinePlayers())
				{
					if(j.hasPermission(Info.PERM_MONITOR))
					{
						j.sendMessage(Info.TAG + ChatColor.RED + "WARNING: " + ChatColor.GOLD + i + ChatColor.RED + " is using " + F.f(times.get(i), 4) + "ms");
					}
				}
				
				hit.add(i);
			}
		}
	}
	
	public void scanPaper()
	{
		TimingsReport pt = getReact().getTimingsController().getReports().get("Plugin");
		
		if(pt == null)
		{
			return;
		}
		
		if(times.containsKey("React"))
		{
			getReact().getSampleController().getSampleReactionTime().setOpn(times.get("React"));
		}
		
		times.clear();
		
		for(Plugin i : Bukkit.getPluginManager().getPlugins())
		{
			for(String j : pt.getData().keySet())
			{
				if(j.equals(i.getName()))
				{
					if(!times.containsKey(i.getName()))
					{
						times.put(i.getName(), pt.getData().get(j).getMs());
					}
					
					else
					{
						times.put(i.getName(), pt.getData().get(j).getMs() + times.get(i.getName()));
					}
					
					break;
				}
			}
		}
		
		if(!notify)
		{
			return;
		}
		
		reportProblems();
		
		wait--;
		
		if(wait <= 0)
		{
			wait = cc.getInt("timings.notifier.flush-list-delay");
			hit.clear();
		}
	}
	
	public void scan()
	{
		if(!getReact().getTimingsController().supported())
		{
			scanPaper();
		}
		
		TimingsReport pt = getReact().getTimingsController().getReports().get("Plugin Task");
		TimingsReport pe = getReact().getTimingsController().getReports().get("Plugin Event");
		
		if(pt == null || pe == null)
		{
			return;
		}
		
		if(times.containsKey("React"))
		{
			getReact().getSampleController().getSampleReactionTime().setOpn(times.get("React"));
		}
		
		times.clear();
		
		for(Plugin i : Bukkit.getPluginManager().getPlugins())
		{
			String pluginName = i.getName() + " " + "v" + i.getDescription().getVersion();
			
			for(String j : pt.getData().keySet())
			{
				if(j.equals(pluginName))
				{
					if(!times.containsKey(i.getName()))
					{
						times.put(i.getName(), pt.getData().get(j).getMs());
					}
					
					else
					{
						times.put(i.getName(), pt.getData().get(j).getMs() + times.get(i.getName()));
					}
					
					break;
				}
			}
			
			for(String j : pe.getData().keySet())
			{
				if(j.equals(pluginName))
				{
					if(!times.containsKey(i.getName()))
					{
						times.put(i.getName(), pe.getData().get(j).getMs());
					}
					
					else
					{
						times.put(i.getName(), pe.getData().get(j).getMs() + times.get(i.getName()));
					}
					
					break;
				}
			}
		}
		
		if(!notify)
		{
			return;
		}
		
		reportProblems();
		
		wait--;
		
		if(wait <= 0)
		{
			wait = cc.getInt("timings.notifier.flush-list-delay");
			hit.clear();
		}
	}
	
	public void report(CommandSender sender)
	{
		GList<String> order = new GList<String>();
		GList<Double> sortd = times.v();
		Collections.sort(sortd);
		
		for(Double i : sortd)
		{
			for(String j : times.k())
			{
				if(times.get(j).equals(i))
				{
					order.add(j);
					break;
				}
			}
		}
		
		sender.sendMessage(Info.HR);
		
		for(String i : order)
		{
			sender.sendMessage(ChatColor.AQUA + i + ": " + ChatColor.YELLOW + F.f(times.get(i), 4) + "ms");
		}
		
		sender.sendMessage(Info.HR);
	}
	
	public void report(CommandSender sender, Plugin plugin)
	{
		if(times.containsKey(plugin.getName()))
		{
			sender.sendMessage(Info.TAG + ChatColor.AQUA + plugin.getName() + ": " + ChatColor.YELLOW + F.f(times.get(plugin.getName()), 4) + "ms");
		}
		
		else
		{
			sender.sendMessage(Info.TAG + ChatColor.RED + "No timings data for " + plugin.getName());
		}
	}
	
	public boolean enabledTimings()
	{
		return cc.getBoolean("timings.processing.enabled");
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("timings.processing.enabled", true);
		cc.set("timings.notifier.flush-list-delay", 1200);
		cc.set("timings.notifier.enable", true);
		cc.set("timings.notifier.considered-high-ms", 9.5);
	}
	
	@Override
	public void onReadConfig()
	{
		wait = cc.getInt("timings.notifier.flush-list-delay");
		notify = cc.getBoolean("timings.notifier.enable");
		high = cc.getDouble("timings.notifier.considered-high-ms");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "timings-controller";
	}
}
