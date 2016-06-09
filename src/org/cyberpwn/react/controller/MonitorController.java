package org.cyberpwn.react.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.bukkit.Bukkit;
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
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.PostGCEvent;
import org.cyberpwn.react.api.SpikeEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.json.VersionBukkit;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.map.MapGraph;
import org.cyberpwn.react.map.Mapper;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.nms.PacketUtil;
import org.cyberpwn.react.nms.Title;
import org.cyberpwn.react.object.GBiset;
import org.cyberpwn.react.object.GList;
import org.cyberpwn.react.object.GMap;
import org.cyberpwn.react.object.MonitorScreen;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Verbose;

public class MonitorController extends Controller implements Configurable
{
	private GMap<Player, GBiset<Integer, Integer>> monitors;
	private GList<Player> mapPause;
	private GMap<Player, Integer> locks;
	private Integer delay;
	private Integer currentDelay;
	private MonitorScreen ms;
	private ClusterConfig cc;
	private GMap<Player, MapGraph> mappers;
	private Integer mTick;
	private Boolean dTick;
	private Mapper mapper;
	private int level;
	private int mLevel;
	private GMap<Player, Integer> packeted;
	private String disp;
	private int dispTicks;
	private int overflow;
	
	public MonitorController(React react)
	{
		super(react);
		
		packeted = new GMap<Player, Integer>();
		monitors = new GMap<Player, GBiset<Integer, Integer>>();
		currentDelay = 0;
		disp = "";
		level = 0;
		mLevel = 0;
		overflow = 0;
		dispTicks = 100;
		dTick = false;
		ms = new MonitorScreen(react);
		delay = 0;
		mTick = 0;
		mapPause = new GList<Player>();
		cc = new ClusterConfig();
		mappers = new GMap<Player, MapGraph>();
		mapper = new Mapper();
		locks = new GMap<Player, Integer>();
	}
	
	public void start()
	{
		react.getDataController().load("cache", this);
	}
	
	public void stop()
	{
		react.getDataController().save("cache", this);
	}
	
	public void tick()
	{
		dispTicks--;
		
		if(dispTicks <= 0)
		{
			disp = "";
		}
		
		if(currentDelay <= 0)
		{
			currentDelay = delay;
			mLevel++;
			
			if(mLevel > 2)
			{
				mLevel = 0;
				
				level++;
				
				if(level > 6)
				{
					level = 0;
				}
			}
			
			dispatch();
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
		currentDelay--;
	}
	
	public void dispatch()
	{
		for(Player i : new GList<Player>(monitors.keySet()))
		{
			int his = i.getInventory().getHeldItemSlot();
			int ois = monitors.get(i).getA();
			int cg = monitors.get(i).getB();
			
			if(i.isSneaking() && !locks.containsKey(i))
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
					React.gpd(i).setMonitoringTab(cg);
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
			
			if(packeted.containsKey(i))
			{
				packeted.put(i, packeted.get(i) - 1);
				
				if(packeted.get(i) < 40)
				{
					PacketUtil.sendActionBar(i, ChatColor.LIGHT_PURPLE + "Monitoring will Resume " + ChatColor.AQUA + "shortly");
				}
				
				else
				{
					PacketUtil.sendActionBar(i, ChatColor.LIGHT_PURPLE + "Monitoring will Resume in " + ChatColor.AQUA + F.f((double) packeted.get(i) / 20.0, 0) + " seconds");
				}
				
				if(packeted.get(i) <= 0)
				{
					packeted.remove(i);
				}
				
				return;
			}
			
			Title tx = ms.update(monitors.get(i).getB());
			
			if(!ms.getIgnoreDisp().contains(i))
			{
				tx.setTitle(" ");
				
				if(!ms.doubled(monitors.get(i).getB()))
				{
					tx.setSubTitle(" ");
				}
			}
			
			if(getReact().getActionController().getActionInstabilityCause().issues())
			{
				tx.setAction(ChatColor.RED + org.apache.commons.lang.StringUtils.repeat(">", level)
						
						+ " " + ChatColor.RESET + tx.getAction() + ChatColor.RED + " " +
						
						org.apache.commons.lang.StringUtils.repeat("<", level));
			}
			
			tx.send(i);
			packeted.remove(i);
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
			p.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_INSUFFICIENT_PERMISSION);
			return;
		}
		
		if(disp)
		{
			p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MAPPING_ENABLED);
		}
		
		if(!VersionBukkit.tc())
		{
			p.sendMessage(Info.TAG + ChatColor.RED + "WARNING: You are using 1.7, maps may be ``/glitchy.");
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
		
		React.gpd(p).setMapping(true);
		mappers.put(p, new MapGraph());
		Verbose.x("monitor", p.getName() + ": Mapping enabled");
		
		try
		{
			cc.set("mappers", cc.getStringList("mappers").qadd(p.getUniqueId().toString()).removeDuplicates());
		}
		
		catch(Exception e)
		{
			onNewConfig();
		}
	}
	
	public void stopMapping(Player p, boolean disp)
	{
		mappers.remove(p);
		Verbose.x("monitor", p.getName() + ": Mapping disabled");
		if(disp)
		{
			p.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_MAPPING_DISABLED);
		}
		
