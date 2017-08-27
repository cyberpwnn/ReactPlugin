package com.volmit.cache;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CEntity implements CachedEntity
{
	protected EntityType type;
	protected int ticksLived;
	protected String customName;
	protected boolean hasCustomName;
	protected Location location;
	protected Vector velocity;
	
	public CEntity()
	{
		type = null;
		ticksLived = 1;
		customName = null;
		hasCustomName = false;
		location = null;
		velocity = null;
	}
	
	@Override
	public EntityType getType()
	{
		return type;
	}
	
	@Override
	public int getTicksLived()
	{
		return ticksLived;
	}
	
	@Override
	public String getCustomName()
	{
		return customName;
	}
	
	@Override
	public boolean hasCustomName()
	{
		return hasCustomName;
	}
	
	@Override
	public Location getLocation()
	{
		return location;
	}
	
	@Override
	public Vector getVelocity()
	{
		return velocity;
	}
	
	@Override
	public void read(Entity e)
	{
		type = e.getType();
		ticksLived = e.getTicksLived() + 1;
		customName = e.getCustomName();
		hasCustomName = e.isCustomNameVisible();
		location = e.getLocation();
		velocity = e.getVelocity();
	}
	
	@Override
	public Entity restore()
	{
		Entity e = null;
		
		if(type.equals(EntityType.DROPPED_ITEM))
		{
			e = location.getWorld().dropItem(location, new ItemStack(Material.STONE, 64));
		}
		
		else
		{
			e = location.getWorld().spawnEntity(location, type);
		}
		
		if(hasCustomName)
		{
			e.setCustomName(customName);
			e.setCustomNameVisible(hasCustomName);
		}
		
		e.setTicksLived(ticksLived);
		e.teleport(location);
		e.setVelocity(velocity);
		
		return e;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customName == null) ? 0 : customName.hashCode());
		result = prime * result + (hasCustomName ? 1231 : 1237);
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ticksLived;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((velocity == null) ? 0 : velocity.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		if(obj == null)
		{
			return false;
		}
		if(getClass() != obj.getClass())
		{
			return false;
		}
		CEntity other = (CEntity) obj;
		if(customName == null)
		{
			if(other.customName != null)
			{
				return false;
			}
		}
		else if(!customName.equals(other.customName))
		{
			return false;
		}
		if(hasCustomName != other.hasCustomName)
		{
			return false;
		}
		if(location == null)
		{
			if(other.location != null)
			{
				return false;
			}
		}
		else if(!location.equals(other.location))
		{
			return false;
		}
		if(ticksLived != other.ticksLived)
		{
			return false;
		}
		if(type != other.type)
		{
			return false;
		}
		if(velocity == null)
		{
			if(other.velocity != null)
			{
				return false;
			}
		}
		else if(!velocity.equals(other.velocity))
		{
			return false;
		}
		return true;
	}
	
}
