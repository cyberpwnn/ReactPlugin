package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.PlayerData;

public class PlayerController extends Controller
{
	private final GMap<Player, PlayerData> cache;
	
	public PlayerController(React react)
	{
		super(react);
		
		this.cache = new GMap<Player, PlayerData>();
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		for(Player i : React.instance().onlinePlayers())
		{
			if(cache.containsKey(i))
			{
				save(i);
			}
		}
	}
	
	public boolean exists(Player p)
	{
		return new File(new File(getReact().getDataFolder(), "playerdata"), cache.get(p).getCodeName() + ".yml").exists();
	}
	
	public PlayerData gpd(Player p)
	{
		if(!cache.containsKey(p))
		{
			load(p);
		}
		
		return cache.get(p);
	}
	
	public void spd(Player p, PlayerData pd)
	{
		cache.put(p, pd);
	}
	
	public void load(Player p)
	{
		if(cache.containsKey(p))
		{
			return;
		}
		
		PlayerData pd = new PlayerData(p.getUniqueId());
		getReact().getDataController().load("playerdata", pd);
		cache.put(p, pd);
	}
	
	public void save(Player p)
	{
		if(!cache.containsKey(p))
		{
			return;
		}
		
		try
		{
			cache.get(p).onNewConfig();
			cache.get(p).getConfiguration().toYaml().save(new File(new File(getReact().getDataFolder(), "playerdata"), cache.get(p).getCodeName() + ".yml"));
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
		cache.remove(p);
	}
}
