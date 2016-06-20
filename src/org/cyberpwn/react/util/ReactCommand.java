package org.cyberpwn.react.util;

import org.bukkit.command.CommandSender;

public class ReactCommand
{
	private GList<String> triggers;
	private String description;
	private CommandRunnable runnable;
	
	public ReactCommand(CommandRunnable runnable, String desc, String... triggers)
	{
		this.triggers = new GList<String>();
		this.runnable = runnable;
		this.description = desc;

		for(String i : triggers)
		{
			this.triggers.add(i.toLowerCase());
		}
	}
	
	public boolean onCommand(String sub, CommandSender sender, String[] args)
	{
		if(triggers.contains(sub.toLowerCase()))
		{
			runnable.run(sender, args);
			return true;
		}
		
		return false;
	}

	public GList<String> getTriggers()
	{
		return triggers;
	}

	public CommandRunnable getRunnable()
	{
		return runnable;
	}

	public String getDescription()
	{
		return description;
	}
}
