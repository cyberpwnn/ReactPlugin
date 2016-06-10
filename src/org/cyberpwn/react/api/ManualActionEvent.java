package org.cyberpwn.react.api;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.event.ReactEvent;

public class ManualActionEvent extends ReactEvent implements Cancellable
{
	private CommandSender sender;
	private Actionable action;
	private Boolean cancelled;
	
	public ManualActionEvent(CommandSender sender, Actionable action)
	{
		this.sender = sender;
		this.action = action;
		this.cancelled = false;
	}

	public CommandSender getSender()
	{
		return sender;
	}

	public Actionable getAction()
	{
		return action;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
}
