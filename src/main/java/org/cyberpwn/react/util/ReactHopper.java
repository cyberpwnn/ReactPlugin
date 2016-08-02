package org.cyberpwn.react.util;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
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
	
	public Block getBlock()
	{
		return hopper.getBlock();
	}
	
	public boolean isHopperBelow()
	{
		return getBlock().getRelative(BlockFace.DOWN).getState() instanceof Hopper;
	}
	
	public Hopper getBelowHopper()
	{
		if(isHopperBelow())
		{
			return (Hopper) getBlock().getRelative(BlockFace.DOWN).getState();
		}
		
		else
		{
			return null;
		}
	}
	
	public boolean hitsTop(int maxIterations)
	{
		ReactHopper current = this;
		
		for(int i = 0; i < maxIterations; i++)
		{
			if(current.canTransfer() && !W.inventoryFull(current.getInventory()))
			{
				if(current.getTargetInventory().getHolder() instanceof Hopper)
				{
					current = new ReactHopper((Hopper) current.getTargetInventory().getHolder());
				}
				
				else if(current.getTargetInventory().getHolder() instanceof Chest && (((Chest) current.getTargetInventory().getHolder()).getBlock().getRelative(BlockFace.DOWN).getState() instanceof Hopper))
				{
					current = new ReactHopper((Hopper) ((BlockState) current.getTargetInventory().getHolder()).getBlock().getRelative(BlockFace.DOWN).getState());
				}
				
				else if(current.isHopperBelow())
				{
					current = new ReactHopper((Hopper) current.getBelowHopper());
				}
				
				else
				{
					if(current.isHopperBelow())
					{
						return true;
					}
					
					else if(current.getTargetInventory() == null)
					{
						if(current.getFace().equals(BlockFace.DOWN))
						{
							return true;
						}
					}
					
					return current.getFace().equals(BlockFace.DOWN);
				}
			}
			
			else
			{
				if(current.isHopperBelow())
				{
					return true;
				}
				
				else if(current.getTargetInventory() == null)
				{
					return false;
				}
				
				return false;
			}
		}
		
		if(current.getTargetInventory() == null)
		{
			return false;
		}
		
		return current.getFace().equals(BlockFace.DOWN);
	}
	
	public Inventory furthestRoute(int maxIterations)
	{
		ReactHopper current = this;
		
		for(int i = 0; i < maxIterations; i++)
		{
			if(current.canTransfer() && !W.inventoryFull(current.getInventory()))
			{
				if(current.getTargetInventory().getHolder() instanceof Hopper)
				{
					current = new ReactHopper((Hopper) current.getTargetInventory().getHolder());
				}
				
				else if(current.getTargetInventory().getHolder() instanceof Chest && (((Chest) current.getTargetInventory().getHolder()).getBlock().getRelative(BlockFace.DOWN).getState() instanceof Hopper))
				{
					current = new ReactHopper((Hopper) ((BlockState) current.getTargetInventory().getHolder()).getBlock().getRelative(BlockFace.DOWN).getState());
				}
				
				else if(current.isHopperBelow())
				{
					current = new ReactHopper((Hopper) current.getBelowHopper());
				}
				
				else
				{
					if(current.isHopperBelow())
					{
						return new ReactHopper((Hopper) current.getBelowHopper()).getInventory();
					}
					
					else if(current.getTargetInventory() == null)
					{
						return current.getInventory();
					}
					
					return current.getTargetInventory();
				}
			}
			
			else
			{
				if(current.isHopperBelow())
				{
					return new ReactHopper((Hopper) current.getBelowHopper()).getInventory();
				}
				
				else if(current.getTargetInventory() == null)
				{
					return current.getInventory();
				}
				
				return current.getTargetInventory();
			}
		}
		
		if(current.getTargetInventory() == null)
		{
			return current.getInventory();
		}
		
		return current.getTargetInventory();
	}
	
	public boolean canTransfer()
	{
		if((hopper.getBlock().getRelative(getFace()).getState() instanceof InventoryHolder) && !W.inventoryFull(((InventoryHolder) hopper.getBlock().getRelative(getFace()).getState()).getInventory()))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean canTransfer(Inventory from, Inventory to)
	{
		return !W.inventoryEmpty(from) && !W.inventoryFull(to);
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
	
	public boolean transfer(int maxIterations)
	{
		Inventory target = furthestRoute(maxIterations);
		
		if(getInventory().equals(target))
		{
			return false;
		}
		
		transfer(target, maxIterations);
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void transfer()
	{
		while(canTransfer() && hasItems())
		{
			ItemStack item = getInventory().getItem(W.firstItem(getInventory())).clone();
			getInventory().setItem(W.firstItem(getInventory()), new ItemStack(Material.AIR));
			getTargetInventory().addItem(item);
			
			if(getTargetInventory().getHolder() instanceof BlockState)
			{
				((BlockState) getTargetInventory().getHolder()).getBlock().getLocation().getWorld().playEffect(((BlockState) getTargetInventory().getHolder()).getBlock().getLocation().clone().add((2 * Math.random()) - 1, (2 * Math.random()) - 1, (2 * Math.random()) - 1), Effect.TILE_BREAK, item.getTypeId());
			}
		}
	}
	
	public void update(Inventory i)
	{
		if(i.getHolder() instanceof BlockState)
		{
			((BlockState) i.getHolder()).update();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void transfer(Inventory i, int maxIterations)
	{
		while(canTransfer(getInventory(), i))
		{
			try
			{
				ItemStack itemx = getInventory().getItem(W.firstItem(getInventory())).clone();
				
				if(i.getHolder() instanceof BlockState)
				{
					BlockState bs = ((BlockState) i.getHolder());
					
					if(bs instanceof Furnace)
					{
						Furnace f = (Furnace) bs;
						
						if(hitsTop(maxIterations))
						{
							if(f.getInventory().getItem(0) == null ||f.getInventory().getItem(0).getType().equals(Material.AIR))
							{
								ItemStack item = getInventory().getItem(W.firstItem(getInventory())).clone();
								getInventory().setItem(W.firstItem(getInventory()), new ItemStack(Material.AIR));
								f.getInventory().setItem(0, item);
								update(i);
								update(getInventory());
							}
						}
						
						else
						{
							if(f.getInventory().getItem(1) == null ||f.getInventory().getItem(1).getType().equals(Material.AIR))
							{
								ItemStack item = getInventory().getItem(W.firstItem(getInventory())).clone();
								getInventory().setItem(W.firstItem(getInventory()), new ItemStack(Material.AIR));
								f.getInventory().setItem(1, item);
								update(i);
								update(getInventory());
							}
						}
					}
					
					else
					{
						ItemStack item = getInventory().getItem(W.firstItem(getInventory())).clone();
						getInventory().setItem(W.firstItem(getInventory()), new ItemStack(Material.AIR));
						i.addItem(item);
						update(i);
						update(getInventory());
					}
				}
				
				if(getTargetInventory().getHolder() instanceof BlockState)
				{
					for(int j = 0; j < 8; j++)
					{
						((BlockState) i.getHolder()).getBlock().getLocation().getWorld().playEffect(((BlockState) i.getHolder()).getBlock().getLocation().clone().add(0.5, 1, 0.5), Effect.TILE_BREAK, itemx.getTypeId());
					}
				}
			}
			
			catch(Exception e)
			{
				break;
			}
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
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && (o instanceof ReactHopper) && ((ReactHopper) o).getHopper().getBlock().equals(getHopper().getBlock()))
		{
			return true;
		}
		
		return false;
	}
}
