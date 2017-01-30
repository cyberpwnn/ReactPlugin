package org.cyberpwn.react.controller;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;

public class DropController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private double radius = 16;
	
	public DropController(React react)
	{
		super(react);
		
		cc = new ClusterConfig();
	}
	
	@Override
	public void start()
	{
		
	}
	
	@Override
	public void tick()
	{
		
	}
	
	@Override
	public void stop()
	{
		
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("radius", radius, "The radius (in blocks) from the nearest player");
	}
	
	@Override
	public void onReadConfig()
	{
		radius = cc.getDouble("radius");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "drops";
	}
}
