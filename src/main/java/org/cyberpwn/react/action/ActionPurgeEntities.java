package org.cyberpwn.react.action;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.nms.NMSX;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.VersionBukkit;

public class ActionPurgeEntities extends Action implements Listener
{
	public ActionPurgeEntities(ActionController actionController)
	{
		super(actionController, Material.FLINT_AND_STEEL, "purge-mobs", "ActionPurgeEntities", 100, "Mob Purger", L.ACTION_PURGEENTITIES, true);
		
		React.instance().register(this);
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
		int v = 0;
		
		for(World i : Bukkit.getWorlds())
		{
			for(Entity j : i.getEntities())
			{
				if(!can(j.getLocation()))
				{
					continue;
				}
				
				if(cc.getStringList(getCodeName() + ".cullable").contains(j.getType().toString()))
				{
					if(j.getType().toString().equals("PLAYER"))
					{
						continue;
					}
					
					if(j.getType().toString().equals("COMPLEX_PART"))
					{
						continue;
					}
					
					if(j.getType().toString().equals("PAINTING"))
					{
						continue;
					}
					
					if(j.getType().toString().equals("PAINTING"))
					{
						continue;
					}
					
					if(j.getType().toString().equals("ITEM_FRAME"))
					{
						continue;
					}
					
					if(j.getType().toString().equals("ARMOR_STAND"))
					{
						continue;
					}
					
					if(j.getType().toString().equals("WITHER_SKULL"))
					{
						continue;
					}
					
					if(NMSX.getEntityName(j) != null)
					{
						if(cc.getBoolean(getCodeName() + ".filter.ignore-named-entities"))
						{
							continue;
						}
					}
					
					if(j.getType().equals(EntityType.VILLAGER))
					{
						if(cc.getBoolean(getCodeName() + ".filter.ignore-villagers"))
						{
							continue;
						}
					}
					
					if(isTamed(j) && cc.getBoolean(getCodeName() + ".filter.ignore-tamed-entities"))
					{
						continue;
					}
					
					E.r(j);
					v++;
				}
			}
		}
		
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms (" + F.f(v) + " entities)");
	}
	
	@EventHandler
	public void on(PlayerCommandPreprocessEvent e)
	{
		if(e.getMessage().equalsIgnoreCase("/killall all") && cc.getBoolean(getCodeName() + ".override.replace-kill-command"))
		{
			if(e.getPlayer().hasPermission(Info.PERM_ACT))
			{
				e.getPlayer().sendMessage("caught");
				manual(e.getPlayer());
				e.setCancelled(true);
			}
		}
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
		
		cc.set(getCodeName() + ".cullable", allow, "Remove entities from here you dont want being purged.");
		cc.set(getCodeName() + ".filter.ignore-tamed-entities", true, "Ignore tamed entities");
		cc.set(getCodeName() + ".filter.ignore-named-entities", false, "Ignore entities with names.");
		cc.set(getCodeName() + ".filter.ignore-villagers", false, "Ignore testificates.");
		cc.set(getCodeName() + ".override.replace-kill-command", true, "Replaces /killall all functionality with /re act purge-mobs");
	}
}
