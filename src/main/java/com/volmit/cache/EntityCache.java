package com.volmit.cache;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.util.Vector;
import com.volmit.react.util.GList;

public class EntityCache implements Listener
{
	private GList<CachedEntity> cache;
	public static int cachedEntities;
	public static int cachedDrops;
	private int blockRadius;
	
	public EntityCache(int blockRadius)
	{
		this.blockRadius = blockRadius;
		cache = new GList<CachedEntity>();
	}
	
	@EventHandler
	public void on(EntitySpawnEvent e)
	{
		for(Player i : e.getLocation().getWorld().getPlayers())
		{
			if(e.getLocation().distanceSquared(i.getLocation()) > (blockRadius * blockRadius))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	
	public void processWorldCache()
	{
		for(World i : Bukkit.getWorlds())
		{
			for(Entity j : i.getEntities())
			{
				if(!(j instanceof Player))
				{
					for(Player k : i.getPlayers())
					{
						if(j.getLocation().distanceSquared(k.getLocation()) > (blockRadius * blockRadius))
						{
							cache(j);
							break;
						}
					}
				}
			}
			
			cachedDrops = 0;
			cachedEntities = 0;
			
			for(CachedEntity j : cache.copy())
			{
				if(EntityType.values()[j.type].equals(EntityType.DROPPED_ITEM))
				{
					cachedDrops++;
				}
				
				else
				{
					cachedEntities++;
				}
				
				Location l = new Location(Bukkit.getWorld(j.world), j.x, j.y, j.z);
				
				for(Player k : i.getPlayers())
				{
					if(l.distanceSquared(k.getLocation()) <= (blockRadius * blockRadius))
					{
						restore(j);
						break;
					}
				}
			}
		}
	}
	
	public CachedEntity cache(Entity e)
	{
		if(e.isDead())
		{
			return null;
		}
		
		CachedEntity ex = new CachedEntity(e);
		cache.add(ex);
		
		return ex;
	}
	
	public Entity restore(CachedEntity e)
	{
		if(e.done)
		{
			cache.remove(e);
			return null;
		}
		
		cache.remove(e);
		Entity ex = e.restoreEntity();
		ex.setVelocity(new Vector(0, 0, 0));
		
		return ex;
	}
}
