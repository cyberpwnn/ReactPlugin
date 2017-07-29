package org.cyberpwn.react.controller;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.PostGCEvent;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.HandledEvent;
import org.cyberpwn.react.util.Q;
import org.cyberpwn.react.util.Q.P;
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
	
	@Override
	public void start()
	{
		for(World i : getReact().getServer().getWorlds())
		{
			worlds.put(i, new ReactWorld(i));
		}
		
		new Task(10)
		{
			@Override
			public void run()
			{
				new Q(P.LOW, "World Save Checker", true)
				{
					@Override
					public void run()
					{
						for(World i : worlds.k())
						{
							worlds.get(i).save(false);
						}
					}
				};
			}
		};
	}
	
	public boolean canStack(World w)
	{
		return !worlds.get(w).getConfiguration().getBoolean("entities.disable-stacking");
	}
	
	public boolean canTouch(Entity e)
	{
		if(worlds.get(e.getWorld()).getConfiguration().getStringList("entities.assume-no-side-effects").contains(e.getType().toString()))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void tick()
	{
		
	}
	
	@EventHandler
	public void worldUnload(WorldUnloadEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				getReact().unRegister(worlds.get(e.getWorld()));
				worlds.remove(e.getWorld());
			}
		};
	}
	
	@EventHandler
	public void gc(PostGCEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				for(World i : worlds.k())
				{
					worlds.get(i).save(true);
				}
			}
		};
	}
	
	@EventHandler
	public void worldLoad(WorldLoadEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				worlds.put(e.getWorld(), new ReactWorld(e.getWorld()));
			}
		};
	}
}
