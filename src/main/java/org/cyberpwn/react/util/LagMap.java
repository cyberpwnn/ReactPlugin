package org.cyberpwn.react.util;

import java.util.Collections;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LagMap
{
	private GMap<Location, Integer> map;
	private GMap<Location, GList<InstabilityCause>> cause;
	
	public LagMap()
	{
		map = new GMap<Location, Integer>();
		cause = new GMap<Location, GList<InstabilityCause>>();
	}
	
	public LagMap crop(Location l)
	{
		Chunk c = l.getChunk();
		LagMap m = new LagMap();
		
		int x = c.getX();
		int z = c.getZ();
		int mx = x + 63;
		int ix = x - 64;
		int mz = z + 63;
		int iz = z - 64;
		
		for(Location i : map.k())
		{
			if(i.getWorld().equals(c.getWorld()))
			{
				int cx = i.getChunk().getX();
				int cz = i.getChunk().getZ();
				
				if(cx >= ix && cx <= mx && cz >= iz && cz <= mz)
				{
					m.report(i, cause.get(i), map.get(i));
				}
			}
		}
		
		return m;
	}
	
	public void report(Location l, GList<InstabilityCause> c, Integer score)
	{
		Location ll = l.clone();
		
		if(!map.containsKey(ll))
		{
			map.put(ll, 0);
			cause.put(ll, new GList<InstabilityCause>());
		}
		
		map.put(ll, map.get(ll) + score);
		cause.get(ll).addAll(c);
		cause.get(ll).removeDuplicates();
	}
	
	public void report(Location l, InstabilityCause c, int score)
	{
		Location ll = l.clone();
		
		if(!map.containsKey(ll))
		{
			map.put(ll, 0);
			cause.put(ll, new GList<InstabilityCause>());
		}
		
		map.put(ll, map.get(ll) + score);
		cause.get(ll).add(c);
		cause.get(ll).removeDuplicates();
	}
	
	public void getDamaged(Player p)
	{
		GMap<Chunk, GBiset<GMap<InstabilityCause, Integer>, Integer>> jesusChrist = new GMap<Chunk, GBiset<GMap<InstabilityCause, Integer>, Integer>>();
		GMap<Chunk, Integer> damage = new GMap<Chunk, Integer>();
		GList<Chunk> chunks = new GList<Chunk>();
		GList<Integer> revorder = new GList<Integer>();
		GList<Chunk> order = new GList<Chunk>();
		
		for(Location i : map.k())
		{
			Chunk c = i.getChunk();
			
			if(!chunks.contains(c))
			{
				chunks.add(c);
			}
		}
		
		for(Chunk i : chunks)
		{
			GMap<InstabilityCause, Integer> cause = report(i);
			Integer d = report(cause);
			damage.put(i, d);
			
			if(revorder.size() < 12)
			{
				revorder.add(d);
			}
			
			jesusChrist.put(i, new GBiset<GMap<InstabilityCause, Integer>, Integer>(cause, d));
		}
		
		Collections.sort(revorder);
		Collections.reverse(revorder);
		
		for(Integer i : revorder)
		{
			for(Chunk j : damage.k())
			{
				if(damage.get(j) == i)
				{
					order.add(j);
					break;
				}
			}
		}
		
		for(Chunk i : order)
		{
			RTX r = new RTX();
			GList<ColoredString> lines = new GList<ColoredString>();
			lines.add(new ColoredString(C.YELLOW, "Total Load" + ": "));
			lines.add(new ColoredString(C.WHITE, F.f(damage.get(i)) + "\n"));
			
			int h = Integer.MIN_VALUE;
			InstabilityCause main = null;
			
			for(InstabilityCause j : jesusChrist.get(i).getA().k())
			{
				int score = jesusChrist.get(i).getA().get(j);
				lines.add(new ColoredString(C.RED, j.getName() + ": "));
				lines.add(new ColoredString(C.WHITE, F.f(score) + "\n"));
				
				if(score > h)
				{
					h = score;
					main = j;
				}
			}
			
			lines.add(new ColoredString(C.GREEN, "(Click To Teleport)"));
			Location l = safe(i);
			r.addTextFireHoverCommand("Chunk[" + i.getX() + ", " + i.getZ() + "] ", new RTEX(lines.toArray(new ColoredString[lines.size()])), "/reacttp " + l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ(), C.RED);
			r.addText("Caused by ", C.GRAY);
			GList<ColoredString> ls = new GList<ColoredString>();
			
			for(String j : F.wrap(main.getProblem(), 36))
			{
				ls.add(new ColoredString(C.GRAY, j + "\n"));
			}
			
			r.addTextHover(main.getName(), new RTEX(ls.toArray(new ColoredString[ls.size()])), C.WHITE);
			r.tellRawTo(p);
		}
	}
	
	public Location safe(Chunk c)
	{
		int level = level(c.getWorld(), c.getBlock(0, 0, 0).getX(), c.getBlock(0, 0, 0).getZ());
		return new Location(c.getWorld(), c.getBlock(0, 0, 0).getX(), level, c.getBlock(0, 0, 0).getZ());
	}
	
	public int level(World w, int x, int z)
	{
		for(int i = 255; i > 0; i--)
		{
			if(!w.getBlockAt(x, i, z).getType().equals(Material.AIR))
			{
				return i + 1;
			}
		}
		
		return 255;
	}
	
	public int getTotalDamage(Chunk c)
	{
		GMap<InstabilityCause, Integer> r = report(c);
		int m = 0;
		
		for(InstabilityCause i : r.k())
		{
			m += r.get(i);
		}
		
		return m;
	}
	
	public int report(GMap<InstabilityCause, Integer> r)
	{
		int m = 0;
		
		for(InstabilityCause i : r.k())
		{
			m += r.get(i);
		}
		
		return m;
	}
	
	public GMap<InstabilityCause, Integer> report(Chunk c)
	{
		GMap<InstabilityCause, Integer> m = new GMap<InstabilityCause, Integer>();
		
		for(Location i : map.k())
		{
			if(i.getChunk().equals(c))
			{
				int damage = map.get(i);
				GList<InstabilityCause> causes = cause.get(i);
				
				for(InstabilityCause j : causes)
				{
					if(!m.containsKey(j))
					{
						m.put(j, 0);
					}
					
					m.put(j, m.get(j) + damage);
				}
			}
		}
		
		return m;
	}
	
	public void update()
	{
		for(Location i : map.k())
		{
			map.put(i, (int) (map.get(i) / 1.4));
			
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
	
	public GMap<Location, Integer> getMap()
	{
		return map;
	}
	
	public GMap<Location, GList<InstabilityCause>> getCause()
	{
		return cause;
	}
}
