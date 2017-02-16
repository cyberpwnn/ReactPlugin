package org.cyberpwn.react.controller;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.HijackedConsole;

public class ConsoleController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private HijackedConsole hc;
	
	public ConsoleController(React react)
	{
		super(react);
		
		cc = new ClusterConfig();
	}
	
	@Override
	public void start()
	{
		hc = new HijackedConsole(cc);
		HijackedConsole.hijacked = true;
		s("Console hijack enabled");
		hc.start();
	}
	
	@Override
	public void stop()
	{
		s("Console hijack disabled");
		HijackedConsole.hijacked = false;
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("hijack-console", true, "Takes over the output stream of the console.\nThe purpose of this is to read the console, then print it back out.\nYou should not notice any difference with this on.");
		cc.set("console-hacks.color", true, "Allow color to be removed from the console.");
		cc.set("console-hacks.update-interval", 50, "Modify the update speed of the console in milliseconds.");
	}
	
	@Override
	public void onReadConfig()
	{
		HijackedConsole.hijacked = cc.getBoolean("hijack-console");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "console";
	}
}
