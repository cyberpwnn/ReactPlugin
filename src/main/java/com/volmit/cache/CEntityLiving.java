package com.volmit.cache;

import java.util.Collection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class CEntityLiving extends CEntity implements CachedEntityLiving
{
	protected double maxHealth;
	protected double health;
	protected Collection<PotionEffect> effects;
	
	@Override
	public double getMaxHealth()
	{
		return maxHealth;
	}
	
	@Override
	public double getHealth()
	{
		return health;
	}
	
	@Override
	public Collection<PotionEffect> getPotionEffects()
	{
		return effects;
	}
	
	@Override
	public void read(Entity e)
	{
		super.read(e);
		LivingEntity l = (LivingEntity) e;
		health = l.getHealth();
		maxHealth = l.getMaxHealth();
		effects = l.getActivePotionEffects();
	}
	
	@Override
	public Entity restore()
	{
		LivingEntity e = (LivingEntity) super.restore();
		
		e.setMaxHealth(maxHealth);
		e.setHealth(health);
		e.addPotionEffects(effects);
		
		return e;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		long temp;
		temp = Double.doubleToLongBits(health);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxHealth);
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
		if(!super.equals(obj))
		{
			return false;
		}
		if(getClass() != obj.getClass())
		{
			return false;
		}
		CEntityLiving other = (CEntityLiving) obj;
		if(effects == null)
		{
			if(other.effects != null)
			{
				return false;
			}
		}
		else if(!effects.equals(other.effects))
		{
			return false;
		}
		if(Double.doubleToLongBits(health) != Double.doubleToLongBits(other.health))
		{
			return false;
		}
		if(Double.doubleToLongBits(maxHealth) != Double.doubleToLongBits(other.maxHealth))
		{
			return false;
		}
		return true;
	}
	
}
