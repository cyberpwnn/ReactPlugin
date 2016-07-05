package org.cyberpwn.react.api;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.LivingEntity;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ReactEvent;

public class ChunkPurgeEvent extends ReactEvent
{
	private GList<Chunk> ignore;
	
	public ChunkPurgeEvent()
	{
		this.ignore = new GList<Chunk>();
	}
	
	public boolean isIgnored(Chunk chunk)
	{
		return ignore.contains(chunk);
	}
	
	public boolean isIgnored(LivingEntity entity)
	{
		return ignore.contains(entity.getLocation().getChunk());
	}
	
	public void ignore(LivingEntity entity)
	{
		if(!isIgnored(entity))
		{
			ignore.add(entity.getLocation().getChunk());
		}
	}
	
	public void ignore(Chunk chunk)
	{
		if(!isIgnored(chunk))
		{
			ignore.add(chunk);
		}
	}

	public List<Chunk> getIgnore()
	{
		return ignore;
	}
}
