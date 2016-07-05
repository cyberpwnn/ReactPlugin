package org.cyberpwn.react.cluster;

public interface Configurable
{
	void onNewConfig();
	void onReadConfig();
	ClusterConfig getConfiguration();
	String getCodeName();
}
