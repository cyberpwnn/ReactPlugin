package org.cyberpwn.react.action;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.cyberpwn.react.util.GList;

public interface Actionable
{
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	
	void preAct();
	
	void act();
	
	void start();
	
	void stop();
	
	GList<String> getAliases();
	
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
