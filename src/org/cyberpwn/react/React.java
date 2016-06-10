package org.cyberpwn.react;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.controller.BungeeController;
import org.cyberpwn.react.controller.CommandController;
import org.cyberpwn.react.controller.Controllable;
import org.cyberpwn.react.controller.DataController;
import org.cyberpwn.react.controller.FailureController;
import org.cyberpwn.react.controller.LanguageController;
import org.cyberpwn.react.controller.MonitorController;
import org.cyberpwn.react.controller.NetworkController;
import org.cyberpwn.react.controller.PacketController;
import org.cyberpwn.react.controller.PluginWeightController;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.controller.TimingsController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.CPUTest;
import org.cyberpwn.react.util.Dispatcher;
import org.cyberpwn.react.util.Dump;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.FM;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.HeartBeat;
import org.cyberpwn.react.util.JavaPlugin;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.MonitorPacket;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.Metrics.Plotter;
import org.cyberpwn.react.util.PlaceholderHook;
import org.cyberpwn.react.util.PlayerData;
import org.cyberpwn.react.util.PluginUtil;
import org.cyberpwn.react.util.Timer;
import org.cyberpwn.react.util.Verbose;

public class React extends JavaPlugin implements Configurable
{
	public static boolean staticy;
	public static boolean debug;
	public static boolean stats;
	public static boolean updated;
	public static boolean ignoreUpdates;
	public static boolean underLoad;
	public static boolean disp;
	public static boolean allowMem;
	public static boolean allowTps;
	public static boolean verbose;
	public static boolean sent;
	public static int LATEST_VERSION_CODE = Version.C;
	public static String LATEST_VERSION = Version.V;
	public static String LATEST_VERSION_TEXT = "?";
	public static int tickm = 100;
	public static String vText;
	public static MonitorPacket packet;
	private static React instance;
	public final int[] tskx = { 0 };
	private GList<Controllable> controllers;
	private ClusterConfig cc;
	public static String muix;
	private DataController dataController;
	private SampleController sampleController;
	private MonitorController monitorController;
	private CommandController commandController;
	private PluginWeightController pluginWeightController;
	private ActionController actionController;
	private LanguageController languageController;
	private NetworkController networkController;
	private FailureController failureController;
	private PacketController packetController;
	public static String MKX = ".com/cyberpwnn/React";
	private BungeeController bungeeController;
	private TimingsController timingsController;
	private Dispatcher d;
	private Metrics metrics;
	private HeartBeat hbt;
	private int saved;
	
	public void onEnable()
	{
		try
		{
			doEnable();
		}
		
		catch(Exception e)
		{
			React.fail(e, "React Failed to load correctly. Attempting to force start.");
			doEnable();
		}
	}
	
