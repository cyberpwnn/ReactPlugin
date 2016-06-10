package org.cyberpwn.react.action;

import java.util.Iterator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.json.VersionBukkit;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.nms.NMS;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.Verbose;

import net.md_5.bungee.api.ChatColor;

public class ActionCullEntities extends Action implements Listener
{
	public ActionCullEntities(ActionController actionController)
	{
		super(actionController, Material.SHEARS, "cull-mobs", "ActionCullEntities", 100, "Mob Culler", L.ACTION_CULLENTITIES, true);
	}
	
	public void act()
	{
		final int[] cpt = new int[] { 0 };
		
		for(World i : getActionController().getReact().getServer().getWorlds())
		{
			cpt[0] += i.getLoadedChunks().length;
		}
		
		cpt[0] /= (idealTick + 1);
		
		for(World i : getActionController().getReact().getServer().getWorlds())
		{
			final Iterator<Chunk> it = new GList<Chunk>(i.getLoadedChunks()).iterator();
			
			new Task(0)
			{
				public void run()
				{
					int[] itx = new int[] { 0 };
					while(it.hasNext() && itx[0] <= cpt[0])
					{
						cull(it.next());
						itx[0]++;
					}
					
					if(!it.hasNext())
					{
						cancel();
					}
				}
			};
		}
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
	
	public void cull(Chunk i)
	{
		int w = weight(i);
		
		if(w >= cc.getInt(getCodeName() + ".max-entities-per-chunk"))
		{
			for(int j = 0; j <= cc.getInt(getCodeName() + ".max-entities-per-chunk"); j++)
			{
				cull(i, w - cc.getInt(getCodeName() + ".max-entities-per-chunk"));
			}
		}
	}
	
	public void cull(Chunk c, int a)
	{
		for(Entity i : c.getEntities())
		{
			if(a <= 0)
			{
				return;
			}
			
			if(cc.getStringList(getCodeName() + ".cullable").contains(i.getType().toString()))
			{
				if(NMS.instance().getEntityName(i) != null)
				{
					if(cc.getBoolean(getCodeName() + ".filter.ignore-named-entities"))
					{
						continue;
					}
				}
				
				if(i.getType().equals(EntityType.VILLAGER))
				{
					if(cc.getBoolean(getCodeName() + ".filter.ignore-villagers"))
					{
						continue;
					}
				}
				
				i.remove();
				a--;
			}
		}
		
		Verbose.x("cull", "Culled " + a + " Entities");
	}
	
	public void start()
	{
		getActionController().getReact().register(this);
	}
	
	public void stop()
	{
		getActionController().getReact().unRegister(this);
	}
	
	public int weight(Chunk chunk)
	{
		int w = 0;
		
		for(Entity i : chunk.getEntities())
		{
			if(cc.getStringList(getCodeName() + ".cullable").contains(i.getType().toString()))
			{
				w++;
			}
		}
		
		return w;
	}
	
	public void cullOne(Chunk c)
	{
		for(Entity i : c.getEntities())
		{
			if(cc.getStringList(getCodeName() + ".cullable").contains(i.getType().toString()))
			{
				i.remove();
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEntitySpawn(EntitySpawnEvent e)
	{
		if(e.getEntityType().equals(EntityType.PLAYER) || !cc.getStringList(getCodeName() + ".cullable").contains(e.getEntityType().toString()))
		{
			return;
		}
		
		if(cc.getBoolean(getCodeName() + ".enable-entity-spawn-radius") && new Area(e.getLocation(), (double) cc.getInt(getCodeName() + ".max-entities-radius")).getNearbyEntities().length > cc.getInt(getCodeName() + ".max-entities-per-radius"))
		{
			cullOne(e.getLocation().getChunk());
			return;
		}
		
		if(weight(e.getEntity().getLocation().getChunk()) >= cc.getInt(getCodeName() + ".max-entities-per-chunk"))
		{
			cullOne(e.getLocation().getChunk());
		}
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
		
		cc.set(getCodeName() + ".max-entities-per-chunk", 28);
		cc.set(getCodeName() + ".filter.ignore-named-entities", false);
		cc.set(getCodeName() + ".filter.ignore-villagers", false);
		cc.set(getCodeName() + ".cullable", allow);
		cc.set(getCodeName() + ".enable-entity-spawn-radius", true);
		cc.set(getCodeName() + ".max-entities-per-radius", 8);
		cc.set(getCodeName() + ".max-entities-radius", 8);
	}
}
