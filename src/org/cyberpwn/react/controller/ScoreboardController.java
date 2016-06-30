package org.cyberpwn.react.controller;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.BoardController;
import org.cyberpwn.react.util.GBiset;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.ScreenBoard;

public class ScoreboardController extends Controller
{
	private GMap<Player, GBiset<Integer, Integer>> monitors;
	private BoardController bc;
	private ScreenBoard ms;
	private Integer inx;
	
	public ScoreboardController(React react)
	{
		super(react);
		
		inx = 0;
		bc = new BoardController(ChatColor.AQUA + "React Monitor", getReact());
		monitors = new GMap<Player, GBiset<Integer, Integer>>();
		ms = new ScreenBoard();	
	}
	
	public void stop()
	{
		for(Player i : monitors.k())
		{
			bc.remove(i);
			monitors.remove(i);
		}
	}
	
	public void dispatch()
	{
		for(Player i : new GList<Player>(monitors.keySet()))
		{
			int his = i.getInventory().getHeldItemSlot();
			int ois = monitors.get(i).getA();
			int cg = monitors.get(i).getB();
			
			if(i.isSneaking() && !getReact().getMonitorController().getLocks().containsKey(i))
			{
				if(his != ois)
				{
					if(ois == 0 && his > 7)
					{
						cg = ms.dec(cg);
					}
					
					else if(ois == 8 && his < 3)
					{
						cg = ms.inc(cg);
					}
					
					else if(his > ois)
					{
						cg = ms.inc(cg);
					}
					
					else if(his < ois)
					{
						cg = ms.dec(cg);
					}
					
					monitors.get(i).setB(cg);
					monitors.get(i).setA(his);
				}
			}
			
			else
			{
				if(getReact().getMonitorController().getLocks().containsKey(i))
				{
					monitors.get(i).setB(getReact().getMonitorController().getLocks().get(i));
				}
				
				monitors.get(i).setA(his);
			}
			
			inx++;
			
			if(inx > 2)
			{
				inx = 0;
				display(i, bc, cg);
			}
		}
	}
	
	public boolean isMonitoring(Player p)
	{
		return monitors.containsKey(p);
	}
	
	public void toggleMonitoring(Player p)
	{
		if(isMonitoring(p))
		{
			bc.remove(p);
			monitors.remove(p);
			p.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_SCOREBOARD_DISABLED);
		}
		
		else
		{
			monitors.put(p, new GBiset<Integer, Integer>(0, 0));
			p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_SCOREBOARD_ENABLED);
		}
	}

	public void display(Player p, BoardController bc, int cg)
	{
		GList<String> k = new GList<String>();
		
		if(cg < 0)
		{
			cg = 0;
		}
		
		for(int i = cg; i < cg + 12; i++)
		{
			int scc = i;
			
			if(scc >= ms.getElements().size())
			{
				scc = i - (ms.getElements().size() - 1);
				
				if(scc >= ms.getElements().size())
				{
					break;
				}
			}
			
			k.add(ms.getElements().get(scc).color() + ms.getElements().get(scc).formatted(false));
		}
		
		bc.getBoardManager().set(p, k);
	}
}
