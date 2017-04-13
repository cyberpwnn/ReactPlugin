package org.cyberpwn.react.util;

import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class Typ implements Configurable
{
	private ClusterConfig cc;
	
	public Typ()
	{
		cc = new ClusterConfig();
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		
	}

	@Override
	public void onReadConfig()
	{
		
	}

	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}

	@Override
	public String getCodeName()
	{
		return "typ";
	}
}
