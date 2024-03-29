package org.cyberpwn.react;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.cyberpwn.react.action.Action;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.controller.ChannelListenController;
import org.cyberpwn.react.controller.CommandController;
import org.cyberpwn.react.controller.ConfigurationController;
import org.cyberpwn.react.controller.ConsoleController;
import org.cyberpwn.react.controller.Controllable;
import org.cyberpwn.react.controller.DataController;
import org.cyberpwn.react.controller.LagMapController;
import org.cyberpwn.react.controller.LanguageController;
import org.cyberpwn.react.controller.MonitorController;
import org.cyberpwn.react.controller.NetworkController;
import org.cyberpwn.react.controller.PlayerController;
import org.cyberpwn.react.controller.PluginWeightController;
import org.cyberpwn.react.controller.RegionController;
import org.cyberpwn.react.controller.RemoteController;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.controller.ScoreboardController;
import org.cyberpwn.react.controller.TaskManager;
import org.cyberpwn.react.controller.TileController;
import org.cyberpwn.react.controller.TimingsController;
import org.cyberpwn.react.controller.WorldController;
import org.cyberpwn.react.file.FileHack;
import org.cyberpwn.react.file.ICopy;
import org.cyberpwn.react.file.IDelete;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.nms.Title;
import org.cyberpwn.react.queue.ParallelPoolManager;
import org.cyberpwn.react.queue.TICK;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.Base64;
import org.cyberpwn.react.util.CFX;
import org.cyberpwn.react.util.CPUTest;
import org.cyberpwn.react.util.Dispatcher;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.FM;
import org.cyberpwn.react.util.GFile;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.HijackedConsole;
import org.cyberpwn.react.util.JavaPlugin;
import org.cyberpwn.react.util.M;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.MonitorPacket;
import org.cyberpwn.react.util.PhantomSpinner;
import org.cyberpwn.react.util.PlaceholderHook;
import org.cyberpwn.react.util.Platform;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.TaskLater;
import org.cyberpwn.react.util.Timer;
import org.cyberpwn.react.util.Verbose;

public class React extends JavaPlugin implements Configurable
{
	public static boolean STOPPING = false;
	private ParallelPoolManager poolManager;
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
	public static React instance;
	private final int[] tskx = {0};
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
	private WorldController worldController;
	private ChannelListenController channelListenController;
	private TimingsController timingsController;
	private ScoreboardController scoreboardController;
	private LagMapController lagMapController;
	private ConsoleController consoleController;
	private TileController tileController;
	private RegionController regionController;
	private TaskManager taskManager;
	public static String nonce = "%%__NONCE__%%";
	private static String MKX = ".com/cyberpwnn/React";
	public static String hashed = "https://raw.githubusercontent.com/cyberpwnn/React/master/serve/war/hash.yml";
	private Dispatcher d;
	private boolean asr;
	public static boolean dreact = false;
	private Metrics metrics;
	private int saved;
	private long start;
	private PrintStream old;
	private int imh;
	public static GList<Runnable> runnables;
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	public static Average afn;

	@Override
	public void onEnable()
	{
		updateVersion();
		imh = 0;
		old = System.out;
		start = M.ms();
		justUpdated = false;
		super.startup();
		runnables = new GList<Runnable>();

		try
		{
			doEnable();
		}

		catch(Exception e)
		{
			doEnable();
		}
	}

	/**
	 * Purge all chunks in the given world
	 *
	 * @param w
	 *            the given world
	 * @param save
	 *            should these chunks be saved before unloading?
	 * @param force
	 *            should we force unload? Setting to true will ignore if players are
	 *            in it.
	 */
	public void purgeChunks(World w, boolean save, boolean force)
	{
		purgeChunks(new GList<Chunk>(w.getLoadedChunks()), save, force);
	}

	/**
	 * Purge all chunks in the given list
	 *
	 * @param w
	 *            the given list of chunks
	 * @param save
	 *            should these chunks be saved before unloading?
	 * @param force
	 *            should we force unload? Setting to true will ignore if players are
	 *            in it.
	 */
	public void purgeChunks(List<Chunk> chunks, boolean save, boolean force)
	{
		for(Chunk i : chunks)
		{
			purgeChunk(i, save, force);
		}
	}

