package org.cyberpwn.react.util;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
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
					e.getBlock().setType(Material.AIR);
				}
				
				else
				{
					e.setCancelled(true);
					l.getBlock().setType(e.getBlock().getType());
					
					new TaskLater(0)
					{
						public void run()
						{
							e.getBlock().setType(Material.AIR);
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
			Cuboid c = new Cuboid(e.getBlock().getLocation().clone().add(new Vector(3, 6, 3)), e.getBlock().getLocation().clone().add(new Vector(-3, -2, -3)));
			final Iterator<Block> it = c.iterator();
			final Material m = e.getBlock().getType();
			
			new Task(0)
			{
				public void run()
				{
					long ms = M.ms();
					
					while(it.hasNext() && M.ms() - ms < 1)
					{
						Block b = it.next();
						
						if(b.getType().equals(m))
						{
							b.breakNaturally();
						}
					}
					
					if(!it.hasNext())
					{
						cancel();
					}
				}
			};
			
		}
	}
}
