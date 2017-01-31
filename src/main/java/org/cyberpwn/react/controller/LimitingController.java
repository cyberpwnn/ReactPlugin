package org.cyberpwn.react.controller;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.ExecutiveIterator;
import org.cyberpwn.react.util.ExecutiveRunnable;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.M;
import org.cyberpwn.react.util.TaskLater;

@SuppressWarnings("deprecation")
public class LimitingController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private GMap<Player, Long> playerActions;
	private Integer playerLimit;
	
	public LimitingController(React react)
	{
		super(react);
		
		cc = new ClusterConfig();
		playerActions = new GMap<Player, Long>();
		playerLimit = Bukkit.getServer().getMaxPlayers();
	}
	
	@Override
	public void start()
	{
		for(Player i : getReact().onlinePlayers())
		{
			playerActions.put(i, M.ms());
		}
	}
	
	@Override
	public void tick()
	{
		if(cc.getBoolean("limiting.enable") && getReact().getActionController().getActionInstabilityCause().isLagging())
		{
			if(cc.getBoolean("limiting.players.change-player-limit"))
			{
				playerLimit = cc.getInt("limiting.players.new-player-limit");
				
				if(cc.getBoolean("limiting.players.kick-excess-players") && Bukkit.getServer().getOnlinePlayers().size() > playerLimit)
				{
					GList<Player> kickme = new GList<Player>();
					Integer kicks = Bukkit.getServer().getOnlinePlayers().size() - playerLimit;
					
					if(kicks > 0)
					{
						if(cc.getBoolean("limiting.players.kick-most-afk-first"))
						{
							GList<Long> order = playerActions.v();
							Collections.sort(order);
							Collections.reverse(order);
							
							for(int i = 0; i < kicks; i++)
							{
								if(order.hasIndex(i))
								{
									kickme.add(playerActions.findKey(order.get(i)));
								}
							}
						}
						
						else
						{
							for(int i = 0; i < kicks; i++)
							{
								kickme.add(getReact().onlinePlayers()[i]);
							}
						}
						
						new ExecutiveIterator<Player>(0.4, kickme.copy(), new ExecutiveRunnable<Player>()
						{
							@Override
							public void run()
							{
								kickPlayer(next());
							}
						}, new Runnable()
						{
							@Override
							public void run()
							{
								
							}
						});
					}
				}
			}
		}
		
		else
		{
			playerLimit = Bukkit.getServer().getMaxPlayers();
		}
	}
	
	public void kickPlayer(Player p)
	{
		new TaskLater(1)
		{
			@Override
			public void run()
			{
				p.kickPlayer(F.color(cc.getString("limiting.players.kick-message")));
			}
		};
	}
	
	public void kickPlayerFull(Player p)
	{
		new TaskLater(1)
		{
			@Override
			public void run()
			{
				p.kickPlayer(F.color(cc.getString("limiting.players.server-full-message")));
			}
		};
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(PlayerChatEvent e)
	{
		act(e.getPlayer());
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		if(Bukkit.getOnlinePlayers().size() + 1 > playerLimit)
		{
			kickPlayerFull(e.getPlayer());
			
			return;
		}
		
		playerActions.put(e.getPlayer(), M.ms());
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		playerActions.remove(e.getPlayer());
	}
	
	@EventHandler
	public void on(PlayerDeathEvent e)
	{
		try
		{
			act(e.getEntity());
			act(e.getEntity().getKiller());
		}
		
		catch(Exception xe)
		{
			
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(PlayerCommandPreprocessEvent e)
	{
		act(e.getPlayer());
	}
	
	@EventHandler
	public void on(PlayerMoveEvent e)
	{
		act(e.getPlayer());
	}
	
	@EventHandler
	public void on(BlockBreakEvent e)
	{
		act(e.getPlayer());
	}
	
	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		act(e.getPlayer());
	}
	
	@EventHandler
	public void on(ServerListPingEvent e)
	{
		try
		{
			if(cc.getBoolean("limiting.players.show-changed-limit"))
			{
				e.setMaxPlayers(playerLimit);
			}
		}
		
		catch(Exception ex)
		{
			
		}
	}
	
	public void act(Player p)
	{
		playerActions.put(p, M.ms());
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("limiting.enable", false, "When enabled, start limiting based on the settings provieded.\nThis will only take effect when the server is lagging");
		cc.set("limiting.players.new-player-limit", (int) (Bukkit.getServer().getMaxPlayers() / 1.5) + 1, "When the server starts lagging, change the player limit to the specified number.\nThis will not display the new player limit on the serverlist, but you can change that below.");
		cc.set("limiting.players.show-changed-limit", false, "Shows the new player limit in the server tab list and websites when the player limit is changed.\nWarning, this may alter the way any server list plugins you have work.");
		cc.set("limiting.players.change-player-limit", true, "Should react touch the player limit (virtual)?");
		cc.set("limiting.players.kick-excess-players", false, "Kicks the players over the new server limit.");
		cc.set("limiting.players.kick-most-afk-first", true, "Attempts to kick the most afk users first.");
		cc.set("limiting.players.kick-message", "&cKicked for AFK (too many players also)", "Send a color coded kick message to kicked players.");
		cc.set("limiting.players.server-full-message", "&cServer is Full!", "Send a color coded kick message to joining players.");
	}
	
	@Override
	public void onReadConfig()
	{
		// Dynamic
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "limits";
	}
}
