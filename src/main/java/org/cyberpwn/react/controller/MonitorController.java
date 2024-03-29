package org.cyberpwn.react.controller;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.PostGCEvent;
import org.cyberpwn.react.api.SpikeEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.map.MapGraph;
import org.cyberpwn.react.map.Mapper;
import org.cyberpwn.react.nms.NMSX;
import org.cyberpwn.react.nms.PacketUtil;
import org.cyberpwn.react.nms.Title;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.CompassUtil;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBiset;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.HandledEvent;
import org.cyberpwn.react.util.LOAD;
import org.cyberpwn.react.util.N;
import org.cyberpwn.react.util.Platform;
import org.cyberpwn.react.util.PlayerData;
import org.cyberpwn.react.util.RegionProperty;
import org.cyberpwn.react.util.ScreenMonitor;
import org.cyberpwn.react.util.Timer;
import org.cyberpwn.react.util.Verbose;
import org.cyberpwn.react.util.VersionBukkit;

public class MonitorController extends Controller implements Configurable
{
	private GMap<Player, GBiset<Integer, Integer>> monitors;
	private GList<Player> pausedMonitors;
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
	private GList<Player> compass;
	private Average a;

	public MonitorController(React react)
	{
		super(react);

		a = new Average(9);
		compass = new GList<Player>();
		monitors = new GMap<Player, GBiset<Integer, Integer>>();
		currentDelay = 0;
		pausedMonitors = new GList<Player>();
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

	public void toggleCompass(Player p)
	{
		if(p.hasPermission(Info.PERM_MONITOR))
		{
			if(hasCompass(p))
			{
				disableCompass(p);
			}

			else
			{
				enableCompass(p);
			}
		}
	}

	public boolean hasCompass(Player p)
	{
		return compass.contains(p);
	}

	public void enableCompass(Player p)
	{
		if(!compass.contains(p))
		{
			compass.add(p);

			ItemStack i = new ItemStack(Material.COMPASS);
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(ChatColor.RED + "CPU Guage");
			im.setLore(new GList<String>().qadd(ChatColor.GRAY + "Measures CPU Utilization via Dial").qadd(ChatColor.GREEN + "Usage: 0%"));
			i.setItemMeta(im);
			i.addUnsafeEnchantment(Enchantment.DURABILITY, 1336);
			i.setAmount(1);
			p.getInventory().addItem(i);
		}
	}

	public void disableCompass(Player p)
	{
		compass.remove(p);

		p.setCompassTarget(p.getWorld().getSpawnLocation());

		for(int i = 0; i < p.getInventory().getSize() * 9; i++)
		{
			try
			{
				ItemStack is = p.getInventory().getItem(i);

				if(is != null && is.getType().equals(Material.COMPASS) && is.hasItemMeta())
				{
					ItemMeta im = is.getItemMeta();

					if(im.getDisplayName().startsWith(ChatColor.RED + "CPU Guage") && is.getEnchantmentLevel(Enchantment.DURABILITY) == 1336)
					{
						p.getInventory().setItem(i, new ItemStack(Material.AIR));
					}
				}
			}

			catch(Exception e)
			{

			}
		}
	}

	@Override
	public void stop()
	{
		for(Player i : compass.copy())
		{
			disableCompass(i);
		}

		react.getDataController().save("cache", this);
	}

	@Override
	public void tick()
	{
		getReact().getScoreboardController().dispatch();

		handleCompass();

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

			try
			{
				dispatch();
			}

			catch(Exception e)
			{
				e.printStackTrace();
			}

			if(getReact().getConfiguration().getBoolean("monitor.ticking.dynamic"))
			{
				delay = (int) (2.0 * (t.getTime() / 1000000.0));
			}

			else
			{
				delay = getReact().getConfiguration().getInt("monitor.ticking.base");
			}

			t.stop();
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

			for(Player p : mappers.keySet())
			{
				@SuppressWarnings("deprecation")
				ItemStack mp = p.getItemInHand();
				if(mp.getType() == Material.MAP)
				{
					if(mp.getEnchantmentLevel(Enchantment.DURABILITY) == 1337)
					{
						short d = mp.getDurability();
						@SuppressWarnings("deprecation")
						MapView map = Bukkit.getServer().getMap(d);

						for(MapRenderer r : map.getRenderers())
						{
							map.removeRenderer(r);
						}

						map.addRenderer(mappers.get(p));
					}
				}
			}
		}

		if(react.getSampleController().getSampleTicksPerSecond().getValue().getInteger() < 11)
		{
			dTick = true;
			tick();
			return;
		}

		mTick++;

		currentDelay -= 2;

		if(LOAD.HEAVY.min())
		{
			currentDelay++;
		}

		if(LOAD.LIGHT.min())
		{
			currentDelay--;
		}
	}

	public void handleCompass()
	{
		a.put(Platform.CPU.getProcessCPULoad() * Platform.CPU.getAvailableProcessors());

		for(Player i : compass)
		{
			CompassUtil.updatePlayerCompassFor(i, a.getAverage());
		}
	}

