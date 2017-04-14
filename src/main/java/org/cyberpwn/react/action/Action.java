package org.cyberpwn.react.action;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.N;
import org.cyberpwn.react.util.RegionProperty;

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
		enabled = true;
		this.description = description;
		cc = new ClusterConfig();
		this.cname = cname;
		reactionTime = 0l;
		this.manual = manual;
		
		actionController.registerAction(this);
	}
	
	public boolean can(Location l)
	{
		if(getActionController().getReact().getRegionController().getProperties(l).contains(RegionProperty.DENY_REACTION))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void preAct()
	{
		if(React.dreact)
		{
			return;
		}
		
		if(enabled && !React.isMef())
		{
			N.t("Fired Action " + getName());
			act();
		}
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
		

		for(Player j : Bukkit.getOnlinePlayers())
		{
			if(j.hasPermission(Info.PERM_MONITOR) && React.isBroadcast() && !j.equals(p))
			{
				j.sendMessage(Info.TAG + p.getName() + " Executed " + getKey());
			}
		}
		
		N.t("Manual Action " + getName(), "name", p.getName());
		p.sendMessage(Info.TAG + ChatColor.YELLOW + L.MESSAGE_MANUAL + getName() + L.MESSAGE_MANUAL_STARTED);
	}
	
	@Override
	public void start()
	{
		
	}
	
	@Override
	public void stop()
	{
		
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}
	
	public ActionController getActionController()
	{
		return actionController;
	}
	
	@Override
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
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("component.enable", true, "ABOUT " + getName() + "\n" + getDescription() + "\n\nYou can disable " + getName() + " here.");
		cc.set("component.interval", idealTick, "Its typically not a good idea to change this\nunless you know what you are doing.");
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
		reactionTime = ns;
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
	
	@Override
	public Material getMaterial()
	{
		return material;
	}
	
	@Override
	public String getKey()
	{
		return key;
	}
	
	public Boolean getEnabled()
	{
		return enabled;
	}
}
