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
import org.cyberpwn.react.api.ReactAPI;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class ReactWorld implements Configurable, Listener
{
	private ClusterConfig cc;
	private World world;
	private Long lastSave;
	private Long saveTime;
	
	public ReactWorld(World world)
	{
		cc = new ClusterConfig();
		this.world = world;
		this.lastSave = System.currentTimeMillis();
		this.saveTime = 0l;
		React.instance().getDataController().load("worlds", this);
		React.instance().register(this);
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("physics.fast-decay", false);
		cc.set("physics.fast-fall", false);
		cc.set("save.auto-save", false);
		cc.set("save.timings.min-wait-minutes", 5);
		cc.set("save.timings.max-wait-minutes", 30);
		cc.set("save.before-save.purge-chunks", true);
		cc.set("save.before-save.cull-drops", true);
		cc.set("save.before-save.cull-entities", true);
		cc.set("save.conditions.save-after-gc", true);
		cc.set("save.conditions.save-while-lagging", true);
	}
	
	public long sinceLastSave()
	{
		return (long)(((double)M.ms())/1000.0/60.0) - (long)(((double)lastSave)/1000.0/60.0); 
	}
	
	public boolean canSave()
	{
		System.out.println(sinceLastSave());
		if(sinceLastSave() > cc.getInt("save.timings.min-wait-minutes"))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean shouldSave(boolean gcd)
	{
		if(canSave() && sinceLastSave() >= cc.getInt("save.timings.max-wait-minutes"))
		{
			Verbose.x("World Save: " + world.getName(), "CONDITION: Max limit reached");
			return true;
		}
		
		if(canSave() && cc.getBoolean("save.conditions.save-while-lagging") && ReactAPI.isLagging())
		{
			Verbose.x("World Save: " + world.getName(), "CONDITION: Lagging. Might as well save.");
			return true;
		}
		
		if(canSave() && cc.getBoolean("save.conditions.save-after-gc") && gcd)
		{
			Verbose.x("World Save: " + world.getName(), "CONDITION: GC Detected. Taking advantage of this time.");
			return true;
		}
		
		if(canSave() && saveTime < 350)
		{
			Verbose.x("World Save: " + world.getName(), "CONDITION: Last save time was fast enough (" + saveTime + "ms)");
			return true;
		}
		
		return false;
	}
	
	public void save(boolean gcd)
	{
		if(!cc.getBoolean("save.auto-save"))
		{
			return;
		}
		
		if(shouldSave(gcd))
		{
			if(cc.getBoolean("save.before-save.purge-chunks"))
			{
				React.instance().getActionController().getActionPurgeChunks().act();
				Verbose.x("World Save: " + world.getName(), "PREREQ: Purging Chunks");
			}
			
			if(cc.getBoolean("save.before-save.cull-drops"))
			{
				React.instance().getActionController().getActionCullDrops().act();
				Verbose.x("World Save: " + world.getName(), "PREREQ: Culling Drops");
			}
			
			if(cc.getBoolean("save.before-save.cull-entities"))
			{
				React.instance().getActionController().getActionCullEntities().act();
				Verbose.x("World Save: " + world.getName(), "PREREQ: Culling Entities");
			}
			
			Verbose.x("World Save: " + world.getName(), "Saving...");
			Long ms = M.ms();
			world.save();
			saveTime = M.ms() - ms;
			lastSave = System.currentTimeMillis();
			Verbose.x("World Save: " + world.getName(), "Saved in " + F.f(saveTime) + "ms");
		}
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
