package org.cyberpwn.react.cluster;

import org.cyberpwn.react.cluster.ClusterConfig.ClusterDataType;

public class Cluster
{
	protected final ClusterDataType type;
	protected final String key;
	protected Double value;
	
	public Cluster(ClusterDataType type, String key, Double value)
	{
		this.type = type;
		this.key = key;
		this.value = value;
	}
	
	public ClusterDataType getType()
	{
		return type;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public Double getValue()
	{
		return value;
	}
	
	public void setValue(Double value)
	{
		this.value = value;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if(key == null)
		{
			if(other.key != null)
				return false;
		}
		else if(!key.equals(other.key))
			return false;
		if(type != other.type)
			return false;
		if(value == null)
		{
			if(other.value != null)
				return false;
		}
		else if(!value.equals(other.value))
			return false;
		return true;
	}
}
