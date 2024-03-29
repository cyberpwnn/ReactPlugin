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
import org.cyberpwn.react.queue.TICK;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.N;
import org.cyberpwn.react.util.RegionProperty;

public class Action implements Actionable, Configurable
{
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	public static int APT = 0;
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
	protected GList<String> aliases;
	protected boolean sleepy;
	protected double maxSleepFactor;
	protected int sleepyTicks;
	protected long lastTick;
	protected double dfacx;
	
	public Action(ActionController actionController, Material material, String key, String cname, Integer idealTick, String name, String description, boolean manual)
	{
		aliases = new GList<String>();
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
		sleepy = true;
		lastTick = TICK.tick;
		maxSleepFactor = 3;
		sleepyTicks = 20;
		dfacx = 5.353;
		
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
		
		if(enabled)
		{
			long since = TICK.tick - lastTick;
			
			if(isAsleep())
			{
				if(since < sleepyTicks)
				{
					return;
				}
			}
			
			nap();
			lastTick = TICK.tick;
			act();
			APT++;
		}
	}
	
	public void nap()
	{
		sleepyTicks = (int) ((maxSleepFactor * idealTick * Math.random() * dfacx) + 2);
	}
	
	public boolean isAsleep()
	{
		if(getActionController().getReact().getSampleController().isCaffeine())
		{
			return false;
		}
		
		if(!sleepy)
		{
			return false;
		}
		
		if(sleepyTicks > 0)
		{
			return true;
		}
		
		return false;
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
		lastTick = TICK.tick;
		nap();
		
		if(mae.isCancelled())
		{
			return;
		}
		
		N.t("Manual Action " + getName(), "name", p.getName());
	}
	
	public void notifyOf(String s, CommandSender ex)
	{
		for(Player j : Bukkit.getOnlinePlayers())
		{
			if(j.hasPermission(Info.PERM_MONITOR) && React.isBroadcast() && !j.equals(ex))
			{
				j.sendMessage(Info.TAG + ChatColor.GRAY + (ex.getName()) + " ran " + s);
			}
		}
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
	
	@Override
	public GList<String> getAliases()
	{
		return aliases;
	}
}
