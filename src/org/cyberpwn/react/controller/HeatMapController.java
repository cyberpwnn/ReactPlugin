package org.cyberpwn.react.controller;

import org.cyberpwn.react.React;
import org.cyberpwn.react.util.GChunk;
import org.cyberpwn.react.util.GMap;

public class HeatMapController extends Controller
{
	private GMap<GChunk, Integer> heat;
	private GMap<GChunk, Integer> preHeat;
	
	public HeatMapController(React react)
	{
		super(react);
		
		heat = new GMap<GChunk, Integer>();
		preHeat = new GMap<GChunk, Integer>();
	}

	public GMap<GChunk, Integer> getHeat()
	{
		return heat;
	}

	public GMap<GChunk, Integer> getPreHeat()
	{
		return preHeat;
	}
}
