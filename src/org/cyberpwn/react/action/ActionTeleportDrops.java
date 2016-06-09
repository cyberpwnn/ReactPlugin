package org.cyberpwn.react.action;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.cyberpwn.react.controller.ActionController;

public class ActionTeleportDrops extends Action implements Listener
{
	private boolean on;
	
	public ActionTeleportDrops(ActionController actionController)
	{
		super(actionController, Material.STONE, "x", "ActionTeleportDrops", 100, "Teleport Drops", "When enabled, some drops will be teleported and given to the player who broke the block only when the server has too many drops or is lagging.", false);
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
	
	public void onNewConfig()
	{
		super.onNewConfig();
	}
}
