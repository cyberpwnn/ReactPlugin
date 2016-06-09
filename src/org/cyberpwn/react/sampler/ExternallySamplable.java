package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.object.Value;
import org.cyberpwn.react.object.ValueType;

public interface ExternallySamplable
{
	String getPlugin();
	
	void onTick();
	
	void onStart();
	
	void onStop();
	
	ValueType getType();
	
	Value get();
	
	void setLastTick(Long lastTick);
	
	Long getLastTick();
	
	Integer getMaxDelay();
	
	void setMaxDelay(Integer maxDelay);
	
	Integer getMinDelay();
	
	void setMinDelay(Integer minDelay);
	
	Integer getIdealDelay();
	
	void setIdealDelay(Integer idealDelay);
	
	Integer getCurrentDelay();
	
	void setCurrentDelay(Integer currentDelay);
	
	String formatted();
	
	ChatColor color();
	
	Long getReactionTime();
	
	void setReactionTime(long ns);
	
	String getName();
}
