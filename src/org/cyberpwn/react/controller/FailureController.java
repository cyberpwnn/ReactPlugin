package org.cyberpwn.react.controller;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.util.Failure;
import org.cyberpwn.react.util.GList;

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
		Bukkit.getConsoleSender().sendMessage(Info.COLOR_ERR + "React failed to execute an operation. This issue has been logged, and will be saved to a log eventually.");
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
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + StringUtils.repeat(" ", ind) + "\\> " + ChatColor.LIGHT_PURPLE + cnm + Info.COLOR_ERR + "." + ChatColor.AQUA + i.getMethodName() + ChatColor.GREEN + "(" + i.getLineNumber() + ")");
			ind++;
		}
		
		failures.add(new Failure(System.currentTimeMillis(), new GList<StackTraceElement>(e.getStackTrace()), msg, e.getClass().toString()));
	}
	
	public void fail(Exception e)
	{
		fail(e, "React has not yet been prepared for this type of error. Please Report this!");
	}

	public GList<Failure> getFailures()
	{
		return failures;
	}
}