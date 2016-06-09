package org.cyberpwn.react.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReactEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	public ReactEvent()
	{
		
	}
	
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
