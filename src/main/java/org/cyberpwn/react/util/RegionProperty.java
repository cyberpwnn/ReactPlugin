package org.cyberpwn.react.util;

public enum RegionProperty
{
	DENY_REACTION("Deny Reactions to execute in this region."),
	DENY_MAPPING("Deny Map Graphs to be taken in this region."),
	DENY_MONITORING("Deny Monitoring title messages in this region.");
	
	private String description;
	
	private RegionProperty(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public static RegionProperty get(String f)
	{
		for(RegionProperty i : RegionProperty.values())
		{
			if(i.toString().equals(f))
			{
				return i;
			}
		}
		
		return null;
	}
}
