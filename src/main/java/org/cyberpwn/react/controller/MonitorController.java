package org.cyberpwn.react.controller;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.PostGCEvent;
import org.cyberpwn.react.api.SpikeEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.map.MapGraph;
import org.cyberpwn.react.map.Mapper;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.nms.PacketUtil;
import org.cyberpwn.react.nms.Title;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBiset;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.PlayerData;
import org.cyberpwn.react.util.ScreenMonitor;
import org.cyberpwn.react.util.Timer;
import org.cyberpwn.react.util.Verbose;
import org.cyberpwn.react.util.VersionBukkit;

public class MonitorController extends Controller implements Configurable
{
	private GMap<Player, GBiset<Integer, Integer>> monitors;
	private GList<Player> mapPause;
	private GMap<Player, Integer> locks;
	private Integer delay;
	private Integer currentDelay;
	private ScreenMonitor ms;
	private ClusterConfig cc;
	private GMap<Player, MapGraph> mappers;
	private Integer mTick;
	private Boolean dTick;
	private Mapper mapper;
	private int level;
	private int mLevel;
	private String disp;
	private int dispTicks;
	private int overflow;
	private PlayerController pc;
	
	public MonitorController(React react)
	{
		super(react);
		
		monitors = new GMap<Player, GBiset<Integer, Integer>>();
		currentDelay = 0;
		disp = "";
		level = 0;
		mLevel = 0;
		overflow = 0;
		dispTicks = 100;
		dTick = false;
		ms = new ScreenMonitor(react);
		delay = 0;
		mTick = 0;
		mapPause = new GList<Player>();
		cc = new ClusterConfig();
		mappers = new GMap<Player, MapGraph>();
		mapper = new Mapper();
		locks = new GMap<Player, Integer>();
	}
	
	@Override
	public void start()
	{
		pc = getReact().getPlayerController();
		react.getDataController().load("cache", this);
		
		if(!React.hashed.contains("cyberpwnn"))
		{
			React.setMef(true);
		}
	}
	
	@Override
	public void stop()
	{
		react.getDataController().save("cache", this);
	}
	
	@Override
	public void tick()
	{
		getReact().getScoreboardController().dispatch();
		
		dispTicks--;
		
		if(dispTicks <= 0)
		{
			disp = "";
		}
		
		if(currentDelay <= 0)
		{
			if(delay > 5)
			{
				delay = 5;
			}
			
			if(delay < 1)
			{
				delay = 1;
			}
			
			currentDelay = delay;
			
			if(!getReact().getConfiguration().getBoolean("monitor.ticking.dynamic"))
			{
				currentDelay = getReact().getConfiguration().getInt("monitor.ticking.base");
			}
			
			Timer t = new Timer();
			t.start();
			dispatch();
			t.stop();
			
			if(getReact().getConfiguration().getBoolean("monitor.ticking.dynamic"))
			{
				delay = (int) (2.0 * ((double) t.getTime() / 1000000.0));
			}
			
			else
			{
				delay = getReact().getConfiguration().getInt("monitor.ticking.base");
			}
		}
		
		if(dTick)
		{
			dTick = false;
			return;
		}
		
		for(Player p : new GList<Player>(mappers.keySet()))
		{
			boolean b = false;
			
			for(ItemStack i : p.getInventory().getContents())
			{
				if(i == null)
				{
					continue;
				}
				
				if(i.getType().equals(Material.MAP))
				{
					if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
					{
						b = true;
						break;
					}
				}
			}
			
			if(!b)
			{
				stopMapping(p);
			}
		}
		
		if(mTick > cc.getInt("map.interval"))
		{
			mTick = 0;
			mapper.sample(react.getSampleController());
			
			// for(Player p : mappers.keySet())
			// {
			// ItemStack mp = p.getItemInHand();
			// if(mp.getType() == Material.MAP)
			// {
			// if(mp.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
			// {
			// short d = mp.getDurability();
			// @SuppressWarnings("deprecation")
			// MapView map = Bukkit.getServer().getMap(d);
			//
			// for(MapRenderer r : map.getRenderers())
			// {
			// map.removeRenderer(r);
			// }
			//
			// map.addRenderer(mappers.get(p));
			// }
			// }
			// }
		}
		
		if(react.getSampleController().getSampleTicksPerSecond().getValue().getInteger() < 11)
		{
			dTick = true;
			tick();
			return;
		}
		
		mTick++;
		currentDelay--;
	}
	
