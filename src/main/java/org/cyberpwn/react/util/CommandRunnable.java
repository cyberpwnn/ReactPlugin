package org.cyberpwn.react.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRunnable implements Runnable
{
	private CommandSender sender;
	private String[] args;
	
	public void run(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
		
		run();
	}
	
	@Override
	public void run()
	{
		
	}
	
	public CommandSender getSender()
	{
		return sender;
	}
	
	public String[] getArgs()
	{
		return args;
	}
	
	public boolean isPlayer()
	{
		return sender instanceof Player;
	}
	
	public Player getPlayer()
	{
		if(isPlayer())
		{
			return (Player) sender;
		}
		
		return null;
	}
}
