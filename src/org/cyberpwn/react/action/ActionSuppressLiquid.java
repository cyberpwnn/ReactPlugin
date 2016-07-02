package org.cyberpwn.react.action;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ManualActionEvent;

public class ActionSuppressLiquid extends Action implements Listener
{
	private boolean frozen;
	
	public ActionSuppressLiquid(ActionController actionController)
	{
		super(actionController, Material.WATER_BUCKET, "cull-liquid", "ActionSuppressLiquid", 20, "Liquid Suppression", L.ACTION_SUPPRESSLIQUID, true);
	}
	
	public void act()
	{
		
	}
	
	public void freeze()
	{
		if(cc.getBoolean(getCodeName() + ".freeze-all-liquid-on-lag"))
		{
			frozen = true;
		}
	}
	
	public void unfreeze()
	{
		frozen = false;
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
		
		cc.set(getCodeName() + ".freeze-all-liquid-on-lag", true);
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
		
		getActionController().getReact().scheduleSyncTask(40, new Runnable()
		{
			@Override
			public void run()
			{
				unfreeze();
				p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
			}
		});
	}
	
	@EventHandler
	public void onRedstone(BlockPhysicsEvent e)
	{
		if(frozen)
		{
			if(e.getChangedType().equals(Material.WATER) || e.getChangedType().equals(Material.STATIONARY_WATER) || e.getChangedType().equals(Material.LAVA) || e.getChangedType().equals(Material.STATIONARY_LAVA))
			{
				e.setCancelled(true);
			}
			
			if(e.getBlock().equals(Material.WATER) || e.getChangedType().equals(Material.STATIONARY_WATER) || e.getChangedType().equals(Material.LAVA) || e.getChangedType().equals(Material.STATIONARY_LAVA))
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onLiquid(BlockFromToEvent e)
	{
		if(frozen)
		{
			if(e.getToBlock().getType().equals(Material.WATER) || e.getToBlock().getType().equals(Material.STATIONARY_WATER) || e.getToBlock().getType().equals(Material.LAVA) || e.getToBlock().getType().equals(Material.STATIONARY_LAVA))
			{
				e.setCancelled(true);
			}
			
			if(e.getBlock().getType().equals(Material.WATER) || e.getToBlock().getType().equals(Material.STATIONARY_WATER) || e.getToBlock().getType().equals(Material.LAVA) || e.getToBlock().getType().equals(Material.STATIONARY_LAVA))
			{
				e.setCancelled(true);
			}
		}
	}
}
