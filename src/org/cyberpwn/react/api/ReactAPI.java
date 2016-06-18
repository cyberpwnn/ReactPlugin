package org.cyberpwn.react.api;

import org.bukkit.Bukkit;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.controller.SampleController;

public class ReactAPI
{
	private static React r = null;
	
	public static SampleController getSampleController()
	{
		init();
		return r.getSampleController();
	}
	
	public static ActionController getActionController()
	{
		init();
		return r.getActionController();
	}
	
	public static double getMemoryUsed()
	{
		return getSampleMemoryUsed();
	}
	
	public static double getMemoryMax()
	{
		init();
		return r.getSampleController().getSampleMemoryUsed().getMemoryMax();
	}
	
	public static double getMemoryFree()
	{
		init();
		return r.getSampleController().getSampleMemoryUsed().getMemoryFree();
	}
	
	public static double getMemoryGarbage()
	{
		init();
		double k = r.getSampleController().getSampleMemoryUsed().getMemoryUsed() / 1024 / 1024 - getMemoryUsed();
		
		if(k < 0)
		{
			k = 0;
		}
		
		return k;
	}
	
	public static double getSampleTNTPerSecond()
	{
		init();
		return r.getSampleController().getSampleTNTPerSecond().getValue().getDouble();
	}
	
	public static double getSampleTicksPerSecond()
	{
		init();
		return r.getSampleController().getSampleTicksPerSecond().getValue().getDouble();
	}
	
	public static double getSampleStability()
	{
		init();
		return r.getSampleController().getSampleStability().getValue().getDouble();
	}
	
	public static double getSampleRedstoneUpdatesPerSecond()
	{
		init();
		return r.getSampleController().getSampleRedstoneUpdatesPerSecond().getValue().getDouble();
	}
	
	public static double getSampleReactionTime()
	{
		init();
		return r.getSampleController().getSampleReactionTime().getValue().getDouble();
	}
	
	public static double getSamplePlayers()
	{
		init();
		return r.getSampleController().getSamplePlayers().getValue().getDouble();
	}
	
	public static double getSampleMonitoredPlugins()
	{
		init();
		return r.getSampleController().getSampleMonitoredPlugins().getValue().getDouble();
	}
	
	public static double getSampleMemoryUsed()
	{
		init();
		return r.getSampleController().getSampleMemoryUsed().getValue().getDouble();
	}
	
	public static double getSampleMemorySweepFrequency()
	{
		init();
		return r.getSampleController().getSampleMemorySweepFrequency().getValue().getDouble();
	}
	
	public static double getSampleMemoryPerPlayer()
	{
		init();
		return r.getSampleController().getSampleMemoryPerPlayer().getValue().getDouble();
	}
	
	public static double getSampleMemoryAllocationsPerSecond()
	{
		init();
		return r.getSampleController().getSampleMemoryVolatility().getValue().getDouble();
	}
	
	public static double getSampleLiquidFlowPerSecond()
	{
		init();
		return r.getSampleController().getSampleLiquidFlowPerSecond().getValue().getDouble();
	}
	
	public static double getSampleEntities()
	{
		init();
		return r.getSampleController().getSampleEntities().getValue().getDouble();
	}
	
	public static double getSampleDrops()
	{
		init();
		return r.getSampleController().getSampleDrops().getValue().getDouble();
	}
	
	public static double getSampleChunksLoaded()
	{
		init();
		return r.getSampleController().getSampleChunksLoaded().getValue().getDouble();
	}
	
	public static double getSampleChunkMemory()
	{
		init();
		return r.getSampleController().getSampleChunkMemory().getValue().getDouble();
	}
	
	public static double getSampleChunkLoadPerSecond()
	{
		init();
		return r.getSampleController().getSampleChunkLoadPerSecond().getValue().getDouble();
	}
	
	public static double getSampleChunkGenPerSecond()
	{
		init();
		return r.getSampleController().getSampleChunkGenPerSecond().getValue().getDouble();
	}
	
	public static void callActionCollectGarbage()
	{
		init();
		r.getActionController().getActionCollectGarbage().manual(Bukkit.getConsoleSender());
	}
	
	public static void callActionCullDrops()
	{
		init();
		r.getActionController().getActionCullDrops().act();
	}
	
	public static void callActionCullEntities()
	{
		init();
		r.getActionController().getActionCullEntities().act();
	}
	
	public static void callActionSuppressLiquid()
	{
		init();
		r.getActionController().getActionSuppressLiquid().act();
	}
	
	public static void callActionSuppressRedstone()
	{
		init();
		r.getActionController().getActionSuppressRedstone().manual(Bukkit.getConsoleSender());
	}
	
	public static void callActionSuppressTNT()
	{
		init();
		r.getActionController().getActionSuppressTnt().manual(Bukkit.getConsoleSender());
	}
	
	public static String getVersion()
	{
		return Version.V;
	}
	
	public static int getVersionCode()
	{
		return Version.C;
	}
	
	private static void init()
	{
		if(r == null)
		{
			r = React.instance();
		}
	}
}
