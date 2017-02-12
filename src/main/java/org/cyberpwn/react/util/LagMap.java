package org.cyberpwn.react.util;

import org.bukkit.Location;

public class LagMap
{
	private GMap<Location, Integer> map;
	private GMap<Location, GList<InstabilityCause>> cause;
	
	public LagMap()
	{
		map = new GMap<Location, Integer>();
		cause = new GMap<Location, GList<InstabilityCause>>();
	}
	
	public void report(Location l, InstabilityCause c, int score)
	{
		Location ll = l.getBlock().getLocation();
		
		if(!map.containsKey(ll))
		{
			map.put(ll, 0);
			cause.put(ll, new GList<InstabilityCause>());
		}
		
		map.put(ll, map.get(ll) + score);
		cause.get(ll).add(c);
		cause.get(ll).removeDuplicates();
	}
	
	public void update()
	{
		for(Location i : map.k())
		{
			map.put(i, (int) (map.get(i) / 1.3));
			
			if(map.get(i) < 1)
			{
				map.remove(i);
				cause.remove(i);
			}
		}
	}
	
	public GMap<InstabilityCause, Integer> summary()
	{
		GMap<InstabilityCause, Integer> g = new GMap<InstabilityCause, Integer>();
		
		for(Location i : map.k())
		{
			for(InstabilityCause j : cause.get(i))
			{
				if(!g.containsKey(j))
				{
					g.put(j, 0);
				}
				
				g.put(j, g.get(j) + map.get(i));
			}
		}
		
		return g;
	}
	
	@Override
	public String toString()
	{
		GList<String> s = new GList<String>();
		GMap<InstabilityCause, Integer> su = summary();
		
		for(InstabilityCause i : su.k())
		{
			s.add(i.name() + ": " + F.f(su.get(i)));
		}
		
		return s.toString(", ");
	}
}
