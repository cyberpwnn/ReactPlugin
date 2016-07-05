package org.cyberpwn.react.cluster;

import org.cyberpwn.react.cluster.ClusterConfig.ClusterDataType;

public class ClusterString extends Cluster
{
	private String string;
	
	public ClusterString(String key, String value)
	{
		super(ClusterDataType.STRING, key, 0.0);
		this.string = value;
	}
	
	public String get()
	{
		return string;
	}
	
	public void set(String s)
	{
		value = 0.0;
		string = s;
	}
}
