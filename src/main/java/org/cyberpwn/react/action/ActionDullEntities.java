package org.cyberpwn.react.action;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.nms.NMSX;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.VersionBukkit;
import org.cyberpwn.react.util.W;

public class ActionDullEntities extends Action implements Listener
{
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	
	public ActionDullEntities(ActionController actionController)
	{
		super(actionController, Material.SHEARS, "dull-mobs", "ActionDullEntities", 100, "Mob Duller", L.ACTION_DULLENTITIES, true);
		
		aliases.add("dm");
		aliases.add("dull");
	}
	
	@Override
	public void act()
	{
		
	}
	
	public void dull()
	{
		
	}
	
	@Override
	public void start()
	{
		React.instance().register(this);
	}
	
	public void setDull(LivingEntity e, boolean dull)
	{
		NMSX.setAi(e, !dull);
	}
	
	public boolean isTamed(Entity e)
	{
		if(e instanceof LivingEntity)
		{
			LivingEntity ee = (LivingEntity) e;
			
			if(ee instanceof Tameable)
			{
				Tameable t = (Tameable) ee;
				
				if(t.isTamed())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void stop()
	{
		React.instance().unRegister(this);
	}
	
	public void dull(Chunk c)
	{
		if(!cc.getBoolean("component.enable"))
		{
			return;
		}
		
		boolean cx = true;
		int r = cc.getInt(getCodeName() + ".chunk-distance");
		
		if(r < 1)
		{
			r = 1;
		}
		
		if(r == 1)
		{
			for(Entity j : c.getEntities())
			{
				if(j instanceof Player)
				{
					cx = false;
					break;
				}
			}
		}
		
		else
		{
			for(Chunk i : W.chunkRadius(c, r - 1))
			{
				for(Entity j : i.getEntities())
				{
					if(j instanceof Player)
					{
						cx = false;
						break;
					}
				}
				
				if(!cx)
				{
					break;
				}
			}
		}
		
		if(cx)
		{
			for(Entity i : c.getEntities())
			{
				if(isDullable(i))
				{
					setDull((LivingEntity) i, true);
				}
			}
		}
		
		else
		{
			for(Entity i : c.getEntities())
			{
				if(isDullable(i))
				{
					setDull((LivingEntity) i, false);
				}
			}
		}
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
		String msg = ChatColor.WHITE + getName() + ChatColor.GRAY + " in " + ChatColor.WHITE + (System.currentTimeMillis() - ms) + "ms";
		p.sendMessage(Info.TAG + msg);
		notifyOf(msg, p);
	}
	
	public boolean isDullable(Entity e)
	{
		if(e instanceof LivingEntity)
		{
			if(isTamed(e) && cc.getBoolean(getCodeName() + ".filter.ignore-tamed-entities"))
			{
				return false;
			}
			
			if(!can(e.getLocation()))
			{
				return false;
			}
			
			return cc.getStringList(getCodeName() + ".dullable").contains(e.getType().toString());
		}
		
		return false;
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		GList<String> allow = new GList<String>();
		
		for(EntityType i : EntityType.values())
		{
			if(VersionBukkit.tc() && i.equals(EntityType.ARMOR_STAND))
			{
				continue;
			}
			
			if(i.equals(EntityType.PLAYER) || i.equals(EntityType.ARROW) || i.equals(EntityType.BOAT) || i.equals(EntityType.COMPLEX_PART) || i.equals(EntityType.WITHER_SKULL) || i.equals(EntityType.DROPPED_ITEM) || i.equals(EntityType.UNKNOWN) || i.equals(EntityType.THROWN_EXP_BOTTLE) || i.equals(EntityType.EGG) || i.equals(EntityType.ENDER_CRYSTAL) || i.equals(EntityType.ENDER_PEARL) || i.equals(EntityType.ENDER_SIGNAL) || i.equals(EntityType.ITEM_FRAME) || i.equals(EntityType.PAINTING))
			{
				continue;
			}
			
			allow.add(i.toString());
		}
		
		cc.set("component.enable", false, "ABOUT " + getName() + "\n" + getDescription() + "\n\nYou can disable " + getName() + " here.");
		cc.set(getCodeName() + ".filter.ignore-tamed-entities", true, "Ignore tamed entities");
		cc.set(getCodeName() + ".chunk-distance", 2, "Max chunk distance a player must be to keep entities with ai");
		cc.set(getCodeName() + ".dullable", allow, "Entities allowed to be dulled. \nIf you dont want something culled, remove it from here.");
	}
}
