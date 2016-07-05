package org.cyberpwn.react.action;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public interface Actionable
{
	void preAct();
	
	void act();
	
	void start();
	
	void stop();
	
	long getReactionTime();
	
	void setReactionTime(long ns);
	
	String getName();
	
	String getDescription();
	
	Boolean isManual();
	
	int getIdealTick();
	
	Material getMaterial();
	
	String getKey();
	
	void manual(CommandSender sender);
	
	boolean isEnabled();
}
