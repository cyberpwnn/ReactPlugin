package org.cyberpwn.react.cluster;

public interface Configurable
{
	void onNewConfig(ClusterConfig cc);
	void onReadConfig();
	ClusterConfig getConfiguration();
	String getCodeName();
}
