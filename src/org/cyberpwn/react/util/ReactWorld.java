package org.cyberpwn.react.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class ReactWorld implements Configurable, Listener
{
	private ClusterConfig cc;
	private World world;
	
	public ReactWorld(World world)
	{
		cc = new ClusterConfig();
		this.world = world;
		React.instance().getDataController().load("worlds", this);
		React.instance().register(this);
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("physics.fast-decay", false);
		cc.set("physics.fast-fall", false);
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
		return world.getName() + "-settings";
	}
	
	@EventHandler
	public void onBlockFall(final EntityChangeBlockEvent e)
	{
		if(cc.getBoolean("physics.fast-fall"))
		{
			if((e.getEntityType().equals(EntityType.FALLING_BLOCK)))
			{
				final Location l = W.fall(e.getBlock().getLocation());
				
				if(l == null)
				{
					e.setCancelled(true);
					
					new TaskLater(0)
					{
						public void run()
						{
							e.getBlock().setType(Material.AIR);
						}
					};
				}
				
				else
				{
					e.setCancelled(true);
					
					new TaskLater(0)
					{
						public void run()
						{
							e.getBlock().setType(Material.AIR);
							l.getBlock().setType(e.getBlock().getType());
						}
					};
				}
			}
		}
	}
	
	@EventHandler
	public void blockBreak(LeavesDecayEvent e)
	{
		if(cc.getBoolean("physics.fast-decay"))
		{
			for(BlockFace i : BlockFace.values())
			{
				if(e.getBlock().getRelative(i).getType().equals(Material.LEAVES) || e.getBlock().getRelative(i).getType().equals(Material.LEAVES_2))
				{
					e.getBlock().breakNaturally();
					
					for(BlockFace j : BlockFace.values())
					{
						if(e.getBlock().getRelative(j).getType().equals(Material.LEAVES) || e.getBlock().getRelative(j).getType().equals(Material.LEAVES_2))
						{
							e.getBlock().breakNaturally();
						}
					}
				}
			}
		}
	}
}
