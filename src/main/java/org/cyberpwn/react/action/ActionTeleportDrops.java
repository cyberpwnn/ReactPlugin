package org.cyberpwn.react.action;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.L;

public class ActionTeleportDrops extends Action implements Listener
{
	private boolean on;
	
	public ActionTeleportDrops(ActionController actionController)
	{
		super(actionController, Material.STONE, "x", "ActionTeleportDrops", 100, "Teleport Drops", L.ACTION_TELEPORTDROPS, false);
		on = false;
	}
	
	public void turnOn()
	{
		on = true;
	}
	
	public void turnOff()
	{
		on = false;
	}
	
	public void act()
	{
		
	}
	
	public void start()
	{
		getActionController().getReact().register(this);
	}
	
	public void stop()
	{
		getActionController().getReact().unRegister(this);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e)
	{
		if(!on || !e.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
		{
			return;
		}
		
		if(e.getExpToDrop() > 0)
		{
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ORB_PICKUP, 0.3f, 1f);
		}
		
		e.getPlayer().giveExp(e.getExpToDrop());
		e.setExpToDrop(0);
		
		for(ItemStack i : e.getBlock().getDrops(e.getPlayer().getItemInHand()))
		{
			e.getPlayer().getInventory().addItem(i);
		}
		
		e.getBlock().setType(Material.AIR);
	}
	
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
	}
}