	/**
	 * Purge the given chunk
	 *
	 * @param c
	 *            the given chunk
	 * @param save
	 *            should these chunks be saved before unloading?
	 * @param force
	 *            should we force unload? Setting to true will ignore if players are
	 *            in it.
	 */
	@SuppressWarnings("deprecation")
	public void purgeChunk(Chunk c, boolean save, boolean force)
	{
		c.unload(save, !force);
	}

	public void rsps()
	{
		System.setOut(old);
	}

	public void doEnable()
	{
		d = new Dispatcher("React");
		instance = this;
		afn = new Average(200);
		setVerbose(false);
		asr = false;
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
		languageController = new LanguageController(this);
		dataController = new DataController(this);
		sampleController = new SampleController(this);
		playerController = new PlayerController(this);
		monitorController = new MonitorController(this);
		scoreboardController = new ScoreboardController(this);
		commandController = new CommandController(this);
		actionController = new ActionController(this);
		networkController = new NetworkController(this);
		pluginWeightController = new PluginWeightController(this);
		timingsController = new TimingsController(this);
		channelListenController = new ChannelListenController(this);
		worldController = new WorldController(this);
		lagMapController = new LagMapController(this);
		consoleController = new ConsoleController(this);
		taskManager = new TaskManager(this);
		regionController = new RegionController(this);
		tileController = new TileController(this);
		dataController.load((String) null, configurationController);
		dataController.load((String) null, taskManager);
		dataController.load((String) null, consoleController);
		dataController.load((String) null, this);
		dataController.load((String) null, tileController);
		setupTicker();
		Info.rebuildLang();
		GFile fcx = new GFile(new GFile(getDataFolder(), "cache"), "timings.yml");
		d.setSilent(!cc.getBoolean("startup.verbose"));
		d.s(L.DEBUG_STARTING + Version.V);
		FileConfiguration fc = new YamlConfiguration();
		File fx = new GFile(new GFile(getDataFolder(), "cache"), "mcache");

		new BStats(this);

		new TaskLater(10)
		{
			@Override
			public void run()
			{
				new RemoteController();
			}
		};

		if(fx.exists() && nonce.equals("%%__NONCE__%%"))
		{
			try
			{
				fc.loadFromString(new String(Base64.decodeFromFile(fx.getPath())));
				String imd = fc.getString("imeid");

				if(!imd.equals(nonce))
				{
					nonce = imd;
				}
			}

			catch(Exception e)
			{

			}
		}

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
				d.w(L.DEBUG_START + i.getClass().getSimpleName() + L.DEBUG_IN + ChatColor.GREEN + F.nsMs(t.getTime(), 6) + L.DEBUG_MS);
			}

