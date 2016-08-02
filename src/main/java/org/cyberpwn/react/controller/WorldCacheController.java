package org.cyberpwn.react.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.GQuadraset;

public class WorldCacheController extends Controller
{
	private static GMap<GQuadraset<String, Integer, Integer, Integer>, String> cache;
	
	public WorldCacheController(React react)
	{
		super(react);
		
		cache = new GMap<GQuadraset<String, Integer, Integer, Integer>, String>();
	}
	
	@SuppressWarnings("unchecked")
	public void start()
	{
		if(!new File(new File(getReact().getDataFolder(), "cache"), "wcache.rxs").exists())
		{
			try
			{
				new File(new File(getReact().getDataFolder(), "cache"), "wcache.rxs").createNewFile();
			} 
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		else
		{
			try
			{
				FileInputStream fin = new FileInputStream(new File(new File(getReact().getDataFolder(), "cache"), "wcache.rxs"));
				GZIPInputStream gzi = new GZIPInputStream(fin);
				ObjectInputStream ois = new ObjectInputStream(gzi);
				Object obj = ois.readObject();
				ois.close();
				
				if(obj != null && (obj instanceof GMap))
				{
					cache = (GMap<GQuadraset<String, Integer, Integer, Integer>, String>) obj;
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		s("Preparing World...");
		
		for(GQuadraset<String, Integer, Integer, Integer> i : cache.k())
		{
			try
			{
				Location l = new Location(Bukkit.getWorld(i.getA()), i.getB(), i.getC(), i.getD());
				l.getBlock().setType(Material.valueOf(cache.get(i)));
			}
			
			catch(Exception e)
			{
				
			}
		}
		
		cache.clear();
		
		for(World i : Bukkit.getWorlds())
		{
			i.save();
		}
	}
	
	public void tick()
	{

	}
	
	public void stop()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(new File(new File(getReact().getDataFolder(), "cache"), "wcache.rxs"));
			GZIPOutputStream gzo = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gzo);
			oos.writeObject(cache.copy());
			oos.close();
			s("Saved World Cache with " + cache.size() + " entries");
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public GQuadraset<String, Integer, Integer, Integer> quad(Location l)
	{
		return new GQuadraset<String, Integer, Integer, Integer>(l.getWorld().toString(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public void mod(Location l, Material material)
	{
		s("Cache Size: " + cache.size());
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
