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
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.F;

public class ActionPurgeDrops extends Action implements Listener
{
	public ActionPurgeDrops(ActionController actionController)
	{
		super(actionController, Material.FLINT_AND_STEEL, "purge-drops", "ActionPurgeDrops", 100, "Purge Drops", L.ACTION_PURGEDROPS, true);
		
		aliases.add("purged");
		aliases.add("pd");
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
				if(j.getType().equals(EntityType.DROPPED_ITEM))
				{
					if(!can(j.getLocation()))
					{
						continue;
					}
					
					E.r(j);
					v++;
				}
			}
		}
		
		String msg = ChatColor.WHITE + getName() + ChatColor.GRAY + " purged " + ChatColor.WHITE + F.f(v) + ChatColor.GRAY + " drops in " + ChatColor.WHITE + (System.currentTimeMillis() - ms) + "ms";
		p.sendMessage(Info.TAG + msg);
		notifyOf(msg, p);
	}
}
