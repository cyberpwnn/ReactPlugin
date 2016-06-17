package org.cyberpwn.react.controller;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.ReactWorld;

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
	}
	
	@EventHandler
	public void worldUnload(WorldUnloadEvent e)
	{
		getReact().unRegister(worlds.get(e.getWorld()));
		worlds.remove(e.getWorld());
	}
	
	@EventHandler
	public void worldLoad(WorldLoadEvent e)
	{
		worlds.put(e.getWorld(), new ReactWorld(e.getWorld()));
	}
}