	public void dispatch()
	{
		for(Player i : getReact().onlinePlayers())
		{
			if(i.hasPermission(Info.PERM_MONITOR))
			{
				Location l = i.getLocation();

				if(isMapping(i) || mapPause.contains(i))
				{
					if(!mapPause.contains(i) && getReact().getRegionController().getProperties(l).contains(RegionProperty.DENY_MAPPING))
					{
						mapPause.add(i);
						stopMapping(i, false);
						i.sendMessage(Info.TAG + ChatColor.GOLD + L.MESSAGE_MAPPING_PAUSED + ChatColor.DARK_GRAY + " Region Denies Mapping");
					}

					if((i.getOpenInventory().getType().equals(InventoryType.CRAFTING) || i.getOpenInventory().getType().equals(InventoryType.CREATIVE)) && mapPause.contains(i) && !getReact().getRegionController().getProperties(l).contains(RegionProperty.DENY_MAPPING))
					{
						mapPause.remove(i);
						startMapping(i, true);
						i.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + L.MESSAGE_MAPPING_RESUMED + ChatColor.DARK_GRAY + "");
					}
				}
			}
		}

		for(Player i : new GList<Player>(monitors.keySet()))
		{
			Location l = i.getLocation();

			if(!pausedMonitors.contains(i) && getReact().getRegionController().getProperties(l).contains(RegionProperty.DENY_MONITORING))
			{
				pausedMonitors.add(i);
				NMSX.sendActionBar(i, "    ");
				i.sendMessage(Info.TAG + ChatColor.GOLD + L.MESSAGE_MONITORING_PAUSED + ChatColor.DARK_GRAY + " Region Denies Monitoring");
			}

			if(pausedMonitors.contains(i) && !getReact().getRegionController().getProperties(l).contains(RegionProperty.DENY_MONITORING))
			{
				pausedMonitors.remove(i);
				i.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + L.MESSAGE_MONITORING_RESUMED + ChatColor.DARK_GRAY + "");
			}

			if(pausedMonitors.contains(i))
			{
				continue;
			}

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

			boolean lxx = light;
			boolean tmm = tryMove;

			new ASYNC()
			{
				@Override
				public void async()
				{
					Title tx = ms.update(monitors.get(i).getB(), i.isSneaking(), lxx);

					if(!ms.getIgnoreDisp().contains(i))
					{
						tx.setTitle(" ");

						if(!ms.doubled(monitors.get(i).getB()))
						{
							tx.setSubTitle(" ");
						}
					}

					if(tmm)
					{
						tx.setAction(ChatColor.RED + "[LOCK] " + tx.getAction() + " " + ChatColor.RED + "[LOCK]");
					}

					if(getReact().getActionController().getActionInstabilityCause().issues())
					{
						tx.setAction(Info.COLOR_ERR + org.apache.commons.lang.StringUtils.repeat(">", level) + " " + ChatColor.RESET + tx.getAction() + Info.COLOR_ERR + " " + org.apache.commons.lang.StringUtils.repeat("<", level));
					}

					if(!VersionBukkit.tc())
					{
						String mon = tx.getAction();
						String mfa = tx.getSubTitle();
						tx.setAction("");
						tx.setSubTitle(mon);
						tx.setTitle(mfa);
					}

					tx.send(i);
				}
			};
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
		N.t("Mapping Started", "player", p.getName());

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

