package org.cyberpwn.react.cluster;

import org.cyberpwn.react.cluster.ClusterConfig.ClusterDataType;
import org.cyberpwn.react.util.GList;

public class ClusterStringList extends Cluster
{
	private GList<String> strings;
	
	public ClusterStringList(String key, GList<String> value)
	{
		super(ClusterDataType.STRING_LIST, key, 0.0);
		this.strings = value;
	}
	
	public GList<String> get()
	{
		return strings;
	}
	
	public void set(GList<String> s)
	{
		value = 0.0;
		strings = s;
	}
}
