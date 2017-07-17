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
import org.cyberpwn.react.util.StackedEntity;
import org.cyberpwn.react.util.Task;

public class ActionUnStackEntities extends Action implements Listener
{
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	
	public ActionUnStackEntities(ActionController actionController)
	{
		super(actionController, Material.WOOD_SWORD, "un-stack-entities", "ActionUnStackEntities", 100, "Un-Stack Entities", L.ACTION_UNSTACKENTITIES, true);
		
		aliases.add("unstack");
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
		
		new Task(0)
		{
			@Override
			public void run()
			{
				for(StackedEntity i : ase.getStacks().copy())
				{
					if(i.getSize() < 2)
					{
						ase.removeStack(i);
					}
					
					else
					{
						ase.unstack(i.getHost());
					}
				}
				
				if(ase.getStacks().isEmpty())
				{
					cancel();
				}
			}
		};
		
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
