package org.cyberpwn.react.util;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class PlayerCache implements Configurable
{
	private GMap<Integer, GList<Double>> cache;
	private ClusterConfig cc;
	
	public PlayerCache()
	{
		this.cc = new ClusterConfig();
		this.cache = new GMap<Integer, GList<Double>>();
	}
	
	public void sample(React pl)
	{
		Integer p = pl.onlinePlayers().length;
		Long mused = pl.getSampleController().getSampleMemoryUsed().getValue().getLong();
		Long mchun = pl.getSampleController().getSampleChunkMemory().getValue().getLong();
		
		if(p == 0)
		{
			put(0, mused);
			return;
		}
		
		try
		{
			Double mbase = cc.getDouble("cache.c0.v") + mchun;
			double imp = (mused.doubleValue() - mbase.doubleValue()) / p.doubleValue();
			put(p, imp);
		}
		
		catch(NullPointerException e)
		{
			
		}
	}
	
	public double pull(int i)
	{
		if(!cc.contains("cache.c" + i + ".v"))
		{
			return 10;
		}
		
		try
		{
			return cc.getDouble("cache.c" + i + ".v");
		}
		
		catch(Exception e)
		{
			return 10;
		}
	}
	
	public double pull()
	{
		double v = 0;
		int h = 0;
		
		for(Integer i : cache.keySet())
		{
			if(i == 0)
			{
				continue;
			}
			
			if(cc.contains("cache.c" + i + ".v"))
			{
				int m = cc.getInt("cache.c" + i + ".c");
				h += m;
				v += m * cc.getDouble("cache.c" + i + ".v");
			}
		}
		
		if(h <= 0)
		{
			return 1;
		}
		
		return v / h;
	}
	
	@Override
	public void onReadConfig()
	{
		int max = cc.getInt("cache.ai.max");
		
		for(int i = 0; i <= max; i++)
		{
			if(cc.contains("cache.c" + i + ".v"))
			{
				Double v = cc.getDouble("cache.c" + i + ".v");
				
				if(v == null)
				{
					continue;
				}
				
				cache.put(i, new GList<Double>());
				
				for(int j = 0; j < cc.getInt("cache.c" + i + ".c"); j++)
				{
					cache.get(i).add(v);
				}
			}
		}
	}
	
	public void put(int c, double v)
	{
		if(!cache.containsKey(c))
		{
			cache.put(c, new GList<Double>());
		}
		
		cache.get(c).add(v);
		
		if(cache.get(c).size() > 16)
		{
			cache.get(c).remove(0);
		}
		
		cc.set("cache.c" + c + ".c", cache.get(c).size());
		
		double avg = 0;
		
		for(Double i : cache.get(c))
		{
			avg += i;
		}
		
		avg /= cache.get(c).size();
		cc.set("cache.c" + c + ".v", Math.abs(avg));
		cc.set("cache.ai.max", max());
	}
	
	public double maxEver()
	{
		return pull(max());
	}
	
	public int max()
	{
		int mx = 0;
		
		for(Integer i : cache.keySet())
		{
			if(mx < i)
			{
				mx = i;
			}
		}
		
		return mx;
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("cache.ai.max", 0);
		cc.set("cache.c0.v", 0);
		cc.set("cache.c0.c", 1.0);
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "player-impact";
	}
}
