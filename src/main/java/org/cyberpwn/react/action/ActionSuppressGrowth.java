package org.cyberpwn.react.action;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.ManualActionEvent;

public class ActionSuppressGrowth extends Action implements Listener
{
	private GMap<Chunk, Integer> cache;
	private int maxPerInterval;
	
	public ActionSuppressGrowth(ActionController actionController)
	{
		super(actionController, Material.STONE, "x", "ActionSuppressGrowth", 100, "Growth Suppression", L.ACTION_SUPPRESSGROWTH, false);
		
		maxPerInterval = 5;
		cache = new GMap<Chunk, Integer>();
	}
	
	public void start()
	{
		getActionController().getReact().register(this);
	}
	
	public void act()
	{
		cache.clear();
	}
	
	public void manual(final CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		p.sendMessage(Info.TAG + ChatColor.GREEN + getName() + L.MESSAGE_ACTION_FULLY_AUTOMATIC);
	}
	
	@EventHandler
	public void onGrowth(BlockGrowEvent e)
	{
		if(cache.containsKey(e.getBlock().getChunk()))
		{
			cache.put(e.getBlock().getChunk(), cache.get(e.getBlock().getChunk()) + 1);
		}
		
		else
		{
			cache.put(e.getBlock().getChunk(), 1);
		}
		
		if(cache.get(e.getBlock().getChunk()) > maxPerInterval)
		{
			e.setCancelled(true);
		}
	}
	
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		cc.set(getCodeName() + ".max-per-chunk-per-interval", 5, "Max growths per interval defined here.");
	}
	
	public void onReadConfig()
	{
		super.onReadConfig();
		maxPerInterval = cc.getInt(getCodeName() + ".max-per-chunk-per-interval");
	}
}
