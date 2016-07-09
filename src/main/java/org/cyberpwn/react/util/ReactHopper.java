package org.cyberpwn.react.util;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ReactHopper
{
	private final Hopper hopper;
	
	public ReactHopper(Hopper hopper)
	{
		this.hopper = hopper;
	}
	
	public Inventory getInventory()
	{
		return hopper.getInventory();
	}
	
	public Hopper getHopper()
	{
		return hopper;
	}
	
	public boolean canTransfer()
	{
		if((hopper.getBlock().getRelative(getFace()).getState() instanceof InventoryHolder) && !W.inventoryFull(((InventoryHolder) hopper.getBlock().getRelative(getFace()).getState()).getInventory()))
		{
			return true;
		}
		
		return false;
	}
	
	public Inventory getTargetInventory()
	{
		if((hopper.getBlock().getRelative(getFace()).getState() instanceof InventoryHolder))
		{
			return ((InventoryHolder) hopper.getBlock().getRelative(getFace()).getState()).getInventory();
		}
		
		return null;
	}
	
	public boolean hasItems()
	{
		return !W.inventoryEmpty(getInventory());
	}
	
	public void transfer()
	{
		while(canTransfer() && hasItems())
		{
			ItemStack item = getInventory().getItem(W.firstItem(getInventory())).clone();
			getInventory().setItem(W.firstItem(getInventory()), new ItemStack(Material.AIR));
			getTargetInventory().addItem(item);
		}
	}
	
	@SuppressWarnings("deprecation")
	public byte getData()
	{
		return hopper.getRawData();
	}
	
	public BlockFace getFace()
	{
		return getDirection().toFace();
	}
	
	public HopperDirection getDirection()
	{
		if(getData() == 0)
		{
			return HopperDirection.DOWN;
		}
		
		else if(getData() == 2)
		{
			return HopperDirection.NORTH;
		}
		
		else if(getData() == 3)
		{
			return HopperDirection.SOUTH;
		}
		
		else if(getData() == 4)
		{
			return HopperDirection.WEST;
		}
		
		else if(getData() == 5)
		{
			return HopperDirection.EAST;
		}
		
		else
		{
			return HopperDirection.DOWN;
		}
	}
}
