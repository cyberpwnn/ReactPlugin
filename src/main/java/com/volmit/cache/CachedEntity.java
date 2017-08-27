package com.volmit.cache;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public interface CachedEntity
{
	public EntityType getType();
	
	public int getTicksLived();
	
	public String getCustomName();
	
	public boolean hasCustomName();
	
	public Location getLocation();
	
	public Vector getVelocity();
	
	public void read(Entity e);
	
	public Entity restore();
}
