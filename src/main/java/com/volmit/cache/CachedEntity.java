package com.volmit.cache;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class CachedEntity
{
	protected int type;
	protected int ticks;
	protected double hp;
	protected double x;
	protected double y;
	protected double z;
	protected double w;
	protected double h;
	protected String world;
	protected String name;
	protected int matid;
	protected byte dat;
	protected int amt;
	protected short dmg;
	protected boolean done;
	
	@SuppressWarnings("deprecation")
	public CachedEntity(Entity entity)
	{
		done = false;
		type = entity.getType().ordinal();
		ticks = entity.getTicksLived();
		hp = (entity instanceof LivingEntity) ? ((LivingEntity) entity).getHealth() : 0;
		x = entity.getLocation().getX();
		y = entity.getLocation().getY();
		z = entity.getLocation().getZ();
		w = entity.getLocation().getYaw();
		h = entity.getLocation().getPitch();
		world = entity.getLocation().getWorld().getName();
		name = entity.getCustomName() == null ? "" : entity.getCustomName();
		matid = (entity instanceof Item) ? ((Item) entity).getItemStack().getTypeId() : -1;
		dat = (entity instanceof Item) ? ((Item) entity).getItemStack().getData().getData() : -1;
		amt = (entity instanceof Item) ? ((Item) entity).getItemStack().getAmount() : -1;
		dmg = (entity instanceof Item) ? ((Item) entity).getItemStack().getDurability() : -1;
		entity.remove();
	}
	
	@SuppressWarnings("deprecation")
	public Entity restoreEntity()
	{
		if(done)
		{
			return null;
		}
		
		EntityType etype = EntityType.values()[type];
		Location l = new Location(Bukkit.getWorld(world), x, y, z, (float) w, (float) h);
		Entity e = null;
		
		if(etype.equals(EntityType.DROPPED_ITEM))
		{
			e = l.getWorld().dropItem(l, new ItemStack(matid, amt, dmg, dat));
		}
		
		else
		{
			e = l.getWorld().spawnEntity(l, etype);
		}
		
		e.setTicksLived(ticks + 1);
		
		if(e instanceof LivingEntity)
		{
			((LivingEntity) e).setHealth(((LivingEntity) e).getMaxHealth() > hp ? ((LivingEntity) e).getMaxHealth() : hp);
		}
		
		if(!name.equals(""))
		{
			e.setCustomNameVisible(true);
			e.setCustomName(name);
		}
		
		done = true;
		
		return e;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + amt;
		result = prime * result + dat;
		result = prime * result + dmg;
		result = prime * result + (done ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(h);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(hp);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + matid;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ticks;
		result = prime * result + type;
		temp = Double.doubleToLongBits(w);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		CachedEntity other = (CachedEntity) obj;
		if(amt != other.amt)
		{
			return false;
		}
		if(dat != other.dat)
		{
			return false;
		}
		if(dmg != other.dmg)
		{
			return false;
		}
		if(done != other.done)
		{
			return false;
		}
		if(Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h))
		{
			return false;
		}
		if(Double.doubleToLongBits(hp) != Double.doubleToLongBits(other.hp))
		{
			return false;
		}
		if(matid != other.matid)
		{
			return false;
		}
		if(name == null)
		{
			if(other.name != null)
			{
				return false;
			}
		}
		else if(!name.equals(other.name))
		{
			return false;
		}
		if(ticks != other.ticks)
		{
			return false;
		}
		if(type != other.type)
		{
			return false;
		}
		if(Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
		{
			return false;
		}
		if(world == null)
		{
			if(other.world != null)
			{
				return false;
			}
		}
		else if(!world.equals(other.world))
		{
			return false;
		}
		if(Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
		{
			return false;
		}
		if(Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
		{
			return false;
		}
		if(Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
		{
			return false;
		}
		return true;
	}
}
