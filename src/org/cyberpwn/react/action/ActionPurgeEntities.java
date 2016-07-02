package org.cyberpwn.react.action;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.nms.NMS;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ManualActionEvent;
import org.cyberpwn.react.util.VersionBukkit;

public class ActionPurgeEntities extends Action implements Listener
{
	public ActionPurgeEntities(ActionController actionController)
	{
		super(actionController, Material.FLINT_AND_STEEL, "purge-mobs", "ActionPurgeEntities", 100, "Mob Purger", L.ACTION_PURGEENTITIES, true);
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
		
		for(World i : Bukkit.getWorlds())
		{
			for(Entity j : i.getEntities())
			{
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
					
					if(NMS.instance().getEntityName(j) != null)
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
					
					E.r(j);
				}
			}
		}
		
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
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
		
		cc.set(getCodeName() + ".cullable", allow);
		cc.set(getCodeName() + ".filter.ignore-named-entities", false);
		cc.set(getCodeName() + ".filter.ignore-villagers", false);
	}
}