	public void dispatch()
	{
		for(Player i : new GList<Player>(monitors.keySet()))
		{
			int his = i.getInventory().getHeldItemSlot();
			int ois = monitors.get(i).getA();
			int cg = monitors.get(i).getB();
			boolean tryMove = false;
			
			if(his != ois && i.isSneaking() && locks.containsKey(i))
			{
				tryMove = true;
			}
			
			else if(i.isSneaking() && !locks.containsKey(i))
			{
				if(his != ois)
				{
					if(ois == 0 && his > 7)
					{
						cg = ms.dec(cg);
					}
					
					else if(ois == 8 && his < 3)
					{
						cg = ms.inc(cg);
					}
					
					else if(his > ois)
					{
						cg = ms.inc(cg);
					}
					
					else if(his < ois)
					{
						cg = ms.dec(cg);
					}
					
					monitors.get(i).setB(cg);
					monitors.get(i).setA(his);
					pc.gpd(i).setMonitoringTab(cg);
				}
			}
			
			else
			{
				if(locks.containsKey(i))
				{
					monitors.get(i).setB(locks.get(i));
				}
				
				monitors.get(i).setA(his);
			}
			
			boolean light = false;
			
			if(getReact().getConfiguration().getBoolean("monitor.title-bolding"))
			{
				if(i.getLocation().getBlock().getLightLevel() < 10)
				{
					light = true;
				}
			}
			
			else
			{
				light = true;
			}
			
			Title tx = ms.update(monitors.get(i).getB(), i.isSneaking(), light);
			
			if(!ms.getIgnoreDisp().contains(i))
			{
				tx.setTitle(" ");
				
				if(!ms.doubled(monitors.get(i).getB()))
				{
					tx.setSubTitle(" ");
				}
			}
			
			if(tryMove)
			{
				tx.setAction(ChatColor.RED + "[LOCK] " + tx.getAction() + " " + ChatColor.RED + "[LOCK]");
			}
			
			if(getReact().getActionController().getActionInstabilityCause().issues())
			{
				tx.setAction(Info.COLOR_ERR + org.apache.commons.lang.StringUtils.repeat(">", level) + " " + ChatColor.RESET + tx.getAction() + Info.COLOR_ERR + " " + org.apache.commons.lang.StringUtils.repeat("<", level));
			}
			
			tx.send(i);
		}
	}
	
	public void broadcast(String s, String msg)
	{
		for(Player i : react.onlinePlayers())
		{
			if(i.hasPermission(Info.PERM_MONITOR))
			{
				i.sendMessage(String.format(Info.TAG_SPECIAL, s) + ChatColor.AQUA + msg);
			}
		}
	}
	
	public void startMapping(Player p)
	{
		startMapping(p, true);
	}
	
	public void stopMapping(Player p)
	{
		stopMapping(p, true);
	}
	
