package org.cyberpwn.react.controller;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.GBiset;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.GQuadraset;

public class WorldCacheController extends Controller
{
	private GMap<GQuadraset<String, Integer, Integer, Integer>, String> cache;
	
	public WorldCacheController(React react)
	{
		super(react);
		
		cache = new GMap<GQuadraset<String, Integer, Integer, Integer>, String>();
	}
	
	public void start()
	{
		
	}
	
	public void tick()
	{

	}
	
	public void stop()
	{
		
	}
	
	public void write()
	{
		
	}
	
	public GBiset<Integer, Integer> bi(int id, int met)
	{
		return new GBiset<Integer, Integer>(id, met);
	}
	
	public GQuadraset<String, Integer, Integer, Integer> quad(Location l)
	{
		return new GQuadraset<String, Integer, Integer, Integer>(l.getWorld().toString(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public void mod(Location l, Material material)
	{
		cache.put(quad(l), material.toString());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void blockBreak(BlockBreakEvent e)
	{
		mod(e.getBlock().getLocation(), Material.AIR);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void blockPlace(BlockPlaceEvent e)
	{
		mod(e.getBlock().getLocation(), e.getBlock().getType());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void worldSave(WorldSaveEvent e)
	{
		for(GQuadraset<String, Integer, Integer, Integer> i : cache.k())
		{
			if(i.getA().equals(e.getWorld().getName()))
			{
				cache.remove(i);
			}
		}
	}
}
