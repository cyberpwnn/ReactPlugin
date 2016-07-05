package org.cyberpwn.react.reflect;

public class WorldBorderPluginConnector extends PluginConnector
{
	public WorldBorderPluginConnector()
	{
		super("WorldBorder");
	}

	public boolean isActive()
	{
		if(!exists())
		{
			return false;
		}
		
		try
		{
			Class<?> c = Class.forName("com.wimbli.WorldBorder.Config");
			Object fillTask = c.getField("fillTask").get(null);
			Boolean valid = (boolean) fillTask.getClass().getMethod("valid").invoke(null);
			return fillTask != null && valid != null && valid;
		}
		
		catch(Exception e)
		{
			return false;
		}
	}
}
