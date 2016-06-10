package org.cyberpwn.react.controller;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.Failure;
import org.cyberpwn.react.util.GList;

import net.md_5.bungee.api.ChatColor;

public class FailureController extends Controller
{
	private GList<Failure> failures;
	
	public FailureController(React react)
	{
		super(react);
		
		failures = new GList<Failure>();
	}
	
	public void fail(Exception e, String msg)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "React failed to execute an operation. This issue has been logged, and will be saved to a log eventually.");
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + e.toString());
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + msg);
		
		int ind = 0;
		
		for(StackTraceElement i : e.getStackTrace())
		{
			String cnm = i.getClassName();
			
			if(i.getClassName().toLowerCase().contains("react"))
			{
				cnm = ChatColor.BLUE + i.getClassName();
			}
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + StringUtils.repeat(" ", ind) + "\\> " + ChatColor.LIGHT_PURPLE + cnm + ChatColor.RED + "." + ChatColor.AQUA + i.getMethodName() + ChatColor.GREEN + "(" + i.getLineNumber() + ")");
			ind++;
		}
		
		failures.add(new Failure(System.currentTimeMillis(), new GList<StackTraceElement>(e.getStackTrace()), msg, e.getClass().toString()));
	}
	
	public void fail(Exception e)
	{
		fail(e, "React has not yet been prepared for this type of error. Please Report this!");
	}
}