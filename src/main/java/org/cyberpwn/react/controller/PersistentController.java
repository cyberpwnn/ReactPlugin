package org.cyberpwn.react.controller;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class PersistentController extends Controller implements Configurable
{
	private ClusterConfig cc;
	
	public PersistentController(React react)
	{
		super(react);
		
		this.cc = new ClusterConfig();
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
		return "world-persistence";
	}
}