		ItemStack mapd = new ItemStack(Material.MAP);
		mapd.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);

		if(p.getInventory().contains(mapd))
		{
			p.getInventory().remove(mapd);
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

		ItemStack map = new ItemStack(Material.MAP);
		map.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);
		p.getInventory().addItem(map);

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
					i.setAmount(1);
				}
			}
		}

		pc.gpd(p).setMapping(true);
		mappers.put(p, new MapGraph());
		Verbose.x("monitor", p.getName() + ": Mapping enabled");

		try
		{
			cc.set("mappers", cc.getStringList("mappers").qadd(p.getUniqueId().toString()).removeDuplicates());
		}

		catch(Exception e)
		{
			onNewConfig(cc);
		}
	}

	public void stopMapping(Player p, boolean disp)
	{
		N.t("Mapping Stopped", "player", p.getName());

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
			N.t("Monitor Unlocked", "player", p.getName());

			locks.remove(p);
			pc.gpd(p).setLockedTab(false);
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Monitor Unlocked. Use shift + scroll to change tabs.");
		}

		else
		{
			N.t("Monitor Locked", "player", p.getName());

			locks.put(p, monitors.get(p).getB());
			pc.gpd(p).setLockedTab(true);
			pc.gpd(p).setMonitoringTab(monitors.get(p).getB());
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Monitor Tab Locked. Until you use (/re mon -lock)");
		}
	}

	public void stopMonitoring(Player p)
	{
		N.t("Monitoring Stopped", "player", p.getName());

		monitors.remove(p);
		p.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_MONITORING_DISABLED);
		PacketUtil.sendActionBar(p, "  ");
		PacketUtil.clearTitle(p);
		pc.gpd(p).setMonitoring(false);
		cc.set("monitors", cc.getStringList("monitors").qdel(p.getUniqueId().toString()));
	}

	public void startMonitoring(Player p)
	{
		N.t("Monitoring Started", "player", p.getName());

		if(!p.hasPermission(Info.PERM_MONITOR))
		{
			p.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_INSUFFICIENT_PERMISSION);
			stopMonitoring(p);
			return;
		}

		pc.gpd(p).setMonitoring(true);
		cc.set("monitors", cc.getStringList("monitors").qadd(p.getUniqueId().toString()).removeDuplicates());

		monitors.put(p, new GBiset<Integer, Integer>(p.getInventory().getHeldItemSlot(), pc.gpd(p).getMonitoringTab()));

		if(pc.gpd(p).isLockedTab())
		{
			locks.put(p, pc.gpd(p).getMonitoringTab());
		}

		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MONITORING_ENABLED + ChatColor.DARK_GRAY + " Hold <SHIFT> + Scroll Mouse");
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
		new HandledEvent()
		{

			@Override
			public void execute()
			{
				Player p = e.getPlayer();
				ItemStack map = new ItemStack(Material.MAP);
				map.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);

				if(p.hasPermission(Info.PERM_MONITOR))
				{
					try
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

					catch(Exception ex)
					{

					}
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

				pausedMonitors.remove(e.getPlayer());
				monitors.remove(e.getPlayer());
				compass.remove(e.getPlayer());
			}
		};
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
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
			}
		};
	}

	@EventHandler
	public void onOpenInventory(InventoryOpenEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
			{
				Player p = (Player) e.getPlayer();

				if(mappers.containsKey(p))
				{
					stopMapping(p, false);
					mapPause.add(p);
				}

				if(hasCompass(p))
				{
					for(int i = 0; i < p.getInventory().getSize() * 9; i++)
					{
						try
						{
							ItemStack is = p.getInventory().getItem(i);

							if(is != null && is.getType().equals(Material.COMPASS) && is.hasItemMeta())
							{
								ItemMeta im = is.getItemMeta();

								if(im.getDisplayName().startsWith(ChatColor.RED + "CPU Guage") && is.getEnchantmentLevel(Enchantment.DURABILITY) == 1336)
								{
									p.getInventory().setItem(i, new ItemStack(Material.AIR));
								}
							}
						}

						catch(Exception ex)
						{

						}
					}
				}
			}
		};
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
			{
				Player p = (Player) e.getPlayer();

				if(mapPause.contains(p) && !getReact().getRegionController().getProperties(e.getPlayer().getLocation()).contains(RegionProperty.DENY_MAPPING))
				{
					mapPause.remove(p);
					startMapping(p, false);
				}

				if(hasCompass(p))
				{
					ItemStack i = new ItemStack(Material.COMPASS);
					ItemMeta im = i.getItemMeta();
					im.setDisplayName(ChatColor.RED + "CPU Guage");
					im.setLore(new GList<String>().qadd(ChatColor.GRAY + "Measures CPU Utilization via Dial").qadd(ChatColor.GREEN + "Usage: 0%"));
					i.setItemMeta(im);
					i.addUnsafeEnchantment(Enchantment.DURABILITY, 1336);
					i.setAmount(1);
					p.getInventory().addItem(i);
				}
			}
		};
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
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

				if(hasCompass(e.getPlayer()))
				{
					ItemStack i = e.getItemDrop().getItemStack();

					if(i.getType().equals(Material.COMPASS))
					{
						if(i.getEnchantmentLevel(Enchantment.DURABILITY) == 1336)
						{
							e.setCancelled(true);
						}
					}
				}
			}
		};
	}

	@EventHandler
	public void onPickup(InventoryClickEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
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
		};
	}

	@EventHandler
	public void onPickup(InventoryPickupItemEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
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
		};
	}

	@EventHandler
	public void onWorldChange(final PlayerTeleportEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
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
		};
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
		new HandledEvent()
		{

			@Override
			public void execute()
			{
				Verbose.x("memory", Info.COLOR_ERR + "GC <> " + ChatColor.YELLOW + F.mem(e.getSize()) + " " + ChatColor.LIGHT_PURPLE + F.f((e.getTime().getTotalDuration() / 1000.0), 2) + "s");

				react.scheduleSyncTask(20, new Runnable()
				{
					@Override
					public void run()
					{
						disp(Info.COLOR_ERR + "GC <> " + ChatColor.YELLOW + F.mem(e.getSize()) + " " + ChatColor.LIGHT_PURPLE + F.f((e.getTime().getTotalDuration() / 1000.0), 2) + "s");
					}
				});
			}
		};

	}

	@EventHandler
	public void onSpike(SpikeEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
			{
				Verbose.x("performence", Info.COLOR_ERR + "SPIKE <> " + ChatColor.LIGHT_PURPLE + F.f((e.getLockTime().getTotalDuration() / 1000.0), 2) + "s");
				disp(Info.COLOR_ERR + "SPIKE <> " + ChatColor.LIGHT_PURPLE + F.f((e.getLockTime().getTotalDuration() / 1000.0), 2) + "s");
			}
		};
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
