package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.util.Value;
import org.cyberpwn.react.util.ValueType;

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
	
	String formatted(boolean acc);
	
	ChatColor color();
	
	Long getReactionTime();
	
	void setReactionTime(long ns);
	
	String getName();
}
