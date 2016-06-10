package org.cyberpwn.react.util;

import java.io.File;

import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.sampler.Samplable;

import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderHook extends EZPlaceholderHook implements Configurable
{
	private ClusterConfig cc;
	private React react;
	
	public PlaceholderHook(React react)
	{
		super(react, "react");
		this.react = react;
		this.cc = new ClusterConfig();
		
		try
		{
			File f = new File(react.getDataFolder(), "placeholders.yml");
			
			if(f.exists())
			{
				f.delete();
			}
		}
		
		catch(Exception e)
		{
			
		}
		
		react.getDataController().save(null, this);
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier)
	{
		for(Samplable i : react.getSampleController().getSamples().keySet())
		{
			Configurable c = (Configurable) i;
			
			if(identifier.equals(c.getCodeName().replace('-', '_')))
			{
				return String.valueOf(i.get().getDouble());
			}
			
			if(identifier.equals(c.getCodeName().replace('-', '_') + "_rounded"))
			{
				return String.valueOf(F.f(i.get().getDouble(), 0));
			}
		}
		
		if(identifier.equals("current_issues"))
		{
			return String.valueOf(react.getActionController().getActionInstabilityCause().getProblems().size());
		}
		
		if(identifier.equals("version"))
		{
			return String.valueOf(Info.VERSION);
		}
		
		if(identifier.equals("monitoring_player_count"))
		{
			return String.valueOf(react.getMonitorController().getMonitors().size());
		}
		
		if(identifier.equals("mapping_player_count"))
		{
			return String.valueOf(react.getMonitorController().getMappers().size());
		}
		
		if(identifier.equals("monitoring_players"))
		{
			return String.valueOf(react.getMonitorController().getMonitors().k().toString(","));
		}
		
		if(identifier.equals("mapping_players"))
		{
			return String.valueOf(react.getMonitorController().getMappers().k().toString(","));
		}
		
		if(identifier.equals("up_to_date"))
		{
			if(React.ignoreUpdates)
			{
				return "unknown";
			}
			
			return String.valueOf(React.updated);
		}
		
		if(identifier.equals("version_code"))
		{
			return String.valueOf(Info.VERSION_CODE);
		}
		
		return null;
	}
	
	@Override
	public void onNewConfig()
	{
		GList<String> placeholders = new GList<String>();
		
		for(Samplable i : react.getSampleController().getSamples().keySet())
		{
			Configurable c = (Configurable) i;
			placeholders.add("react_" + c.getCodeName().replace('-', '_'));
			placeholders.add("react_" + c.getCodeName().replace('-', '_') + "_rounded");
		}
		
		placeholders.add("react_current_issues");
		placeholders.add("react_version");
		placeholders.add("react_monitoring_player_count");
		placeholders.add("react_mapping_player_count");
		placeholders.add("react_monitoring_players");
		placeholders.add("react_mapping_players");
		placeholders.add("react_up_to_date");
		placeholders.add("react_version_code");
		
		cc.set("placeholders", placeholders);
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
		return "placeholders";
	}
}