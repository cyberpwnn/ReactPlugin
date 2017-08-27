package com.volmit.cache;

import java.util.Collection;
import org.bukkit.potion.PotionEffect;

public interface CachedEntityLiving extends CachedEntity
{
	public double getMaxHealth();
	
	public double getHealth();
	
	public Collection<PotionEffect> getPotionEffects();
}
