package org.cyberpwn.react.action;

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
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.InstabilityCause;

public class ActionHeavyChunk extends Action implements Listener
{
	public ActionHeavyChunk(ActionController actionController)
	{
		super(actionController, Material.GRASS, "hc", "ActionLaggedChunk", 100, "Find Heaviest Chunk", L.ACTION_HEAVYCHUNK, true);
	}
	
	@Override
	public void act()
	{
		
	}
	
	@Override
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
		int m = 0;
		Chunk c = null;
		Location v = null;
		GList<InstabilityCause> cause = new GList<InstabilityCause>();
		
		for(Location i : React.instance().getLagMapController().getMap().getMap().k())
		{
			if(React.instance().getLagMapController().getMap().getMap().get(i) > m)
			{
				if(React.instance().getLagMapController().getMap().getCause().get(i).contains(InstabilityCause.CHUNK_GEN) || React.instance().getLagMapController().getMap().getCause().get(i).contains(InstabilityCause.CHUNKS))
				{
					continue;
				}
				
				if(React.instance().getLagMapController().getMap().getCause().get(i).contains(InstabilityCause.LIQUID))
				{
					continue;
				}
				
				m = React.instance().getLagMapController().getMap().getMap().get(i);
				v = i;
				c = v.getChunk();
				cause.clear();
				cause.addAll(React.instance().getLagMapController().getMap().getCause().get(i));
			}
		}
		
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
		
		if(c == null)
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + "Could not find any chunks that have more than " + lim + " score.");
		}
		
		else
		{
			p.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + "Found " + F.f(m) + " score @ " + c.getWorld().getName() + " [" + c.getX() + ", " + c.getZ() + "]");
			p.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + "Detected: " + ChatColor.GRAY + cause.toString(", "));
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
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		
		cc.set(getCodeName() + ".entity-buffer", 16, "If there are less than this many entities in ALL chunks, react wont find any chunks.");
	}
}
