package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.Value;
import org.cyberpwn.react.util.ValueType;

public interface Samplable
{
	boolean isPooled();
	
	boolean canSleep();
	
	boolean isAsleep();
	
	void sleep(int ticks);
	
	void onTick();
	
	void onStart();
	
	void onStop();
	
	void onMetricsPlot(Graph graph);
	
	void handleAction();
	
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
	
	String formatted(boolean acc);
	
	ChatColor color();
	
	ChatColor darkColor();
	
	Long getReactionTime();
	
	void setReactionTime(long ns);
	
	String getName();
	
	String getDescription();
	
	String getExplaination();
}
