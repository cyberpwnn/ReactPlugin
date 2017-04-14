package org.cyberpwn.react.cluster;

import org.cyberpwn.react.cluster.ClusterConfig.ClusterDataType;

public class ClusterBoolean extends Cluster
{
	private Boolean value;
	
	public ClusterBoolean(String key, boolean value)
	{
		super(ClusterDataType.BOOLEAN, key, 0.0);
		this.value = value;
	}
	
	public boolean get()
	{
		return value;
	}
	
	public void set(boolean b)
	{
		value = b;
	}
}
