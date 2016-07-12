package org.cyberpwn.react.api;

import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.controller.SampleController;

public class ReactAPI
{
	/**
	 * Get the instance of react
	 * 
	 * @return React instance
	 */
	public static React getReact()
	{
		return React.instance();
	}
	
	/**
	 * Get a list of players using the react title monitor
	 * 
	 * @return a list of players
	 */
	public static Player[] getMonitoringPlayers()
	{
		return getReact().getMonitorController().getMonitors().k().toArray(new Player[getReact().getMonitorController().getMonitors().k().size()]);
	}
	
	/**
	 * Get a list of players using the react map
	 * 
	 * @return a list of players
	 */
	public static Player[] getMappingPlayers()
	{
		return getReact().getMonitorController().getMappers().k().toArray(new Player[getReact().getMonitorController().getMappers().k().size()]);
	}
	
	/**
	 * Reloads React
	 */
	public static void reloadReact()
	{
		getReact().onReload(null);
	}
	
	/**
	 * Gets the sampleController. With this you can grab any data from samplers.
	 * For example, <strong>int chunks =
	 * ReactAPI.getSampleController().getSampleChunksLoaded().get().getInteger()
	 * </strong>
	 * 
	 * @return the SampleController instance
	 */
	public static SampleController getSampleController()
	{
		return getReact().getSampleController();
	}
	
	/**
	 * Gets the actionController. With this you can manually run actions within
	 * react, although some of them aren't recommended.
	 * 
	 * @return the ActionController instance
	 */
	public static ActionController getActionController()
	{
		return getReact().getActionController();
	}
	
	/**
	 * Gets the ticks per second in a very accurate double.
	 * 
	 * @return
	 */
	public static double getTicksPerSecond()
	{
		return getSampleController().getSampleTicksPerSecond().get().getDouble();
	}
	
	/**
	 * Check if react has (based on config) determined if the server is lagging
	 * 
	 * @return true if the server is considered lagging
	 */
	public static boolean isLagging()
	{
		return React.instance().getActionController().getActionInstabilityCause().isLagging();
	}
	
	/**
	 * Get the ACTUAL amount of memory used. this is different then the runtime
	 * operation. This EXCLUDES GARBAGE in the memory.
	 * 
	 * @return the memory used on the server. IN MEGABYTES
	 */
	public static double getMemoryUsed()
	{
		return React.instance().getSampleController().getSampleMemoryUsed().getValue().getDouble();
	}
	
	/**
	 * Returns the amount of memory allocated to this JVM environment
	 * 
	 * @return the max memory in BYTES
	 */
	public static double getMemoryMax()
	{
		return React.instance().getSampleController().getSampleMemoryUsed().getMemoryMax();
	}
	
	/**
	 * Returns the usual amount of memory excluding garbage.
	 * 
	 * @return the free memory in BYTES
	 */
	public static double getMemoryFree()
	{
		return React.instance().getSampleController().getSampleMemoryUsed().getMemoryFree();
	}
	
	/**
	 * Returns the amount of GARBAGE in the memory
	 * 
	 * @return the memory used from garbage in MEGABYTES
	 */
	public static double getMemoryGarbage()
	{
		double k = React.instance().getSampleController().getSampleMemoryUsed().getMemoryUsed() / 1024 / 1024 - getMemoryUsed();
		
		if(k < 0)
		{
			k = 0;
		}
		
		return k;
	}
	
	/**
	 * Returns the version of react
	 * 
	 * @return the version of react
	 */
	public static String getVersion()
	{
		return Version.V;
	}
	
	/**
	 * Returns the version code of react
	 * 
	 * @return the version code of react
	 */
	public static int getVersionCode()
	{
		return Version.C;
	}
}
