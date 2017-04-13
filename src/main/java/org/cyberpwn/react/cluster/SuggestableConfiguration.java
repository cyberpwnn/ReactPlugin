package org.cyberpwn.react.cluster;

import org.cyberpwn.react.util.GList;

public interface SuggestableConfiguration
{
	public void getSuggestedChanges(ClusterConfig cc, GList<String> suggestions);
}
