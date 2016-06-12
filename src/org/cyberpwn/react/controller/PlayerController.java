package org.cyberpwn.react.controller;

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
		getReact().getDataController().load("player", pd);
		cache.put(p, pd);
	}
	
	public void save(Player p)
	{
		if(!cache.containsKey(p))
		{
			return;
		}
		
		PlayerData pd = new PlayerData(p.getUniqueId());
		getReact().getDataController().save("player", pd);
		cache.put(p, pd);
	}
}
