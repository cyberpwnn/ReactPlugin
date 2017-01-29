package org.cyberpwn.react.action;

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
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ExecutiveIterator;
import org.cyberpwn.react.util.ExecutiveRunnable;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.VersionBukkit;

public class ActionCullEntities extends Action implements Listener
{
	public ActionCullEntities(ActionController actionController)
	{
		super(actionController, Material.SHEARS, "cull-mobs", "ActionCullEntities", 100, "Mob Culler", L.ACTION_CULLENTITIES, true);
	}
	
	@Override
	public void act()
	{
		new ExecutiveIterator<World>(1.0, new GList<World>(Bukkit.getWorlds()), new ExecutiveRunnable<World>()
		{
			@Override
			public void run()
			{
				cull(next());
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				
			}
		});
	}
	
	@EventHandler
	public void onSpawn(EntitySpawnEvent e)
	{
		if(weight(e.getEntity().getLocation().getChunk()) > cc.getInt(getCodeName() + ".max-entities-per-chunk"))
		{
			if(isCullable(e.getEntity()))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
	public void start()
	{
		React.instance().register(this);
	}
	
	@Override
	public void stop()
	{
		React.instance().unRegister(this);
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
		act();
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
	}
	
	public void cull(Chunk c)
	{
		if(c.getEntities().length > cc.getInt(getCodeName() + ".max-entities-per-chunk"))
		{
			int tc = c.getEntities().length - cc.getInt(getCodeName() + ".max-entities-per-chunk");
			
			for(int i = 0; i < tc; i++)
			{
				cull(c.getEntities()[i]);
			}
		}
	}
	
	public void cull(Entity e)
	{
		if(isCullable(e))
		{
			e.remove();
		}
	}
	
	public void cull(World w)
	{
		for(Chunk i : w.getLoadedChunks())
		{
			if(weight(i) > cc.getInt(getCodeName() + ".max-entities-per-chunk"))
			{
				cull(i);
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
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
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
		cc.set(getCodeName() + ".cullable", allow, "Entities allowed to be culled. \nIf you dont want something culled, remove it from here.");
	}
}
