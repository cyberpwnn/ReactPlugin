package com.volmit.cache;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import com.volmit.react.sample.TICK;
import com.volmit.react.util.GList;
import com.volmit.react.util.GMap;

public class CachedSet implements Listener
{
	private GMap<Chunk, GList<CachedEntity>> cache;
	private int maxEntitiesPerChunk;
	private int activationRange;
	public static int cached = 0;
	public static int cachedDrops = 0;
	
	public CachedSet(int maxEntitiesPerChunk, int activationRange)
	{
		this.maxEntitiesPerChunk = maxEntitiesPerChunk;
		this.activationRange = activationRange;
		cache = new GMap<Chunk, GList<CachedEntity>>();
	}
	
	@EventHandler
	public void on(EntitySpawnEvent e)
	{
		if(canCache(e.getEntity()))
		{
			e.setCancelled(true);
			e.getEntity().remove();
		}
	}
	
	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		if(cache.containsKey(e.getChunk()))
		{
			for(CachedEntity i : cache.get(e.getChunk()).copy())
			{
				restoreEntity(i);
			}
		}
	}
	
	public void tick()
	{
		if(TICK.tick % 5 == 0)
		{
			cache();
			restore();
		}
	}
	
	private void cache()
	{
		for(World i : Bukkit.getWorlds())
		{
			for(Entity j : i.getEntities())
			{
				if(canCache(j))
				{
					cacheEntity(j);
				}
			}
		}
	}
	
	private boolean canCache(Entity e)
	{
		for(Player k : Bukkit.getOnlinePlayers())
		{
			if(e.getLocation().distanceSquared(k.getLocation()) <= Math.pow(activationRange, 2))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private void restore()
	{
		for(Player i : Bukkit.getOnlinePlayers())
		{
			restore(i);
		}
	}
	
	private void restore(Player p)
	{
		for(Chunk i : nearbyChunks(p.getLocation().getChunk()))
		{
			preverify(i);
			
			for(CachedEntity j : cache.get(i).copy())
			{
				if(j.getLocation().distanceSquared(p.getLocation()) <= Math.pow(activationRange, 2))
				{
					restoreEntity(j);
				}
			}
		}
	}
	
	private GList<Chunk> nearbyChunks(Chunk c)
	{
		GList<Chunk> cx = new GList<Chunk>();
		
		for(Chunk i : cache.k())
		{
			double dfs = (Math.pow(c.getX() - i.getX(), 2) + Math.pow(c.getZ() - i.getZ(), 2)) * 16;
			
			if(dfs <= Math.pow(activationRange + 16, 2))
			{
				cx.add(i);
			}
		}
		
		return cx;
	}
	
	private void cacheEntity(Entity e)
	{
		CachedEntity c = null;
		
		if(e instanceof Item)
		{
			c = new CEntityDrop();
			((CachedEntityDrop) c).read(e);
			cachedDrops++;
		}
		
		else if(e instanceof LivingEntity)
		{
			c = new CEntityLiving();
			((CachedEntityLiving) c).read(e);
			cached++;
		}
		
		else
		{
			c = new CEntity();
			c.read(e);
			cached++;
		}
		
		e.remove();
		preverify(getChunkFor(c));
		cache.get(getChunkFor(c)).add(c);
	}
	
	private Entity restoreEntity(CachedEntity e)
	{
		preverify(getChunkFor(e));
		cache.get(getChunkFor(e)).remove(e);
		
		if(e.getType().equals(EntityType.DROPPED_ITEM))
		{
			cachedDrops--;
		}
		
		else
		{
			cached--;
		}
		
		return e.restore();
	}
	
	private void preverify(Chunk c)
	{
		for(Chunk i : cache.k())
		{
			while(cache.get(i).size() > maxEntitiesPerChunk)
			{
				cache.get(i).remove(0);
			}
			
			if(cache.get(i).isEmpty())
			{
				cache.remove(i);
			}
		}
		
		if(!cache.containsKey(c))
		{
			cache.put(c, new GList<CachedEntity>());
		}
	}
	
	private Chunk getChunkFor(CachedEntity e)
	{
		return e.getLocation().getChunk();
	}
	
	public GMap<Chunk, GList<CachedEntity>> getCache()
	{
		return cache;
	}
	
	public int getMaxEntitiesPerChunk()
	{
		return maxEntitiesPerChunk;
	}
	
	public int getActivationRange()
	{
		return activationRange;
	}
}
