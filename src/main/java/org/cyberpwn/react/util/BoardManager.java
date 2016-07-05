package org.cyberpwn.react.util;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.cyberpwn.react.React;

public class BoardManager
{
	private React pl;
	private String name;
	private GMap<Player, Scoreboard> boards;
	
	public BoardManager(String name, React pl)
	{
		this.pl = pl;
		this.name = name;
		this.boards = new GMap<Player, Scoreboard>();
	}
	
	public void set(Player p, GMap<String, String> data)
	{
		Scoreboard board = pl.getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("objective", "dummy");
		
		objective.setDisplayName(name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		int i = 0;
		
		for(String k : data.keySet())
		{
			String s = k + ": " + data.get(k);
			
			if(s.length() > 40)
			{
				s = s.substring(0, 37) + "...";
			}
			
			i++;
			
			objective.getScore(s).setScore(i);
		}
		
		boards.put(p, board);
		p.setScoreboard(board);
	}
	
	public void set(Player p, GList<String> data)
	{
		Scoreboard board = pl.getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("objective", "dummy");
		
		objective.setDisplayName(name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		int i = 0;
		
		for(String k : data)
		{
			String s = k;
			
			if(s.length() > 40)
			{
				s = s.substring(0, 37) + "...";
			}
			
			i++;
			
			objective.getScore(s).setScore(i);
		}
		
		boards.put(p, board);
		p.setScoreboard(board);
	}
}