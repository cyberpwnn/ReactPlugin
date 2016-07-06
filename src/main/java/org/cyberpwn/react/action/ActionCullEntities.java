package org.cyberpwn.react.action;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.ExecutiveIterator;
import org.cyberpwn.react.util.ExecutiveRunnable;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ManualActionEvent;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.VersionBukkit;

public class ActionCullEntities extends Action implements Listener
{
	public ActionCullEntities(ActionController actionController)
	{
		super(actionController, Material.SHEARS, "cull-mobs", "ActionCullEntities", 100, "Mob Culler", L.ACTION_CULLENTITIES, true);
	}
	
	public void act()
	{
		new ExecutiveIterator<World>(1l, new GList<World>(Bukkit.getWorlds()), new ExecutiveRunnable<World>()
		{
			public void run()
			{
				cull(next());
			}
		}, new Runnable()
		{
			public void run()
			{
				
			}
		});
	}
	
	@EventHandler
	public void onSpawn(EntitySpawnEvent e)
	{
		cull(e.getEntity().getLocation().getChunk(), e.getEntity());
	}
	
	public void start()
	{
		React.instance().register(this);
	}
	
	public void stop()
	{
		React.instance().unRegister(this);
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
		act();
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
	}
	
	public void cull(Chunk c, Entity j)
	{
		Area a = new Area(j.getLocation(), (double) cc.getInt(getCodeName() + ".max-entities-radius"));
		if(a.getNearbyEntities().length > cc.getInt(getCodeName() + ".max-entities-per-radius"))
		{
			Iterator<Entity> it = new GList<Entity>(a.getNearbyEntities()).iterator();
			
			new Task(0)
			{
				public void run()
				{
					if(it.hasNext())
					{
						Entity i = it.next();
						
						if(a.getNearbyEntities().length > cc.getInt(getCodeName() + ".max-entities-per-radius"))
						{
							if(isCullable(i))
							{
								E.r(i);
							}
						}
					}
					
					else
					{
						cancel();
					}
				}
			};
		}
	}
	
	public void cull(World w)
	{
		if(w.getEntities().size() > cc.getInt(getCodeName() + ".max-entities-per-chunk") * w.getLoadedChunks().length)
		{
			int[] l = new int[] { 0 };
			
			for(Entity i : w.getEntities())
			{
				if(isCullable(i))
				{
					l[0]++;
				}
			}
			
			if(l[0] > cc.getInt(getCodeName() + ".max-entities-per-chunk") * w.getLoadedChunks().length)
			{
				for(Entity i : w.getEntities())
				{
					if(isCullable(i) && l[0] > cc.getInt(getCodeName() + ".max-entities-per-chunk") * w.getLoadedChunks().length)
					{
						l[0]--;
						E.r(i, cc.getBoolean(getCodeName() + ".animate-entity-culls"));
					}
				}
			}
		}
	}
	
	public boolean isCullable(Entity e)
	{
		return cc.getStringList(getCodeName() + ".cullable").contains(e.getType().toString());
	}
	
	public int weight(Chunk chunk)
	{
		int w = 0;
		
		for(Entity i : chunk.getEntities())
		{
			if(isCullable(i))
			{
				w++;
			}
		}
		
		return w;
	}
	
	public void onNewConfig()
	{
		super.onNewConfig();
		GList<String> allow = new GList<String>();
		
		for(EntityType i : EntityType.values())
		{
			if(!VersionBukkit.get().equals(VersionBukkit.V7))
			{
				if(i.equals(i.equals(EntityType.ARMOR_STAND)))
				{
					continue;
				}
			}
			
			if(i.equals(EntityType.PLAYER) || i.equals(EntityType.ARROW) || i.equals(EntityType.BOAT) || i.equals(EntityType.COMPLEX_PART) || i.equals(EntityType.WITHER_SKULL) || i.equals(EntityType.DROPPED_ITEM) || i.equals(EntityType.UNKNOWN) || i.equals(EntityType.THROWN_EXP_BOTTLE) || i.equals(EntityType.EGG) || i.equals(EntityType.ENDER_CRYSTAL) || i.equals(EntityType.ENDER_PEARL) || i.equals(EntityType.ENDER_SIGNAL) || i.equals(EntityType.ITEM_FRAME) || i.equals(EntityType.PAINTING))
			{
				continue;
			}
			
			allow.add(i.toString());
		}
		
		cc.set(getCodeName() + ".max-entities-per-chunk", 16, "The maximum allowed entities per chunk. \nMore entities will spawn, but other entities may be removed.");
		cc.set(getCodeName() + ".filter.ignore-named-entities", false, "Ignore entities that have names from nametags/plugins");
		cc.set(getCodeName() + ".filter.ignore-villagers", false, "Ignore all testificates.");
		cc.set(getCodeName() + ".cullable", allow, "Entities allowed to be culled. \nIf you dont want something culled, remove it from here.");
		cc.set(getCodeName() + ".animate-entity-culls", false, "Kill entities as if the entity died.\nThis will animate deaths instead of blinking them away");
		cc.set(getCodeName() + ".enable-entity-spawn-radius", true, "Use radius culling for entities based on the radius config.");
		cc.set(getCodeName() + ".max-entities-per-radius", 16, "The allowed number of entities per radius check defined below.");
		cc.set(getCodeName() + ".max-entities-radius", 32, "The radius of a radius check in blocks.");
	}
}
