package org.cyberpwn.react;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.controller.CommandController;
import org.cyberpwn.react.controller.ConfigurationController;
import org.cyberpwn.react.controller.Controllable;
import org.cyberpwn.react.controller.DataController;
import org.cyberpwn.react.controller.EntityStackController;
import org.cyberpwn.react.controller.FailureController;
import org.cyberpwn.react.controller.LanguageController;
import org.cyberpwn.react.controller.MonitorController;
import org.cyberpwn.react.controller.NetworkController;
import org.cyberpwn.react.controller.PacketController;
import org.cyberpwn.react.controller.PlayerController;
import org.cyberpwn.react.controller.PluginWeightController;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.controller.TimingsController;
import org.cyberpwn.react.controller.UpdateController;
import org.cyberpwn.react.controller.WorldController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.CPUTest;
import org.cyberpwn.react.util.Dispatcher;
import org.cyberpwn.react.util.Dump;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.FM;
import org.cyberpwn.react.util.FU;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GTime;
import org.cyberpwn.react.util.JavaPlugin;
import org.cyberpwn.react.util.M;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.Metrics.Plotter;
import org.cyberpwn.react.util.MonitorPacket;
import org.cyberpwn.react.util.PlaceholderHook;
import org.cyberpwn.react.util.Timer;
import org.cyberpwn.react.util.Verbose;

public class React extends JavaPlugin implements Configurable
{
	private static boolean staticy;
	private static boolean debug;
	private static boolean stats;
	private static boolean updated;
	private static boolean ignoreUpdates;
	private static boolean underLoad;
	private static boolean disp;
	private static boolean allowMem;
	private static boolean allowTps;
	private static boolean verbose;
	private static boolean sent;
	private static boolean mef = false;
	private static int LATEST_VERSION_CODE = Version.C;
	private static String LATEST_VERSION = Version.V;
	private static String LATEST_VERSION_TEXT = "?";
	private static int tickm = 100;
	private static String vText;
	private static boolean nf;
	private static MonitorPacket packet;
	private static React instance;
	private final int[] tskx = { 0 };
	private GList<Controllable> controllers;
	private ClusterConfig cc;
	private static String muix;
	private ConfigurationController configurationController;
	private DataController dataController;
	private PlayerController playerController;
	private SampleController sampleController;
	private boolean justUpdated;
	private MonitorController monitorController;
	private CommandController commandController;
	private PluginWeightController pluginWeightController;
	private ActionController actionController;
	private LanguageController languageController;
	private NetworkController networkController;
	private UpdateController updateController;
	private EntityStackController entityStackController;
	private WorldController worldController;
	private FailureController failureController;
	public final static String nonce = "%%__NONCE__%%";
	private PacketController packetController;
	private static String MKX = ".com/cyberpwnn/React";
	public static String hashed = "https://raw.githubusercontent.com/cyberpwnn/React/master/serve/war/hash.yml";
	private TimingsController timingsController;
	private Dispatcher d;
	private Metrics metrics;
	private int saved;
	private long start;
	
	public void onEnable()
	{
		start = M.ms();
		justUpdated = false;
		
		try
		{
			doEnable();
		}
		
		catch(Exception e)
		{
			React.fail(e, L.MESSAGE_LOAD_FAIL);
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
		
		setVerbose(false);
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
		instance = this;
		packet = new MonitorPacket();
		
		configurationController = new ConfigurationController(this);
		failureController = new FailureController(this);
		packetController = new PacketController(this);
		dataController = new DataController(this);
		sampleController = new SampleController(this);
		playerController = new PlayerController(this);
		monitorController = new MonitorController(this);
		commandController = new CommandController(this);
		actionController = new ActionController(this);
		networkController = new NetworkController(this);
		pluginWeightController = new PluginWeightController(this);
		timingsController = new TimingsController(this);
		languageController = new LanguageController(this);
		worldController = new WorldController(this);
		updateController = new UpdateController(this);
		entityStackController = new EntityStackController(this);
		dataController.load(null, this);
		dataController.load(null, updateController);
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
				React.fail(e, L.MESSAGE_METRIC_FAIL);
			}
		}
		
		else
		{
			d.w("Statistics Disabled");
		}
		
		saved = 20 * 60;
		
