package org.cyberpwn.react.action;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;

import net.md_5.bungee.api.ChatColor;

public class ActionSuppressTnt extends Action implements Listener
{
	private boolean frozen;
	
	public ActionSuppressTnt(ActionController actionController)
	{
		super(actionController, Material.TNT, "purge-tnt", "ActionSuppressTnt", 20, "TNT Suppression", L.ACTION_SUPPRESSTNT, true);
	}
	
	public void act()
	{
		
	}
	
	public void freeze()
	{
		if(cc.getBoolean(getCodeName() + ".freeze-all-tnt-on-lag"))
		{
			frozen = true;
		}
	}
	
	public void unfreeze()
	{
		frozen = false;
	}
	
	public void manual(final CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		super.manual(p);
		final long ms = System.currentTimeMillis();
		freeze();
		
		getActionController().getReact().scheduleSyncTask(20, new Runnable()
		{
			@Override
			public void run()
			{
				unfreeze();
				p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
			}
		});
	}
	
	public void start()
	{
		getActionController().getReact().register(this);
	}
	
	public void stop()
	{
		getActionController().getReact().unRegister(this);
	}
	
	public void onNewConfig()
	{
		super.onNewConfig();
		
		cc.set(getCodeName() + ".freeze-all-tnt-on-lag", true);
		cc.set(getCodeName() + ".max-tnt-per-chunk", 16);
	}
	
	@EventHandler
	public void onTNT(ExplosionPrimeEvent e)
	{
		if(frozen)
		{
			e.setCancelled(true);
		}
		
		else
		{
			int ims = 0;
			
			for(Entity i : e.getEntity().getLocation().getChunk().getEntities())
			{
				if(i.getType().equals(EntityType.PRIMED_TNT))
				{
					ims++;
				}
				
				if(ims > cc.getInt(getCodeName() + ".max-tnt-per-chunk"))
				{
					e.setCancelled(true);
					break;
				}
			}
		}
	}
}
