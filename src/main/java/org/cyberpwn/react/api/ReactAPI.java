package org.cyberpwn.react.api;

import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.GList;

public class ReactAPI
{
	public static String[] getActions()
	{
		GList<String> kx = new GList<String>();
		
		for(Actionable i : React.instance().getActionController().getActions().k())
		{
			if(i.isManual())
			{
				kx.add(i.getKey());
			}
		}
		
		return kx.toArray(new String[kx.size()]);
	}
	
	public static String[] getSamplers()
	{
		GList<String> kx = new GList<String>();
		
		for(Samplable i : React.instance().getSampleController().getSamples().k())
		{
			kx.add(i.getName());
		}
		
		return kx.toArray(new String[kx.size()]);
	}
	
	public static void act(String action)
	{
		for(Actionable i : React.instance().getActionController().getActions().k())
		{
			if(i.isManual() && i.getKey().equals(action))
			{
				i.act();
				return;
			}
		}
	}
	
	public static Double sample(String sample)
	{
		for(Samplable i : React.instance().getSampleController().getSamples().k())
		{
			if(i.getName().equals(sample))
			{
				return i.get().getDouble();
			}
		}
		
		return null;
	}
	
	public static boolean isLagging()
	{
		return React.instance().getActionController().getActionInstabilityCause().isLagging();
	}
	
	public static double getMemoryUsed()
	{
		return React.instance().getSampleController().getSampleMemoryUsed().getValue().getDouble();
	}
	
	public static double getMemoryMax()
	{
		return React.instance().getSampleController().getSampleMemoryUsed().getMemoryMax();
	}
	
	public static double getMemoryFree()
	{
		return React.instance().getSampleController().getSampleMemoryUsed().getMemoryFree();
	}
	
	public static double getMemoryGarbage()
	{
		double k = React.instance().getSampleController().getSampleMemoryUsed().getMemoryUsed() / 1024 / 1024 - getMemoryUsed();
		
		if(k < 0)
		{
			k = 0;
		}
		
		return k;
	}
	
	public static String getVersion()
	{
		return Version.V;
	}
	
	public static int getVersionCode()
	{
		return Version.C;
	}
}
