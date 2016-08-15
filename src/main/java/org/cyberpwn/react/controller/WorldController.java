package org.cyberpwn.react.controller;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.PostGCEvent;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.ReactWorld;
import org.cyberpwn.react.util.Task;

public class WorldController extends Controller
{
	private GMap<World, ReactWorld> worlds;
	
	public WorldController(React react)
	{
		super(react);
		
		worlds = new GMap<World, ReactWorld>();
	}
	
	public void start()
	{
		for(World i : getReact().getServer().getWorlds())
		{
			worlds.put(i, new ReactWorld(i));
		}
		
		new Task(10)
		{
			public void run()
			{
				for(World i : worlds.k())
				{
					worlds.get(i).save(false);
				}
			}
		};
	}
	
	public boolean canStack(World w)
	{
		try
		{
			return !worlds.get(w).getConfiguration().getBoolean("entities.disable-stacking");
		}
		
		catch(Exception e)
		{
			return false;
		}
	}
	
	public boolean canTouch(Entity e)
	{
		if(worlds.get(e.getWorld()).getConfiguration().getStringList("entities.assume-no-side-effects").contains(e.getType().toString()))
		{
			return false;
		}
		
		return true;
	}
	
	public void tick()
	{
		
	}
	
	@EventHandler
	public void worldUnload(WorldUnloadEvent e)
	{
		getReact().unRegister(worlds.get(e.getWorld()));
		worlds.remove(e.getWorld());
	}
	
	@EventHandler
	public void gc(PostGCEvent e)
	{
		for(World i : worlds.k())
		{
			worlds.get(i).save(true);
		}
	}
	
	@EventHandler
	public void worldLoad(WorldLoadEvent e)
	{
		worlds.put(e.getWorld(), new ReactWorld(e.getWorld()));
	}
}
