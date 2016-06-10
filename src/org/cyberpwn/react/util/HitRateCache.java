package org.cyberpwn.react.util;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class HitRateCache implements Configurable
{
	private ClusterConfig cc;
	private React react;
	
	public HitRateCache(React react)
	{
		this.react = react;
		this.cc = new ClusterConfig();
		react.getDataController().load("cache", this);
	}
	
	public void save()
	{
		react.getDataController().save("cache", this);
	}
	
	public void hit(InstabilityCause cause)
	{
		String key = "hits." + cause.toString().toLowerCase().replace('_', '-');
		
		if(cc.contains(key))
		{
			cc.set(key, cc.getInt(key) + 1);
		}
		
		else
		{
			cc.set(key, 1);
		}
	}
	
	public int get(InstabilityCause ins)
	{
		if(!cc.contains("hits." + ins.toString().toLowerCase().replace('_', '-')))
		{
			return 0;
		}
		
		if(cc.getInt("hits." + ins.toString().toLowerCase().replace('_', '-')) == null)
		{
			return 0;
		}
		
		return cc.getInt("hits." + ins.toString().toLowerCase().replace('_', '-'));
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("since", String.valueOf(System.currentTimeMillis()));
		
		for(InstabilityCause i : InstabilityCause.values())
		{
			String key = "hits." + i.toString().toLowerCase().replace('_', '-');
			cc.set(key, 0);
		}
	}
	
	@Override
	public void onReadConfig()
	{
		Long time = Long.valueOf(cc.getString("since"));
		
		if(System.currentTimeMillis() - time > new GTime(7, 0, 0, 0, 0).getTotalDuration())
		{
			cc.set("since", String.valueOf(System.currentTimeMillis()));
			
			for(InstabilityCause i : InstabilityCause.values())
			{
				String key = "hits." + i.toString().toLowerCase().replace('_', '-');
				cc.set(key, 0);
			}
		}
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "hits";
	}
}
