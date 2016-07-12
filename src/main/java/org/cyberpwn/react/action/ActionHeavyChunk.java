package org.cyberpwn.react.action;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;

public class ActionHeavyChunk extends Action implements Listener
{
	public ActionHeavyChunk(ActionController actionController)
	{
		super(actionController, Material.GRASS, "hc", "ActionLaggedChunk", 100, "Find Heaviest Chunk", L.ACTION_HEAVYCHUNK, true);
	}
	
	public void act()
	{
		
	}
	
	public void manual(CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		super.manual(p);
		long ms = System.currentTimeMillis();
		int lim = cc.getInt(getCodeName() + ".entity-buffer");
		Chunk c = null;
		
		for(World i : Bukkit.getWorlds())
		{
			for(Chunk j : i.getLoadedChunks())
			{
				int l = j.getEntities().length;
				
				if(l > lim)
				{
					lim = l;
					c = j;
				}
			}
		}
		
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
		
		if(c == null)
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + "Could not find any chunks that have more than " + lim + " entities.");
		}
		
		else
		{
			p.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + "Found " + lim + " entities @ " + c.getWorld().getName() + " [" + c.getX() + ", " + c.getZ() + "]");
			((Player) p).teleport(safe(c));
		}
	}
	
	public Location safe(Chunk c)
	{
		int level = level(c.getWorld(), c.getBlock(0, 0, 0).getX(), c.getBlock(0, 0, 0).getZ());
		return new Location(c.getWorld(), c.getBlock(0, 0, 0).getX(), level, c.getBlock(0, 0, 0).getZ());
	}
	
	public int level(World w, int x, int z)
	{
		for(int i = 255; i > 0; i--)
		{
			if(!w.getBlockAt(x, i, z).getType().equals(Material.AIR))
			{
				return i + 1;
			}
		}
		
		return 255;
	}
	
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		
		cc.set(getCodeName() + ".entity-buffer", 16, "If there are less than this many entities in ALL chunks, react wont find any chunks.");
	}
}