		cc.set("mappers", cc.getStringList("mappers").qdel(p.getUniqueId().toString()));
		ItemStack map = new ItemStack(Material.MAP);
		map.addUnsafeEnchantment(Enchantment.DURABILITY, 1337);
		React.gpd(p).setMapping(false);
		
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
			p.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_VERBOSEOFF);
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
			React.gpd(p).setLockedTab(false);
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Monitor Unlocked. Use shift + scroll to change tabs.");
		}
		
		else
		{
			locks.put(p, monitors.get(p).getB());
			React.gpd(p).setLockedTab(true);
			React.gpd(p).setMonitoringTab(monitors.get(p).getB());
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Monitor Tab Locked. Until you use (/re mon -lock)");
		}
	}
	
	public void stopMonitoring(Player p)
	{
		React.gpd(p).setMonitoringTab(monitors.get(p).getB());
		monitors.remove(p);
		p.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_MONITORING_DISABLED);
		PacketUtil.sendActionBar(p, "  ");
		PacketUtil.clearTitle(p);
		React.gpd(p).setMonitoring(false);
		cc.set("monitors", cc.getStringList("monitors").qdel(p.getUniqueId().toString()));
	}
	
	public void startMonitoring(Player p)
	{
		if(!p.hasPermission(Info.PERM_MONITOR))
		{
			p.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_INSUFFICIENT_PERMISSION);
			stopMonitoring(p);
			return;
		}
		
		if(!VersionBukkit.tc())
		{
			p.sendMessage(Info.TAG + ChatColor.RED + "1.7 is not 100% Compatible yet. Using Chat.");
			p.sendMessage(ms.getRoot());
			return;
		}
		
		React.gpd(p).setMonitoring(true);
		cc.set("monitors", cc.getStringList("monitors").qadd(p.getUniqueId().toString()).removeDuplicates());
		monitors.put(p, new GBiset<Integer, Integer>(p.getInventory().getHeldItemSlot(), React.gpd(p).getMonitoringTab()));
		
		if(React.gpd(p).isLockedTab())
		{
			locks.put(p, React.gpd(p).getMonitoringTab());
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
	public void onNewConfig()
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
	
	public String fromServer(String server)
	{
		JSONObject jsx = getReact().getBungeeController().get(server);
		JSONObject js = jsx.getJSONObject("data");
		
		if(js != null)
		{
			String msg = " ";
			
			msg = msg + ChatColor.GREEN + F.f(js.getDouble("sample-ticks-per-second"), 1) + " tps ";
			msg = msg + ChatColor.GOLD + F.f(js.getDouble("sample-memory-used"), 0) + " mb ";
			msg = msg + ChatColor.RED + F.f(js.getDouble("sample-chunks-loaded"), 0) + " chunks ";
			
			return msg;
		}
		
		return ChatColor.RED + "Unknown";
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
			React.gpd(p).setMonitoring(isMonitoring(p));
			React.gpd(p).setMapping(isMapping(p));
			React.gpd(p).setLockedTab(locks.containsKey(p));
			
			if(isMonitoring(p))
			{
				React.gpd(p).setMonitoringTab(monitors.get(p).getB());
			}
		}
		
		React.spd(p);
		
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
					onNewConfig();
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
						monitors.get(e.getPlayer()).setB(React.gpd(e.getPlayer()).getMonitoringTab());
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
					try
					{
						new Fetcher(new URL("https://raw.githubusercontent.com/cyberpwnn/React/master/serve/package.yml"), new FCCallback()
						{
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
					
					catch(MalformedURLException e)
					{
						
					}
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
			s = s + ChatColor.RED + "  (" + ChatColor.GOLD + overflow + ChatColor.RED + ")";
		}
		
		disp = s;
		dispTicks = 30;
	}
	
	@EventHandler
	public void onGC(final PostGCEvent e)
	{
		Verbose.x("memory", ChatColor.RED + "GC <> " + ChatColor.YELLOW + F.mem(e.getSize()) + " " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getTime().getTotalDuration() / 1000.0), 2) + "s");
		
		react.scheduleSyncTask(20, new Runnable()
		{
			@Override
			public void run()
			{
				disp(ChatColor.RED + "GC <> " + ChatColor.YELLOW + F.mem(e.getSize()) + " " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getTime().getTotalDuration() / 1000.0), 2) + "s");
			}
		});
		
	}
	
	@EventHandler
	public void onSpike(SpikeEvent e)
	{
		Verbose.x("performence", ChatColor.RED + "SPIKE <> " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getLockTime().getTotalDuration() / 1000.0), 2) + "s");
		disp(ChatColor.RED + "SPIKE <> " + ChatColor.LIGHT_PURPLE + F.f(((double) e.getLockTime().getTotalDuration() / 1000.0), 2) + "s");
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
	
	public MonitorScreen getMs()
	{
		return ms;
	}
	
	public void setMs(MonitorScreen ms)
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
	
	public GMap<Player, Integer> getPacketed()
	{
		return packeted;
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
	
	public void setPacketed(GMap<Player, Integer> packeted)
	{
		this.packeted = packeted;
	}
}
