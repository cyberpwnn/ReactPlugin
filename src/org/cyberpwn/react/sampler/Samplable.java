package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.util.Value;
import org.cyberpwn.react.util.ValueType;
import org.cyberpwn.react.util.Metrics.Graph;

public interface Samplable
{
	void onTick();
	
	void onStart();
	
	void onStop();
	
	void onMetricsPlot(Graph graph);
	
	ValueType getType();
	
	Value get();
	
	void setLastTick(Long lastTick);
	
	Long getLastTick();
	
	Integer getMaxDelay();
	
	void setMaxDelay(Integer maxDelay);
	
	Integer getMinDelay();
	
	void setMinDelay(Integer minDelay);
	
	boolean isProblematic();
	
	String getProblem();
	
	Integer getIdealDelay();
	
	void setIdealDelay(Integer idealDelay);
	
	Integer getCurrentDelay();
	
	void setCurrentDelay(Integer currentDelay);
	
	String formatted();
	
	ChatColor color();
	
	Long getReactionTime();
	
	void setReactionTime(long ns);
	
	String getName();
	
	String getDescription();
	
	String getExplaination();
}
