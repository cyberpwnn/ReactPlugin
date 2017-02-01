package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class IDrop
{
	private IStack stack;
	private Location location;
	private Vector velocity;
	private int alive;
	private long tcc;
	
	public IDrop(Item item)
	{
		stack = new IStack(item.getItemStack());
		location = item.getLocation();
		velocity = item.getVelocity();
		alive = item.getTicksLived();
		tcc = System.currentTimeMillis();
		
		if(alive < 1)
		{
			alive = 1;
		}
	}
	
	public void create()
	{
		Item e = location.getWorld().dropItem(location, stack.toItemStack());
		e.setVelocity(velocity);
		e.setTicksLived(alive);
	}
	
	public IStack getStack()
	{
		return stack;
	}
	
	public void setStack(IStack stack)
	{
		this.stack = stack;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public Vector getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(Vector velocity)
	{
		this.velocity = velocity;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + alive;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((stack == null) ? 0 : stack.hashCode());
		result = prime * result + ((velocity == null) ? 0 : velocity.hashCode());
		return result;
	}
	
	public void update()
	{
		long ms = M.ms();
		long diff = ms - tcc;
		int ticks = (int) (diff / 50);
		
		alive += ticks;
		tcc = ms;
	}
	
	public boolean shouldDie()
	{
		int f = Bukkit.spigot().getConfig().getInt("world-settings.default.item-despawn-rate");
		
		return alive >= f;
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
		IDrop other = (IDrop) obj;
		if(alive != other.alive)
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
		if(stack == null)
		{
			if(other.stack != null)
			{
				return false;
			}
		}
		else if(!stack.equals(other.stack))
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