		scheduleSyncRepeatingTask(1, 0, new Runnable()
		{
			@Override
			public void run()
			{
				for(Controllable i : controllers)
				{
					i.tick();
				}
			}
		});
		
		if(cc.getBoolean("startup.prevent-memory-leaks") && onlinePlayers().length == 0)
		{
			scheduleSyncTask(20, new Runnable()
			{
				@Override
				public void run()
				{
					d.s(ChatColor.BLUE + L.MESSAGE_CPUSCORE + F.f(CPUTest.singleThreaded(50)));
					long mem = sampleController.getSampleMemoryUsed().getMemoryUsed();
					System.gc();
					d.s("Released " + F.mem((mem - sampleController.getSampleMemoryUsed().getMemoryUsed()) / 1024 / 1024) + " of memory.");
				}
			});
		}
		
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
		{
			d.v(L.MESSAGE_HOOK_ATTEMPT);
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
			{
				d.v(L.MESSAGE_HOOK_SUCCESS);
				PlaceholderHook h = new PlaceholderHook(this);
				dataController.load(null, h);
				h.hook();
			}
		}
		
		Info.rebuildLang();
		languageController.handleLanguage();
		
		d.v("All good to go!");
		Info.splash();
	}
	
	public Player[] onlinePlayers()
	{
		return getServer().getOnlinePlayers().toArray(new Player[getServer().getOnlinePlayers().size()]);
	}
	
	public void onDisable()
	{
		for(Controllable i : controllers)
		{
			try
			{
				i.stop();
				Verbose.x("core", "Stopping Controller: " + i.getClass().getSimpleName());
				
			}
			
			catch(Exception e)
			{
				
			}
		}
	}
	
	public void onReload(CommandSender sender)
	{
		if(sender.hasPermission(Info.PERM_RELOAD))
		{
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("React"));
			sender.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_RELOADED);
		}
		
		else
		{
			sender.sendMessage(Info.TAG + L.MESSAGE_INSUFFICIENT_PERMISSION);
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
		cc.set("startup.skip-update-check", true);
		cc.set("startup.auto-update", false);
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
		cc.set("messages.notify-instability", true);
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
		nf = cc.getBoolean("messages.notify-instability");
		Info.NAME = Info.COLOR_A + cc.getString("lang.tag-name");
		
		if(cc.contains("placeholders"))
		{
			cc.remove("placeholders");
			getDataController().save(null, this);
		}
	}
	
	public void exul(File file, String ip)
	{
		try
		{
			export("/eula.txt", file);
			
			BufferedReader buf = new BufferedReader(new FileReader(file));
			
			GList<String> linx = new GList<String>();
			String next = null;
			
			while((next = buf.readLine()) != null)
			{
				next = next + "\n";
				
				if(next.contains("<$>"))
				{
					linx.add("IMEID: " + NetworkController.imeid + "\n");
					linx.add("NONCE: " + React.nonce + "\n");
				}
				
				else
				{
					linx.add(next);
				}
			}
			
			buf.close();
			file.delete();
			file.createNewFile();
			PrintWriter pw = new PrintWriter(file);
			
			for(String i : linx)
			{
				pw.write(i);
			}
			
			pw.close();
		}
		
		catch(Exception e)
		{
			
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
			React.fail(e, L.MESSAGE_DUMP_FAIL);
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
	
	public PlayerController getPlayerController()
	{
		return playerController;
	}
	
	public GTime getUptime()
	{
		return new GTime(M.ms() - start);
	}
	
	public static boolean isMef()
	{
		return mef;
	}
	
	public static void setMef(boolean mef)
	{
		React.mef = mef;
	}
	
	public static void setVerbose(boolean verbose)
	{
		React.verbose = verbose;
	}
	
	public static String getNonce()
	{
		return nonce;
	}
	
	public static String getHashed()
	{
		return hashed;
	}
	
	public long getStart()
	{
		return start;
	}
	
	public WorldController getWorldController()
	{
		return worldController;
	}
	
	public static boolean isNf()
	{
		return nf;
	}
	
	public static void setStaticy(boolean staticy)
	{
		React.staticy = staticy;
	}
	
	public static void setDebug(boolean debug)
	{
		React.debug = debug;
	}
	
	public static void setStats(boolean stats)
	{
		React.stats = stats;
	}
	
	public static void setUpdated(boolean updated)
	{
		React.updated = updated;
	}
	
	public static void setIgnoreUpdates(boolean ignoreUpdates)
	{
		React.ignoreUpdates = ignoreUpdates;
	}
	
	public static void setUnderLoad(boolean underLoad)
	{
		React.underLoad = underLoad;
	}
	
	public static void setDisp(boolean disp)
	{
		React.disp = disp;
	}
	
	public static void setAllowMem(boolean allowMem)
	{
		React.allowMem = allowMem;
	}
	
	public static void setAllowTps(boolean allowTps)
	{
		React.allowTps = allowTps;
	}
	
	public static void setSent(boolean sent)
	{
		React.sent = sent;
	}
	
	public static void setLATEST_VERSION_CODE(int lATEST_VERSION_CODE)
	{
		LATEST_VERSION_CODE = lATEST_VERSION_CODE;
	}
	
	public static void setLATEST_VERSION(String lATEST_VERSION)
	{
		LATEST_VERSION = lATEST_VERSION;
	}
	
	public static void setLATEST_VERSION_TEXT(String lATEST_VERSION_TEXT)
	{
		LATEST_VERSION_TEXT = lATEST_VERSION_TEXT;
	}
	
	public static void setTickm(int tickm)
	{
		React.tickm = tickm;
	}
	
	public static void setvText(String vText)
	{
		React.vText = vText;
	}
	
	public static void setNf(boolean nf)
	{
		React.nf = nf;
	}
	
	public static void setPacket(MonitorPacket packet)
	{
		React.packet = packet;
	}
	
	public static void setInstance(React instance)
	{
		React.instance = instance;
	}
	
	public void setControllers(GList<Controllable> controllers)
	{
		this.controllers = controllers;
	}
	
	public void setCc(ClusterConfig cc)
	{
		this.cc = cc;
	}
	
	public static void setMuix(String muix)
	{
		React.muix = muix;
	}
	
	public void setConfigurationController(ConfigurationController configurationController)
	{
		this.configurationController = configurationController;
	}
	
	public void setDataController(DataController dataController)
	{
		this.dataController = dataController;
	}
	
	public void setPlayerController(PlayerController playerController)
	{
		this.playerController = playerController;
	}
	
	public void setSampleController(SampleController sampleController)
	{
		this.sampleController = sampleController;
	}
	
	public void setMonitorController(MonitorController monitorController)
	{
		this.monitorController = monitorController;
	}
	
	public void setCommandController(CommandController commandController)
	{
		this.commandController = commandController;
	}
	
	public void setPluginWeightController(PluginWeightController pluginWeightController)
	{
		this.pluginWeightController = pluginWeightController;
	}
	
	public void setActionController(ActionController actionController)
	{
		this.actionController = actionController;
	}
	
	public void setLanguageController(LanguageController languageController)
	{
		this.languageController = languageController;
	}
	
	public void setNetworkController(NetworkController networkController)
	{
		this.networkController = networkController;
	}
	
	public void setWorldController(WorldController worldController)
	{
		this.worldController = worldController;
	}
	
	public void setFailureController(FailureController failureController)
	{
		this.failureController = failureController;
	}
	
	public static void setMKX(String mKX)
	{
		MKX = mKX;
	}
	
	public static void setHashed(String hashed)
	{
		React.hashed = hashed;
	}
	
	public void setTimingsController(TimingsController timingsController)
	{
		this.timingsController = timingsController;
	}
	
	public void setD(Dispatcher d)
	{
		this.d = d;
	}
	
	public void setMetrics(Metrics metrics)
	{
		this.metrics = metrics;
	}
	
	public void setSaved(int saved)
	{
		this.saved = saved;
	}
	
	public void setStart(long start)
	{
		this.start = start;
	}
	
	public ConfigurationController getConfigurationController()
	{
		return configurationController;
	}
	
	public void export(String resourceName, File file) throws Exception
	{
		URL inputUrl = getClass().getResource(resourceName);
		File dest = new File(file.getPath());
		FU.copyURLToFile(inputUrl, dest);
	}
	
	public boolean isJustUpdated()
	{
		return justUpdated;
	}
	
	public UpdateController getUpdateController()
	{
		return updateController;
	}
	
	public EntityStackController getEntityStackController()
	{
		return entityStackController;
	}
}
