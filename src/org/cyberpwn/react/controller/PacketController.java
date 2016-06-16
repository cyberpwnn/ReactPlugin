package org.cyberpwn.react.controller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.ProtocolLibHandler;

public class PacketController extends Controller
{
	public PacketController(React react)
	{
		super(react);
	}
	
	public void start()
	{
		Plugin pl = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		
		if(pl != null)
		{
			new ProtocolLibHandler(react);
			o("Using ProtocolLib for Better Title Message Managment");
		}
		
		if(!React.hashed.contains("cyberpwnn/React/master/serve/war/hash.yml"))
		{
			React.setMef(true);
		}
	}
	
	public void stop()
	{
		
	}
}