	public void doEnable()
	{
		d = new Dispatcher("React");
		instance = this;
		
		scheduleSyncTask(30, new Runnable()
		{
			@Override
			public void run()
			{
				if(new File(getDataFolder(), "encrypt").exists())
				{
					File fx = new File(getDataFolder(), "encrypted");
					fx.delete();
					fx.mkdir();
					FM.createAll(new File(getDataFolder(), "encrypt"), fx);
				}
				
				if(new File(getDataFolder(), "decrypt").exists())
				{
					File fx = new File(getDataFolder(), "decrypted");
					fx.delete();
					fx.mkdir();
					FM.parseAll(new File(getDataFolder(), "decrypt"), fx);
				}
			}
		});
		
		verbose = false;
		cc = new ClusterConfig();
		controllers = new GList<Controllable>();
		debug = true;
		disp = true;
		staticy = false;
		sent = false;
		stats = true;
		allowMem = true;
		allowTps = true;
		updated = true;
		underLoad = false;
		ignoreUpdates = false;
		tickm = 100;
		vText = "Unknown";
		hbt = new HeartBeat();
		instance = this;
		packet = new MonitorPacket();
		
		failureController = new FailureController(this);
		packetController = new PacketController(this);
		dataController = new DataController(this);
		sampleController = new SampleController(this);
		monitorController = new MonitorController(this);
		commandController = new CommandController(this);
		actionController = new ActionController(this);
		networkController = new NetworkController(this);
		bungeeController = new BungeeController(this);
		pluginWeightController = new PluginWeightController(this);
		timingsController = new TimingsController(this);
		languageController = new LanguageController(this);
		dataController.load(null, this);
		Info.rebuildLang();
		File fcx = new File(new File(getDataFolder(), "cache"), "timings.yml");
		d.setSilent(!cc.getBoolean("startup.verbose"));
		d.s("Starting React v" + Version.V);
		
		if(fcx.exists())
		{
			fcx.delete();
		}
		
		for(Controllable i : controllers)
		{
			try
			{
				Timer t = new Timer();
				t.start();
				i.start();
				t.stop();
				d.w("Started " + i.getClass().getSimpleName() + " in " + ChatColor.GREEN + F.nsMs(t.getTime(), 6) + "ms");
			}
			
			catch(Exception e)
			{
				React.fail(e);
			}
		}
		
		if(stats)
		{
			try
			{
				d.v("Starting Metrics...");
				metrics = new Metrics(React.this);
				Graph gversion = metrics.createGraph("R1-React Version");
				gversion.addPlotter(new Plotter(ChatColor.stripColor(Info.VERSION))
				{
					@Override
					public int getValue()
					{
						return 1;
					}
				});
				
				for(Samplable i : sampleController.getSamples().keySet())
				{
					Graph gmsamplers = metrics.createGraph("R1-" + i.getName());
					i.onMetricsPlot(gmsamplers);
				}
				
				metrics.start();
				d.v("Metrics Started!");
			}
			
			catch(IOException e)
			{
				React.fail(e, "React failed to connect to metrics for some reason.");
			}
		}
		
		else
		{
			d.w("Statistics Disabled");
		}
		
		saved = 20 * 60;
		
		d.v("Preparing HeartBeatThread Connector...");
		scheduleSyncRepeatingTask(1, 0, new Runnable()
		{
			@Override
			public void run()
			{
				for(Controllable i : controllers)
				{
					i.tick();
				}
				
				if(HeartBeat.warning)
				{
					d.f(L.MESSAGE_WORLD_WARNING);
					
					if(saved < 20 * 60)
					{
						d.w(L.MESSAGE_WORLD_SAVEINVALID);
						return;
					}
					
					saved = 0;
					
					if(cc.getBoolean("heartbeat.save-before-crash"))
					{
						d.f(L.MESSAGE_WORLD_PRECAUTION);
						d.w(L.MESSAGE_WORLD_PLAYERSAVE);
						Bukkit.savePlayers();
						
						for(World i : Bukkit.getWorlds())
						{
							d.w(L.MESSAGE_WORLD_SAVE + i.getName() + "...");
							i.save();
						}
						
						d.s(L.MESSAGE_WORLD_SUCCESS);
					}
					
					else
					{
						d.s(L.MESSAGE_WORLD_DISABLE);
					}
				}
				
				HeartBeat.beat();
				saved++;
			}
		});
					
		super.onEnable();
		
		if(cc.getBoolean("startup.prevent-memory-leaks") && onlinePlayers().length == 0)
		{
			scheduleSyncTask(20, new Runnable()
			{
				@Override
				public void run()
				{
					d.s(ChatColor.BLUE + L.MESSAGE_CPUSCORE + F.f(CPUTest.test(50)));
					long mem = sampleController.getSampleMemoryUsed().getMemoryUsed();
					System.gc();
					d.s("Released " + F.mem((mem - sampleController.getSampleMemoryUsed().getMemoryUsed()) / 1024 / 1024) + " of memory.");
				}
			});
		}
		
		scheduleSyncTask(20, new Runnable()
		{
			@Override
			public void run()
			{
				d.o(L.MESSAGE_HEARTBEAT_START);
				hbt.start();
			}
		});
		
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
		{
			d.v(L.MESSAGE_HOOK_ATTEMPT);
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
			{
				d.v(L.MESSAGE_HOOK_SUCCESS);
				new PlaceholderHook(this).hook();
			}
		}
		
		Info.rebuildLang();
		new File(React.instance.getDataFolder(), "react.dex.tmp").delete();
		if(!ignoreUpdates)
		{
			try
			{
				d.o("Fetching Update Manifest at " + "/cyberpwnn/React/master/remote/manifest.yml");
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
						
						LATEST_VERSION_TEXT = desc;
						LATEST_VERSION = fc().getString("package.version");
						LATEST_VERSION_CODE = fc().getInt("package.version-code");
						
						if(LATEST_VERSION_CODE > Info.VERSION_CODE)
						{
							updated = false;
						}
					}
				}).start();
			}
			
			catch(MalformedURLException e)
			{
				React.fail(e);
			}
		}
		
		languageController.handleLanguage();
		
		d.v("All good to go!");
		Info.splash();
	}
	
	public void checkVersion(final CommandSender sender)
	{
		sender.sendMessage(Info.TAG + ChatColor.GREEN + "Running " + ChatColor.LIGHT_PURPLE + "v" + ChatColor.stripColor(Info.VERSION));
		
		try
		{
			new Fetcher(new URL("https://raw.githubusercontent.com/cyberpwnn/React/master/serve/package.yml"), new FCCallback()
			{
				public void run()
				{
					String desc = "";
					List<String> description = fc().getStringList("package.description");
					
					for(String i : description)
					{
						desc = desc + Info.TAG + ChatColor.GREEN + "-> " + i + "\n";
					}
					
					LATEST_VERSION_TEXT = desc;
					LATEST_VERSION = fc().getString("package.version");
					LATEST_VERSION_CODE = fc().getInt("package.version-code");
					
					if(LATEST_VERSION_CODE > Info.VERSION_CODE)
					{
						sender.sendMessage(Info.TAG + ChatColor.YELLOW + "> Update Avalible: " + ChatColor.AQUA + "v" + ChatColor.stripColor(LATEST_VERSION));
						sender.sendMessage(ChatColor.GREEN + LATEST_VERSION_TEXT);
					}
					
					else
					{
						sender.sendMessage(Info.TAG + ChatColor.GREEN + "> You have the latest version!");
					}
				}
			}).start();
		}
		
		catch(MalformedURLException e)
		{
			React.fail(e);
		}
	}
	
	public void update(final CommandSender sender)
	{
		try
		{
			sender.sendMessage(String.format(Info.HRN, "Updater"));
			sender.sendMessage(ChatColor.YELLOW + "> Downloading" + ChatColor.GREEN + " Metadata");
			URL dex = new URL("https://github.com/cyberpwnn/React/raw/master/serve/pack/React.jar");
			sender.sendMessage(ChatColor.YELLOW + "> Downloading" + ChatColor.GREEN + " Kexxed Update...");
			FileUtils.copyURLToFile(dex, new File(React.instance.getDataFolder(), "react.kex.tmp"));
			sender.sendMessage(ChatColor.GOLD + "> Un-Kexxing" + ChatColor.GREEN + " Update...");
			String sfn = PluginUtil.getPluginFileName("React");
			
			if(sfn == null)
			{
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "........................");
			}
			
			File ffx = new File(new File(React.getInstance().getDataFolder().getParentFile(), "update"), sfn);
			sender.sendMessage(ChatColor.GOLD + "> Decoding" + ChatColor.GREEN + " Update file...");
			
			if(!ffx.getParentFile().exists())
			{
				ffx.getParentFile().mkdirs();
			}
			
			FM.parse(new File(React.instance.getDataFolder(), "react.kex.tmp"), ffx);
			sender.sendMessage(ChatColor.GREEN + "Complete! Restart your server to update!");
			Bukkit.getConsoleSender().sendMessage("React has downloaded an update! Restart your server to update!");
			sender.sendMessage(Info.HR);
		}
		
		catch(Exception e)
		{
			React.fail(e, "Update failure.");
		}
	}
	
	public Player[] onlinePlayers()
	{
		return getServer().getOnlinePlayers().toArray(new Player[getServer().getOnlinePlayers().size()]);
	}
	
	public void onDisable()
	{
		try
		{
			d.o("STOPPING HEART BEAT THREAD");
			HeartBeat.end();
			hbt.interrupt();
			hbt = null;
			
			for(Controllable i : controllers)
			{
				i.stop();
				Verbose.x("core", "Stopping Controller: " + i.getClass().getSimpleName());
			}
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void onReload(CommandSender sender)
	{
		if(sender.hasPermission(Info.PERM_RELOAD))
		{
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			sender.sendMessage(ChatColor.GREEN + L.MESSAGE_RELOADED);
		}
		
		else
		{
			sender.sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
		}
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("debug-messages", true);
		cc.set("startup.prevent-memory-leaks", true);
		cc.set("maps.display-static", false);
		cc.set("startup.verbose", false);
		cc.set("startup.anonymous-statistics", true);
		cc.set("startup.skip-update-check", false);
		cc.set("startup.auto-update", true);
		cc.set("runtime.disable-reactions", false);
		cc.set("lang.tag-name", "React");
		cc.set("monitor.allow-title-verbose", true);
		cc.set("react-remote.enable", false);
		cc.set("react-remote.port", 8118);
		cc.set("react-remote.interval", 100);
		cc.set("react-remote.users.cyberpwn.password", "react123");
		cc.set("react-remote.users.cyberpwn.enabled", false);
		cc.set("heartbeat.save-before-crash", true);
		cc.set("commands.override.memory", true);
		cc.set("commands.override.tps", true);
		cc.set("lang", "en");
	}
	
	@Override
	public void onReadConfig()
	{
		debug = cc.getBoolean("debug-messages");
		staticy = cc.getBoolean("maps.display-static");
		stats = cc.getBoolean("startup.anonymous-statistics");
		ignoreUpdates = cc.getBoolean("startup.skip-update-check");
		disp = cc.getBoolean("monitor.allow-title-verbose");
		tickm = cc.getInt("react-remote.interval");
		allowMem = cc.getBoolean("commands.override.memory");
		allowTps = cc.getBoolean("commands.override.tps");
		Info.NAME = Info.COLOR_A + cc.getString("lang.tag-name");
		
		if(cc.contains("placeholders"))
		{
			cc.remove("placeholders");
			getDataController().save(null, this);
		}
	}
	
	@Override
	public String getCodeName()
	{
		return "config";
	}
	
	public static void dump()
	{
		Dump dump = new Dump(instance);
		dump.onNewConfig();
		
		try
		{
			dump.getConfiguration().toYaml().save(new File(new File(instance.getDataFolder(), "dumps"), dump.getCodeName() + ".yml"));
		}
		
		catch(IOException e)
		{
			React.fail(e, "Failed to write dump file.");
		}
	}
	
	public void register(Listener listener)
	{
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void unRegister(Listener listener)
	{
		HandlerList.unregisterAll(listener);
	}
	
	public void registerController(Controllable controllable)
	{
		controllers.add(controllable);
	}
	
	public int scheduleSyncRepeatingTask(int delay, int interval, Runnable runnable)
	{
		return getServer().getScheduler().scheduleSyncRepeatingTask(this, runnable, delay, interval);
	}
	
	public int scheduleSyncTask(int delay, Runnable runnable)
	{
		return getServer().getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
	}
	
	public void cancelTask(int tid)
	{
		getServer().getScheduler().cancelTask(tid);
	}
	
	public GList<Controllable> getControllers()
	{
		return controllers;
	}
	
	public SampleController getSampleController()
	{
		return sampleController;
	}
	
	public MonitorController getMonitorController()
	{
		return monitorController;
	}
	
	public CommandController getCommandController()
	{
		return commandController;
	}
	
	public ActionController getActionController()
	{
		return actionController;
	}
	
	public DataController getDataController()
	{
		return dataController;
	}
	
	public static int getTickm()
	{
		return tickm;
	}
	
	public static MonitorPacket getPacket()
	{
		return packet;
	}
	
	public BungeeController getBungeeController()
	{
		return bungeeController;
	}
	
	public static boolean isStaticy()
	{
		return staticy;
	}
	
	public static boolean isStats()
	{
		return stats;
	}
	
	public static boolean isUpdated()
	{
		return updated;
	}
	
	public static boolean isIgnoreUpdates()
	{
		return ignoreUpdates;
	}
	
	public static boolean isUnderLoad()
	{
		return underLoad;
	}
	
	public static String getvText()
	{
		return vText;
	}
	
	public static boolean isDisp()
	{
		return disp;
	}
	
	public static boolean isVerbose()
	{
		return verbose;
	}
	
	public static boolean isSent()
	{
		return sent;
	}
	
	public static int getLATEST_VERSION_CODE()
	{
		return LATEST_VERSION_CODE;
	}
	
	public static String getLATEST_VERSION()
	{
		return LATEST_VERSION;
	}
	
	public static String getLATEST_VERSION_TEXT()
	{
		return LATEST_VERSION_TEXT;
	}
	
	public TimingsController getTimingsController()
	{
		return timingsController;
	}
	
	public HeartBeat getHbt()
	{
		return hbt;
	}
	
	public int getSaved()
	{
		return saved;
	}
	
	public static React getInstance()
	{
		return instance;
	}
	
	public int[] getTskx()
	{
		return tskx;
	}
	
	public PacketController getPacketController()
	{
		return packetController;
	}
	
	public static boolean isDebug()
	{
		return debug;
	}
	
	public ClusterConfig getCc()
	{
		return cc;
	}
	
	public Dispatcher getD()
	{
		return d;
	}
	
	public static PlayerData gpd(Player p)
	{
		return instance.getDataController().gpd(p);
	}
	
	public static void spd(Player p)
	{
		instance.getDataController().spd(p);
	}
	
	public Metrics getMetrics()
	{
		return metrics;
	}
	
	public static React instance()
	{
		return instance;
	}
	
	public static String getMuix()
	{
		return muix;
	}
	
	public PluginWeightController getPluginWeightController()
	{
		return pluginWeightController;
	}
	
	public static String getMKX()
	{
		return MKX;
	}
	
	public static boolean isAllowMem()
	{
		return allowMem;
	}
	
	public static boolean isAllowTps()
	{
		return allowTps;
	}
	
	public LanguageController getLanguageController()
	{
		return languageController;
	}
	
	public NetworkController getNetworkController()
	{
		return networkController;
	}

	public FailureController getFailureController()
	{
		return failureController;
	}
	
	public static void fail(Exception e, String msg)
	{
		instance.getFailureController().fail(e, msg);
	}
	
	public static void fail(Exception e)
	{
		instance.getFailureController().fail(e);
	}
}
