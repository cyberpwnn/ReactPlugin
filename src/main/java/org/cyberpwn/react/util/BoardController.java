package org.cyberpwn.react.util;

import org.bukkit.entity.Player;
import org.cyberpwn.react.React;

public class BoardController
{
	private GMap<Player, GMap<String, String>> players;
	private BoardManager boardManager;
	
	public BoardController(String name, React pl)
	{
		players = new GMap<Player, GMap<String,String>>();
		boardManager = new BoardManager(name, pl);
	}
	
	public void put(Player p, String k, String v)
	{
		if(!players.containsKey(p))
		{
			players.put(p, new GMap<String, String>());
		}
		
		players.get(p).put(k, v);
	}
	
	public void update(Player p)
	{
		boardManager.set(p, players.get(p));
	}
	
	public void remove(Player p, String k)
	{
		players.get(p).remove(k);
		boardManager.set(p, players.get(p));
	}
	
	public void remove(Player p)
	{
		players.remove(p);
		p.setScoreboard(p.getServer().getScoreboardManager().getMainScoreboard());
	}

	public GMap<Player, GMap<String, String>> getPlayers()
	{
		return players;
	}

	public BoardManager getBoardManager()
	{
		return boardManager;
	}
}