	public void startMapping(Player p, boolean disp)
	{
		if(!p.hasPermission(Info.PERM_MONITOR))
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_INSUFFICIENT_PERMISSION);
			return;
		}
		
		if(disp)
		{
			p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MAPPING_ENABLED);
		}
		
		if(!VersionBukkit.tc())
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + "WARNING: You are using 1.7, maps may be ``/glitchy.");
		}
		
		// ItemStack mapd = new ItemStack(Material.MAP);
		// mapd.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);
		//
		// if(p.getInventory().contains(mapd))
		// {
		// p.getInventory().remove(mapd);
		// }
		//
		// for(ItemStack i : p.getInventory().getContents())
		// {
		// if(i == null)
		// {
		// continue;
		// }
		//
		// if(i.getType().equals(Material.MAP))
		// {
		// if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
		// {
		// p.getInventory().remove(i);
		// }
		// }
		// }
		//
		// ItemStack map = new ItemStack(Material.MAP);
		// map.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);
		// p.getInventory().addItem(map);
		//
		// for(ItemStack i : p.getInventory().getContents())
		// {
		// if(i == null)
		// {
		// continue;
		// }
		//
		// if(i.getType().equals(Material.MAP))
		// {
		// if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
		// {
		// i.setAmount(1);
		// }
		// }
		// }
		//
		// pc.gpd(p).setMapping(true);
		// mappers.put(p, new MapGraph());
		// Verbose.x("monitor", p.getName() + ": Mapping enabled");
		//
		// try
		// {
		// cc.set("mappers",
		// cc.getStringList("mappers").qadd(p.getUniqueId().toString()).removeDuplicates());
		// }
		//
		// catch(Exception e)
		// {
		// onNewConfig(cc);
		// }
	}
	
	public void stopMapping(Player p, boolean disp)
	{
		mappers.remove(p);
		Verbose.x("monitor", p.getName() + ": Mapping disabled");
		if(disp)
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_MAPPING_DISABLED);
		}
		
		cc.set("mappers", cc.getStringList("mappers").qdel(p.getUniqueId().toString()));
		ItemStack map = new ItemStack(Material.MAP);
		map.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);
		pc.gpd(p).setMapping(false);
		
		if(p.getInventory().contains(map))
		{
			p.getInventory().remove(map);
		}
		
		for(ItemStack i : p.getInventory().getContents())
		{
			if(i == null)
			{
				continue;
			}
			
			if(i.getType().equals(Material.MAP))
			{
				if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
				{
					p.getInventory().remove(i);
				}
			}
		}
	}
	
	public void toggleMapping(Player p)
	{
		if(isMapping(p))
		{
			stopMapping(p);
		}
		
		else
		{
			startMapping(p);
		}
	}
	
	public void toggleDisp(Player p)
	{
		ms.toggleDisp(p);
		
		if(!ms.getIgnoreDisp().contains(p))
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_VERBOSEOFF);
		}
		
		else
		{
			p.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + L.MESSAGE_VERBOSEON);
		}
	}
	
	public boolean isMapping(Player p)
	{
		return mappers.containsKey(p);
	}
	
	public boolean isMonitoring(Player p)
	{
		return monitors.containsKey(p);
	}
	
	public void lock(Player p)
	{
		if(locks.containsKey(p))
		{
			locks.remove(p);
			pc.gpd(p).setLockedTab(false);
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Monitor Unlocked. Use shift + scroll to change tabs.");
		}
		
		else
		{
			locks.put(p, monitors.get(p).getB());
			pc.gpd(p).setLockedTab(true);
			pc.gpd(p).setMonitoringTab(monitors.get(p).getB());
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Monitor Tab Locked. Until you use (/re mon -lock)");
		}
	}
	
	public void stopMonitoring(Player p)
	{
		monitors.remove(p);
		p.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_MONITORING_DISABLED);
		PacketUtil.sendActionBar(p, "  ");
		PacketUtil.clearTitle(p);
		pc.gpd(p).setMonitoring(false);
		cc.set("monitors", cc.getStringList("monitors").qdel(p.getUniqueId().toString()));
	}
	
	public void startMonitoring(Player p)
	{
		if(!p.hasPermission(Info.PERM_MONITOR))
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_INSUFFICIENT_PERMISSION);
			stopMonitoring(p);
			return;
		}
		
		if(!VersionBukkit.tc())
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + "1.7 is not 100% Compatible yet. Using Chat.");
			p.sendMessage(ms.getRoot());
			return;
		}
		
		pc.gpd(p).setMonitoring(true);
		cc.set("monitors", cc.getStringList("monitors").qadd(p.getUniqueId().toString()).removeDuplicates());
		monitors.put(p, new GBiset<Integer, Integer>(p.getInventory().getHeldItemSlot(), pc.gpd(p).getMonitoringTab()));
		
		if(!pc.exists(p))
		{
			Title t = new Title("", ChatColor.AQUA + "" + ChatColor.BOLD + "<SHIFT> + Scroll Your Mouse", 20, 20, 30);
			t.send(p);
		}
		
		if(pc.gpd(p).isLockedTab())
		{
			locks.put(p, pc.gpd(p).getMonitoringTab());
		}
		
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MONITORING_ENABLED);
	}
	
	public void toggleMonitoring(Player p)
	{
		if(isMonitoring(p))
		{
			stopMonitoring(p);
		}
		
		else
		{
			startMonitoring(p);
		}
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("map.interval", 5);
		cc.set("monitors", new GList<String>());
		cc.set("mappers", new GList<String>());
	}
	
	@Override
	public void onReadConfig()
	{
		for(Player i : react.getServer().getOnlinePlayers())
		{
			if(cc.getStringList("monitors").contains(i.getUniqueId().toString()))
			{
				startMonitoring(i);
			}
			
			if(cc.getStringList("mappers").contains(i.getUniqueId().toString()))
			{
				startMapping(i);
			}
		}
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "monitors";
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		ItemStack map = new ItemStack(Material.MAP);
		map.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);
		
		if(p.hasPermission(Info.PERM_MONITOR))
		{
			PlayerData pd = pc.gpd(p);
			pd.setMonitoring(isMonitoring(p));
			pd.setMapping(isMapping(p));
			pd.setLockedTab(locks.containsKey(p));
			
			if(isMonitoring(p))
			{
				pd.setMonitoringTab(monitors.get(p).getB());
			}
			
			pc.spd(p, pd);
			pc.save(p);
		}
		
		if(p.getInventory().contains(map))
		{
			p.getInventory().remove(map);
		}
		
		for(ItemStack i : p.getInventory().getContents())
		{
			if(i == null)
			{
				continue;
			}
			
			if(i.getType().equals(Material.MAP))
			{
				if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
				{
					p.getInventory().remove(i);
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e)
	{
		react.scheduleSyncTask(0, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					cc.getStringList("monitors").copy();
				}
				
				catch(Exception e)
				{
					onNewConfig(cc);
					return;
				}
				
				for(String i : cc.getStringList("monitors").copy())
				{
					if(e.getPlayer().getUniqueId().toString().equals(i))
					{
						if(!VersionBukkit.tc())
						{
							cc.set("monitors", cc.getStringList("monitors").qdel(e.getPlayer().getUniqueId().toString()));
							return;
						}
						
						startMonitoring(e.getPlayer());
						monitors.get(e.getPlayer()).setB(pc.gpd(e.getPlayer()).getMonitoringTab());
					}
				}
				
				for(String i : cc.getStringList("mappers").copy())
				{
					if(e.getPlayer().getUniqueId().toString().equals(i))
					{
						startMapping(e.getPlayer());
					}
				}
			}
		});
		
		if((e.getPlayer().isOp() || e.getPlayer().hasPermission(Info.PERM_RELOAD)))
		{
			react.scheduleSyncTask(5, new Runnable()
			{
				@Override
				public void run()
				{
					new Fetcher("https://raw.githubusercontent.com/cyberpwnn/React/master/serve/package.yml", new FCCallback()
					{
						@Override
						public void run()
						{
							String desc = "";
							List<String> description = fc().getStringList("pack.description");
							
							for(String i : description)
							{
								desc = desc + ChatColor.GREEN + i;
							}
							
							String version = fc().getString("package.version");
							int versionCode = fc().getInt("package.version-code");
							
							if(versionCode > Info.VERSION_CODE)
							{
								if((e.getPlayer().isOp() || e.getPlayer().hasPermission(Info.PERM_RELOAD)))
								{
									e.getPlayer().sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + L.MESSAGE_UPDATE_FOUND + ChatColor.GREEN + "v" + version);
									e.getPlayer().sendMessage(Info.TAG + ChatColor.YELLOW + "Use /re version " + ChatColor.GREEN + " for more information.");
									e.getPlayer().sendMessage(Info.TAG + ChatColor.YELLOW + "Use /re update " + ChatColor.GREEN + " to update to this version.");
								}
							}
						}
					}).start();
				}
			});
		}
	}
	
	@EventHandler
	public void onOpenInventory(InventoryOpenEvent e)
	{
		Player p = (Player) e.getPlayer();
		
		if(mappers.containsKey(p))
		{
			stopMapping(p, false);
			mapPause.add(p);
		}
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e)
	{
		Player p = (Player) e.getPlayer();
		
		if(mapPause.contains(p))
		{
			mapPause.remove(p);
			startMapping(p, false);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		if(mappers.containsKey(e.getPlayer()))
		{
			ItemStack i = e.getItemDrop().getItemStack();
			
			if(i.getType().equals(Material.MAP))
			{
				if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
				{
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPickup(InventoryClickEvent e)
	{
		ItemStack i = e.getCurrentItem();
		
		if(i == null)
		{
			return;
		}
		
		if(i.getType().equals(Material.MAP))
		{
			if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
			{
				e.setCancelled(true);
			}
		}
		
		if(i.getType().equals(Material.WRITTEN_BOOK))
		{
			if(i != null && i.getType().equals(Material.WRITTEN_BOOK) && i.hasItemMeta())
			{
				BookMeta bm = (BookMeta) i.getItemMeta();
				
				if(bm.getAuthor().equals(Info.NAME))
				{
					e.setCancelled(true);
					e.getInventory().remove(i);
				}
			}
		}
	}
	
	@EventHandler
	public void onPickup(InventoryPickupItemEvent e)
	{
		ItemStack i = e.getItem().getItemStack();
		
		if(i.getType().equals(Material.MAP))
		{
			if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onWorldChange(final PlayerTeleportEvent e)
	{
		if(!e.getFrom().getWorld().equals(e.getTo().getWorld()))
		{
			if(isMapping(e.getPlayer()))
			{
				Verbose.x("event", e.getPlayer().getName() + ChatColor.YELLOW + "World Change. SWAPPING MAPPING");
				stopMapping(e.getPlayer());
				
				react.scheduleSyncTask(10, new Runnable()
				{
					@Override
					public void run()
					{
						startMapping(e.getPlayer());
					}
				});
			}
		}
	}
	
	public void disp(String s)
	{
		if(dispTicks > 0)
		{
			overflow++;
		}
		
		else
		{
			overflow = 0;
		}
		
		if(overflow > 0)
		{
			s = s + Info.COLOR_ERR + "  (" + ChatColor.GOLD + overflow + Info.COLOR_ERR + ")";
		}
		
		disp = s;
		dispTicks = 30;
	}
	
	@EventHandler
	public void onGC(final PostGCEvent e)
	{
		Verbose.x("memory", Info.COLOR_ERR + "GC <> " + ChatColor.YELLOW + F.mem(e.getSize()) + " " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getTime().getTotalDuration() / 1000.0), 2) + "s");
		
		react.scheduleSyncTask(20, new Runnable()
		{
			@Override
			public void run()
			{
				disp(Info.COLOR_ERR + "GC <> " + ChatColor.YELLOW + F.mem(e.getSize()) + " " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getTime().getTotalDuration() / 1000.0), 2) + "s");
			}
		});
		
	}
	
	@EventHandler
	public void onSpike(SpikeEvent e)
	{
		Verbose.x("performence", Info.COLOR_ERR + "SPIKE <> " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getLockTime().getTotalDuration() / 1000.0), 2) + "s");
		disp(Info.COLOR_ERR + "SPIKE <> " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getLockTime().getTotalDuration() / 1000.0), 2) + "s");
	}
	
	public GMap<Player, GBiset<Integer, Integer>> getMonitors()
	{
		return monitors;
	}
	
	public void setMonitors(GMap<Player, GBiset<Integer, Integer>> monitors)
	{
		this.monitors = monitors;
	}
	
	public GList<Player> getMapPause()
	{
		return mapPause;
	}
	
	public void setMapPause(GList<Player> mapPause)
	{
		this.mapPause = mapPause;
	}
	
	public Integer getDelay()
	{
		return delay;
	}
	
	public void setDelay(Integer delay)
	{
		this.delay = delay;
	}
	
	public Integer getCurrentDelay()
	{
		return currentDelay;
	}
	
	public void setCurrentDelay(Integer currentDelay)
	{
		this.currentDelay = currentDelay;
	}
	
	public ScreenMonitor getMs()
	{
		return ms;
	}
	
	public void setMs(ScreenMonitor ms)
	{
		this.ms = ms;
	}
	
	public ClusterConfig getCc()
	{
		return cc;
	}
	
	public void setCc(ClusterConfig cc)
	{
		this.cc = cc;
	}
	
	public GMap<Player, MapGraph> getMappers()
	{
		return mappers;
	}
	
	public void setMappers(GMap<Player, MapGraph> mappers)
	{
		this.mappers = mappers;
	}
	
	public Integer getmTick()
	{
		return mTick;
	}
	
	public void setmTick(Integer mTick)
	{
		this.mTick = mTick;
	}
	
	public Boolean getdTick()
	{
		return dTick;
	}
	
	public void setdTick(Boolean dTick)
	{
		this.dTick = dTick;
	}
	
	public Mapper getMapper()
	{
		return mapper;
	}
	
	public void setMapper(Mapper mapper)
	{
		this.mapper = mapper;
	}
	
	public String getDisp()
	{
		return disp;
	}
	
	public void setDisp(String disp)
	{
		this.disp = disp;
	}
	
	public int getDispTicks()
	{
		return dispTicks;
	}
	
	public void setDispTicks(int dispTicks)
	{
		this.dispTicks = dispTicks;
	}
	
	public GMap<Player, Integer> getLocks()
	{
		return locks;
	}
	
	public void setLocks(GMap<Player, Integer> locks)
	{
		this.locks = locks;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public int getmLevel()
	{
		return mLevel;
	}
	
	public void setmLevel(int mLevel)
	{
		this.mLevel = mLevel;
	}
	
	public int getOverflow()
	{
		return overflow;
	}
	
	public void setOverflow(int overflow)
	{
		this.overflow = overflow;
	}
	
	public PlayerController getPc()
	{
		return pc;
	}
	
	public void setPc(PlayerController pc)
	{
		this.pc = pc;
	}
}
