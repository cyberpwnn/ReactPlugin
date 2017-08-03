package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.cyberpwn.react.React;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.RegionFile;
import org.cyberpwn.react.util.RegionProperty;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionController extends Controller
{
	private File regionFile;
	private RegionFile r;
	
	public RegionController(React react)
	{
		super(react);
		
		regionFile = new File(new File(react.getDataFolder(), "cache"), "regions.rca");
		r = new RegionFile();
		
		try
		{
			r.load(regionFile);
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void start()
	{
		
	}
	
	@Override
	public void stop()
	{
		try
		{
			r.save(regionFile);
		}
		
		catch(IOException e)
		{
			
		}
	}
	
	public GList<String> getAllWGRegions()
	{
		GList<String> rg = new GList<String>();
		
		if(canUse())
		{
			WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
			
			for(World i : Bukkit.getWorlds())
			{
				for(String j : worldGuard.getRegionManager(i).getRegions().keySet())
				{
					rg.add(j);
				}
			}
		}
		
		return rg;
	}
	
	public GList<RegionProperty> getProperties(Location l)
	{
		GList<RegionProperty> rp = new GList<RegionProperty>();
		
		try
		{
			for(String i : getRegions(l))
			{
				rp.addAll(get(i));
			}
			
			rp.removeDuplicates();
		}
		
		catch(Exception e)
		{
			
		}
		
		return rp;
	}
	
	public GList<String> getRegions(Location l)
	{
		GList<String> g = new GList<String>();
		
		try
		{
			if(canUse())
			{
				WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
				Vector pt = BukkitUtil.toVector(l);
				RegionManager regionManager = worldGuard.getRegionManager(l.getWorld());
				ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
				
				for(ProtectedRegion i : set.getRegions())
				{
					g.add(i.getId());
				}
			}
		}
		
		catch(Exception e)
		{
			
		}
		
		return g;
	}
	
	public boolean canUse()
	{
		return Bukkit.getPluginManager().getPlugin("WorldGuard") != null && Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().startsWith("6.2");
	}
	
	public GList<RegionProperty> get(String s)
	{
		return r.getProperties(s);
	}
	
	public void listRegions(CommandSender sender)
	{
		sender.sendMessage(String.format(Info.HRN, "WG Regions"));
		
		for(String i : getAllWGRegions())
		{
			view(sender, i);
		}
		
		sender.sendMessage(Info.HR);
	}
	
	public void listProperties(CommandSender sender)
	{
		sender.sendMessage(String.format(Info.HRN, "React Flags"));
		
		for(RegionProperty i : RegionProperty.values())
		{
			sender.sendMessage(ChatColor.AQUA + i.toString() + ChatColor.GRAY + ": " + i.getDescription());
		}
		
		sender.sendMessage(Info.HR);
	}
	
	public void view(CommandSender sender, String rg)
	{
		sender.sendMessage(ChatColor.AQUA + rg + ": " + ChatColor.GRAY + " {" + get(rg).toString(", ") + "}");
	}
	
	public void add(CommandSender sender, String s, String p)
	{
		if(!getAllWGRegions().contains(s))
		{
			sender.sendMessage(ChatColor.RED + s + " is not a world guard region. Use /re rg list");
			return;
		}
		
		RegionProperty rp = RegionProperty.get(p);
		
		if(rp == null)
		{
			listProperties(sender);
			return;
		}
		
		add(s, rp);
		view(sender, s);
	}
	
	public void remove(CommandSender sender, String s, String p)
	{
		if(!getAllWGRegions().contains(s))
		{
			sender.sendMessage(ChatColor.RED + s + " is not a world guard region. Use /re rg list");
			return;
		}
		
		RegionProperty rp = RegionProperty.get(p);
		
		if(rp == null)
		{
			listProperties(sender);
			return;
		}
		
		remove(s, rp);
		view(sender, s);
	}
	
	public void remove(CommandSender sender, String s)
	{
		if(!getAllWGRegions().contains(s))
		{
			sender.sendMessage(ChatColor.RED + s + " is not a world guard region. Use /re rg list");
			return;
		}
		
		remove(s);
		view(sender, s);
	}
	
	public void add(String s, RegionProperty p)
	{
		if(!r.getMap().containsKey(s))
		{
			r.getMap().put(s, new GList<RegionProperty>());
			
			try
			{
				r.save(regionFile);
			}
			
			catch(IOException e)
			{
				
			}
		}
		
		if(!r.getMap().get(s).contains(p))
		{
			r.getMap().get(s).add(p);
			
			try
			{
				r.save(regionFile);
			}
			
			catch(IOException e)
			{
				
			}
		}
	}
	
	public void remove(String s)
	{
		r.getMap().remove(s);
		
		try
		{
			r.save(regionFile);
		}
		
		catch(IOException e)
		{
			
		}
	}
	
	public void remove(String s, RegionProperty p)
	{
		if(r.getMap().containsKey(s))
		{
			r.getMap().get(s).remove(p);
			
			if(r.getMap().get(s).isEmpty())
			{
				remove(s);
			}
			
			else
			{
				try
				{
					r.save(regionFile);
				}
				
				catch(IOException e)
				{
					
				}
			}
		}
	}
}
