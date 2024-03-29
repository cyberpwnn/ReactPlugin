package org.cyberpwn.react.action;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;

public class ActionReStackEntities extends Action implements Listener
{
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	
	public ActionReStackEntities(ActionController actionController)
	{
		super(actionController, Material.SLIME_BALL, "re-stack-entities", "ActionReStackEntities", 100, "Re-Stack Entities", L.ACTION_RESTACKENTITIES, true);
		
		aliases.add("restack");
		maxSleepFactor = 3.2;
	}
	
	@Override
	public void act()
	{
		
	}
	
	@Override
	public void manual(CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		super.manual(p);
		long ms = System.currentTimeMillis();
		
		ActionStackEntities ase = actionController.getActionStackEntities();
		ase.unstackClear();
		String msg = ChatColor.WHITE + getName() + ChatColor.GRAY + " in " + ChatColor.WHITE + (System.currentTimeMillis() - ms) + "ms";
		p.sendMessage(Info.TAG + msg);
		notifyOf(msg, p);
	}
	
	@Override
	public void onReadConfig()
	{
		super.onReadConfig();
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
	}
}
