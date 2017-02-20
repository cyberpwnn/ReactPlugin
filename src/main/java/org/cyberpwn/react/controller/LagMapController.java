package org.cyberpwn.react.controller;

import org.bukkit.Location;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ReactAPI;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.LagMap;
import org.cyberpwn.react.util.Q;
import org.cyberpwn.react.util.Q.P;

public class LagMapController extends Controller
{
	private LagMap map;
	
	public LagMapController(React react)
	{
		super(react);
		
		map = new LagMap();
	}
	
	public GMap<InstabilityCause, Double> report()
	{
		double tps = ReactAPI.getTicksPerSecond();
		double usage = 1.0 - (tps / 20);
		int total = 0;
		GMap<InstabilityCause, Integer> summary = map.summary();
		GMap<InstabilityCause, Double> scale = new GMap<InstabilityCause, Double>();
		
		for(InstabilityCause i : summary.k())
		{
			total += summary.get(i);
		}
		
		for(InstabilityCause i : summary.k())
		{
			scale.put(i, ((double) summary.get(i) / (double) total) * usage);
		}
		
		return scale;
	}
	
	public static void report(Location l, InstabilityCause c, int s)
	{
		React.instance().getLagMapController().getMap().report(l, c, s);
	}
	
	@Override
	public void tick()
	{
		new Q(P.LOWEST, "Lag Mapper Update", true)
		{
			@Override
			public void run()
			{
				map.update();
			}
		};
	}
	
	public LagMap getMap()
	{
		return map;
	}
}
