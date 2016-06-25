package org.cyberpwn.react.action;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ManualActionEvent;

import net.md_5.bungee.api.ChatColor;

public class Action implements Actionable, Configurable
{
	protected final ActionController actionController;
	protected final String name;
	protected final String cname;
	protected final String description;
	protected final boolean manual;
	protected final ClusterConfig cc;
	protected final Material material;
	protected final String key;
	protected Long reactionTime;
	protected Boolean enabled;
	protected Integer idealTick;
	
	public Action(ActionController actionController, Material material, String key, String cname, Integer idealTick, String name, String description, boolean manual)
	{
		this.actionController = actionController;
		this.idealTick = idealTick;
		this.material = material;
		this.key = key;
		this.name = name;
		this.enabled = true;
		this.description = description;
		this.cc = new ClusterConfig();
		this.cname = cname;
		this.reactionTime = 0l;
		this.manual = manual;
		
		actionController.registerAction(this);
	}
	
	public void preAct()
	{
		if(enabled && !React.isMef())
		{
			act();
		}
	}
	
	public void act()
	{
		
	}
	
	public void manual(CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		p.sendMessage(Info.TAG + ChatColor.YELLOW + L.MESSAGE_MANUAL + getName() + L.MESSAGE_MANUAL_STARTED);
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public ActionController getActionController()
	{
		return actionController;
	}
	
	public int getIdealTick()
	{
		if(idealTick == null)
		{
			idealTick = 100;
		}
		
		return idealTick;
	}
	
	public Collection<? extends Player> players()
	{
		return actionController.getReact().getServer().getOnlinePlayers();
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("component.enable", true);
		cc.set("component.interval", idealTick);
	}
	
	@Override
	public void onReadConfig()
	{
		enabled = cc.getBoolean("component.enable");
		idealTick = cc.getInt("component.interval");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return F.cname(cname);
	}
	
	@Override
	public long getReactionTime()
	{
		return reactionTime;
	}
	
	@Override
	public void setReactionTime(long ns)
	{
		this.reactionTime = ns;
	}
	
	@Override
	public Boolean isManual()
	{
		return manual;
	}
	
	public String getCname()
	{
		return cname;
	}
	
	public ClusterConfig getCc()
	{
		return cc;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public Boolean getEnabled()
	{
		return enabled;
	}
}
