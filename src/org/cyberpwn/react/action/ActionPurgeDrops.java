package org.cyberpwn.react.action;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;

import net.md_5.bungee.api.ChatColor;

public class ActionPurgeDrops extends Action implements Listener
{
	public ActionPurgeDrops(ActionController actionController)
	{
		super(actionController, Material.FLINT_AND_STEEL, "purge-drops", "ActionPurgeDrops", 100, "Purge Drops", "Removes ALL DROPS in ALL WORLDS", true);
	}
	
	public void act()
	{
		
	}
	
	public void manual(CommandSender p)
	{
		super.manual(p);
		long ms = System.currentTimeMillis();
		
		for(World i : Bukkit.getWorlds())
		{
			for(Entity j : i.getEntities())
			{
				if(j.getType().equals(EntityType.DROPPED_ITEM))
				{
					j.remove();
				}
			}
		}
		
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
	}
}
