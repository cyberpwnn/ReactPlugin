package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.lang.Info;

public class Dispatcher
{
	public static String buffer = null;
	
	public enum DispatchType
	{
		INFO, SUCCESS, FAILURE, WARNING, VERBOSE, OVERBOSE
	}
	
	protected String name;
	protected static Boolean silent = false;
	
	public Dispatcher(String name)
	{
		this.name = "Internal";
	}
	
	private void log(DispatchType type, String s, String... o)
	{
		if(silent)
		{
			return;
		}
		
		String msg = s + "";
		
		for(String i : o)
		{
			msg = msg + i;
		}
		
		String tmg = ChatColor.AQUA + type.toString() + ": " + ChatColor.GOLD + "/" + name + ": " + ChatColor.WHITE + msg;
		
		remote(msg);
		
		if(!React.isDebug())
		{
			return;
		}
		
		Bukkit.getServer().getConsoleSender().sendMessage(tmg);
	}
	
	public static void remote(String msg)
	{
		String tmx = msg;
		buffer = tmx;
	}
	
	public static void r(String msg)
	{
		remote(msg);
	}
	
	public void info(String... o)
	{
		log(DispatchType.INFO, "" + ChatColor.WHITE, o);
	}
	
	public void i(String... s)
	{
		info(s);
	}
	
	public void success(String... o)
	{
		log(DispatchType.SUCCESS, "" + ChatColor.GREEN, o);
	}
	
	public void s(String... o)
	{
		success(o);
	}
	
	public void failure(String... o)
	{
		log(DispatchType.FAILURE, "" + Info.COLOR_ERR, o);
	}
	
	public void f(String... o)
	{
		failure(o);
	}
	
	public void warning(String... o)
	{
		log(DispatchType.WARNING, "" + ChatColor.YELLOW, o);
	}
	
	public void w(String... o)
	{
		warning(o);
	}
	
	public void verbose(String... o)
	{
		log(DispatchType.VERBOSE, "" + ChatColor.LIGHT_PURPLE, o);
	}
	
	public void v(String... o)
	{
		verbose(o);
	}
	
	public void overbose(String... o)
	{
		log(DispatchType.OVERBOSE, "" + ChatColor.AQUA, o);
	}
	
	public void o(String... o)
	{
		overbose(o);
	}
	
	public static String getBuffer()
	{
		return buffer;
	}
	
	public static void setBuffer(String buffer)
	{
		Dispatcher.buffer = buffer;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Boolean getSilent()
	{
		return silent;
	}
	
	public void setSilent(Boolean silent)
	{
		Dispatcher.silent = silent;
	}
}