			catch(Exception e)
			{
				React.fail(e);
			}
		}

		new TaskLater(1)
		{
			@Override
			public void run()
			{
				if(new GFile(getDataFolder(), "encrypt").exists())
				{
					GFile fx = new GFile(getDataFolder(), "encrypted");
					fx.delete();
					fx.mkdir();
					FM.createAll(new GFile(getDataFolder(), "encrypt"), fx);
				}

				if(new GFile(getDataFolder(), "decrypt").exists())
				{
					GFile fx = new GFile(getDataFolder(), "decrypted");
					fx.delete();
					fx.mkdir();
					FM.parseAll(new GFile(getDataFolder(), "decrypt"), fx);
				}
			}
		};

		stats = false;

		new TaskLater(100)
		{
			@Override
			public void run()
			{
				File f = new File(getDataFolder(), "config.yml");

				if(f.length() == 0)
				{
					System.out.println("Failed to gen configs for react.");
					System.out.println("Calling tweak method...");
					getConfigurationController().rebuildConfigurations(Bukkit.getConsoleSender());
				}
			}
		};

		new Task(0)
		{
			@Override
			public void run()
			{
				for(Controllable i : controllers)
				{
					try
					{
						i.tick();
					}

					catch(Exception e)
					{

					}
				}

				imh++;

				if(imh > 2)
				{
					imh = 0;

					try
					{
						if(!asr)
						{
							new ASYNC()
							{
								@Override
								public void async()
								{
									Platform.PROC_CPU = Platform.CPU.getLiveProcessCPULoad();
								}
							};
						}
					}

					catch(Exception e)
					{

					}
				}

				int afm = Action.APT;
				Action.APT = 0;
				afn.put(afm);
			}
		};

		if(cc.getBoolean("startup.prevent-memory-leaks") && onlinePlayers().length == 0)
		{
			new TaskLater(1)
			{
				@Override
				public void run()
				{
					d.s(ChatColor.BLUE + L.MESSAGE_CPUSCORE + F.f(CPUTest.singleThreaded(50)));
					long mem = sampleController.getSampleMemoryUsed().getMemoryUsed();
					System.gc();
					d.s(L.DEBUG_RELEASED + F.mem((mem - sampleController.getSampleMemoryUsed().getMemoryUsed()) / 1024 / 1024) + L.DEBUG_OFMEMORY);
				}
			};
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
		{
			d.v(L.MESSAGE_HOOK_ATTEMPT);
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
			{
				try
				{
					PlaceholderHook h = new PlaceholderHook(this);
					dataController.load((String) null, h);
					h.hook();
					d.v(L.MESSAGE_HOOK_SUCCESS);
				}

				catch(Exception e)
				{

				}
			}
		}

		Info.rebuildLang();
		languageController.handleLanguage();
		setTag();

		d.v(L.DEBUG_FINISHED);

		if(cc.getBoolean("splash-screen"))
		{
			Info.splash();
		}

		else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "React " + Version.V + " Started!");
		}

		File f = new File(getDataFolder(), "r5x");

		if(!f.exists())
		{
			f.mkdirs();
		}
	}

	@Override
	public void onLoad()
	{
		readCurrentTick();
	}

	public static void saveMainConfig()
	{
		File f = new File(instance.getDataFolder(), "config.yml");

		try
		{
			instance.cc.toYaml().save(f);
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void reload()
	{
		Bukkit.getServer().getPluginManager().disablePlugin(instance);
		Bukkit.getServer().getPluginManager().enablePlugin(instance);
	}

	private void readCurrentTick()
	{
		long ms = System.currentTimeMillis();
		File prop = new File("server.properties");
		TICK.tick = (ms - prop.lastModified()) / 50;
		System.out.println("Setting Tick to " + TICK.tick);
	}

	private void setupTicker()
	{
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				TICK.tick++;

				poolManager.chk();
			}
		}, 0, 0);

		poolManager = new ParallelPoolManager(cc.getInt("startup.multicore-threads"));
		poolManager.start();
	}

	@Override
	public void onDisable()
	{
		STOPPING = true;

		for(Controllable i : controllers)
		{
			try
			{
				i.stop();
				Verbose.x("core", L.DEBUG_CONTROLLER_STOPPING + i.getClass().getSimpleName());
			}

			catch(Exception e)
			{

			}
		}

		HijackedConsole.hijacked = false;
	}

	public void i(String s)
	{
		d.i(s);
	}

	public void s(String s)
	{
		d.s(s);
	}

	public void f(String s)
	{
		d.f(s);
	}

	public void w(String s)
	{
		d.w(s);
	}

	public void o(String s)
	{
		d.o(s);
	}

	public void registerController(Controllable controllable)
	{
		controllers.add(controllable);
	}

	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}

	public static boolean canMulticore()
	{
		return React.instance.getConfig().getBoolean("startup.multicore");
	}

	public static boolean shouldMulticore()
	{
		if(Runtime.getRuntime().freeMemory() / 1024 / 1024 < 1024)
		{
			return false;
		}

		if(Runtime.getRuntime().maxMemory() / 1024 / 1024 < corec() * 768)
		{
			return false;
		}

		return true;
	}

	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("splash-screen", true, L.CONFIG_REACT_SPLASH);
		cc.set("debug-messages", true);
		cc.set("startup.prevent-memory-leaks", true, L.CONFIG_REACT_DEBUGMESSAGES);
		cc.set("startup.multicore", shouldMulticore(), L.CONFIG_REACT_MULTICORE);
		cc.set("startup.multicore-threads", 1, L.CONFIG_REACT_MULTITHREAD);
		cc.set("startup.system-profiling", true, L.CONFIG_REACT_STARTUPPROF);
		cc.set("maps.display-static", false);
		cc.set("startup.verbose", false, L.CONFIG_REACT_VERBOSE);
		cc.set("startup.anonymous-statistics", true, L.CONFIG_REACT_STATS);
		cc.set("runtime.disable-reactions", false, L.CONFIG_REACT_DISABLEREACTIONS);
		cc.set("display.tag", "&b[&8React&b]:", L.CONFIG_REACT_TAG);
		cc.set("display.no-permission", "&cInsufficient Permission", L.CONFIG_REACT_PERMDENYMSG);
		cc.set("monitor.allow-title-verbose", true, L.CONFIG_REACT_ALLOWTITLEVERBOSE);
		cc.set("monitor.scoreboard-interval", 10, L.CONFIG_REACT_SCOREBOARD);
		cc.set("monitor.title-bolding", false, L.CONFIG_REACT_TITLEBOLDING);
		cc.set("monitor.broadcast-monitors", true, L.CONFIG_REACT_BROADCAST);
		cc.set("monitor.shift-accuracy", true, L.CONFIG_REACT_ALLOWSHIFTACCURACY);
		cc.set("monitor.ticking.dynamic", true, L.CONFIG_REACT_TITLETICK_DYNAMIC);
		cc.set("monitor.ticking.base", 1, L.CONFIG_REACT_TITLETICK_BASE);
		cc.set("react-remote.enable", false, L.CONFIG_REACT_REMOTE_ENABLE);
		cc.set("react-remote.port", 8118, L.CONFIG_REACT_REMOTE_PORT);
		cc.set("react-remote.interval", 100, L.CONFIG_REACT_INTERVAL);
		cc.set("react-remote.auto-restart", false, L.CONFIG_REACT_RESTART);
		cc.set("heartbeat.save-before-crash", true);
		cc.set("commands.override.memory", true, L.CONFIG_REACT_OVERRIDES_MEMORY);
		cc.set("commands.override.tps", true, L.CONFIG_REACT_OVERRIDES_TPS);
		cc.set("messages.notify-instability", true, L.ACTION_INSTABILITYCAUSE);
		cc.set("write-world-configs", true, "Should react write configs.");
		cc.set("lang", "en", "Language code.");
	}

	public boolean isSWorld()
	{
		return cc.getBoolean("write-world-configs");
	}

	public void setTag()
	{
		Info.MSG_PERM = F.color(cc.getString("display.no-permission"));
		Info.TAG = F.color(cc.getString("display.tag")) + " " + ChatColor.GRAY;
		L.MESSAGE_INSUFFICIENT_PERMISSION = Info.MSG_PERM;
	}

	public void updateVersion()
	{
		String version = getDescription().getVersion();
		Version.V = version;
		Version.C = Version.toB(version);
	}

	@Override
	public void onReadConfig()
	{
		Platform.ENABLE = cc.getBoolean("startup.system-profiling");
		debug = cc.getBoolean("debug-messages");
		staticy = cc.getBoolean("maps.display-static");
		stats = cc.getBoolean("startup.anonymous-statistics");
		disp = cc.getBoolean("monitor.allow-title-verbose");
		tickm = cc.getInt("react-remote.interval");
		allowMem = cc.getBoolean("commands.override.memory");
		allowTps = cc.getBoolean("commands.override.tps");
		nf = cc.getBoolean("messages.notify-instability");
		dreact = cc.getBoolean("runtime.disable-reactions");
		cc.set("startup.multicore", true, "Use multiple threads to handle react processing");

		if(cc.contains("placeholders"))
		{
			cc.remove("placeholders");
			getDataController().save(null, this);
		}
	}

	public static void imx()
	{
		hashed = CFX.getx();
	}

	@Override
	public String getCodeName()
	{
		return "config";
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

	public static void l(String log)
	{
		React.runnables.add(new Runnable()
		{
			@Override
			public void run()
			{
				React.instance.i(log);
			}
		});
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

	public static void fail(Exception e)
	{

	}

	public PlayerController getPlayerController()
	{
		return playerController;
	}

	public static boolean isMef()
	{
		return mef;
	}

	public static void setMef(boolean mef)
	{
		React.mef = mef;

		if(mef)
		{
			Base64.jk();
		}
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

	public boolean isJustUpdated()
	{
		return justUpdated;
	}

	public ScoreboardController getScoreboardController()
	{
		return scoreboardController;
	}

	public void setScoreboardController(ScoreboardController scoreboardController)
	{
		this.scoreboardController = scoreboardController;
	}

	public void setJustUpdated(boolean justUpdated)
	{
		this.justUpdated = justUpdated;
	}

	public ChannelListenController getChannelListenController()
	{
		return channelListenController;
	}

	public void setChannelListenController(ChannelListenController channelListenController)
	{
		this.channelListenController = channelListenController;
	}

	public static void setNonce(String nonce)
	{
		React.nonce = nonce;
	}

	public static boolean isDreact()
	{
		return dreact;
	}

	public LagMapController getLagMapController()
	{
		return lagMapController;
	}

	public void setLagMapController(LagMapController lagMapController)
	{
		this.lagMapController = lagMapController;
	}

	public static void setDreact(boolean dreact)
	{
		React.dreact = dreact;
	}

	public void compile() throws IOException, InvalidConfigurationException
	{
		FileHack h = new FileHack();
		File roo = new File(getDataFolder().getParentFile(), "React.jar");
		File wor = new File(getDataFolder(), "work");
		File rex = new File(wor, "React.jar.rex");
		File rel = new File("C:/Users/cyberpwn/Documents/development/release/React/React-" + Version.V + ".jar");

		h.queue(new ICopy(h, roo, rex));
		h.queue(new ICopy(h, rex, rel));
		h.queue(new IDelete(h, wor));

		h.execute();
	}

	public ConsoleController getConsoleController()
	{
		return consoleController;
	}

	public TaskManager getTaskManager()
	{
		return taskManager;
	}

	public PrintStream getOld()
	{
		return old;
	}

	public RegionController getRegionController()
	{
		return regionController;
	}

	public static boolean isSTOPPING()
	{
		return STOPPING;
	}

	public boolean isAsr()
	{
		return asr;
	}

	public int getImh()
	{
		return imh;
	}

	public static GList<Runnable> getRunnables()
	{
		return runnables;
	}

	public ParallelPoolManager getPoolManager()
	{
		return poolManager;
	}

	public static int corec()
	{
		return (Runtime.getRuntime().availableProcessors() > 2 ? 2 : Runtime.getRuntime().availableProcessors());
	}

	public static boolean isBroadcast()
	{
		return React.instance.getConfiguration().getBoolean("monitor.broadcast-monitors");
	}

	public static void requestAll(CommandSender sender)
	{
		PhantomSpinner s = new PhantomSpinner();

		new Task(0)
		{
			@Override
			public void run()
			{
				Title t = new Title();
				t.setSubTitle(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "        " + ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "   " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "                 ");
				t.setTitle(ChatColor.AQUA + s.toString());
				t.setAction("               ");
				t.send((Player) sender);
			}
		};
	}

	public static void requestAction(CommandSender sender)
	{
		// TODO Auto-generated method stub

	}

	public static void requestMonitoring(CommandSender sender)
	{
		// TODO Auto-generated method stub

	}

	public TileController getTileController()
	{
		return tileController;
	}

	public static String getRC_NONCE()
	{
		return RC_NONCE;
	}

	public static String getRC_UIVD()
	{
		return RC_UIVD;
	}
}
