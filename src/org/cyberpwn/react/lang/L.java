package org.cyberpwn.react.lang;

public class L
{
	public static String LANGUAGE_CODE = "en";
	public static String LANGUAGE_NAME = "English";
	public static String REACT_NAME = "React";
	public static String GUI_ACTIONS = "Actions";
	public static String EXAMPLE = "Example";
	public static String REACTIONS = "Reactions";
	public static String SAMPLERS = "Samplers";
	public static String MONITORING = "Monitoring";
	
	public static String SAMPLER_CHUNK_GEN = "The average chunks generated per second.";
	public static String SAMPLER_CHUNK_LOAD = "The average chunks loaded per second.";
	public static String SAMPLER_CHUNK_MEMORY = "Counts all chunks in all loaded worlds and determines the memory consumption.";
	public static String SAMPLER_CHUNK_LOADED = "Counts all chunks in all loaded worlds.";
	
	public static String SAMPLER_WORLD_DROPS = "Counts all drops loaded on all worlds.";
	public static String SAMPLER_WORLD_ENTITIES = "Counts all entities loaded on all worlds.";
	public static String SAMPLER_WORLD_LIQUIDFLOW = "Liquid Flows per Second. This is when liquid expands or drains.";
	public static String SAMPLER_WORLD_TNT = "The average tnt primes per second.";
	public static String SAMPLER_WORLD_REDSTONE = "Redstone Updates per Second. Every time redstone updates at all.";
	
	public static String SAMPLER_MEMORY_GARBAGEDIRECTION = "When the server uses memory, and is done using it, it marks it as trash. This sample measures the direction of it.";
	public static String SAMPLER_MEMORY_MAHS = "This measures the rate the server consumes memory. Note that more than 90% of this memory will be quickly restored for reuse.";
	public static String SAMPLER_MEMORY_PLAYERS = "A very rough estimate of how many megabytes a player takes up.";
	public static String SAMPLER_MEMORY_GC = "When the server uses memory, and is done using it, it marks it as trash. This sample measures how often it 'cleans' the trash.";
	public static String SAMPLER_MEMORY_USED = "Memory (RAM) is temporary storage for short term data such as player position, chunks, and more. This measures how much memory you are using.";
	
	public static String SAMPLER_GENERAL_HISTORY = "View this on the lower right quadrant on the map.";
	public static String SAMPLER_GENERAL_HITRATE = "Determines the hit rate of reacts reactions. For example, what percent of the time does react detect something specific.";
	public static String SAMPLER_GENERAL_CPUSCORE = "This calculates the single threaded performance of your cpu while under (or not) load.";
	public static String SAMPLER_GENERAL_PLUGINS = "React tracks and communicates with other plugins. They are typically common plugins that lots of servers use.";
	public static String SAMPLER_GENERAL_PLAYERS = "Samples how many players are connected to the server.";
	public static String SAMPLER_GENERAL_REACTIONTIME = "Counts the ammount of time in miliseconds it takes react to do everything it is scheduled to do. One tick is 50ms, so typically react is under 3ms";
	public static String SAMPLER_GENERAL_STABILITY = "This percent is determined by your TPS, MEM, and more over large periods of time.";
	public static String SAMPLER_GENERAL_TPS = "Twenty times a second the server 'ticks'. This is where all the action happens on the cpu. If this number is lower than 17 you will start to expirience server lag.";
	public static String SAMPLER_GENERAL_TIMINGS = "Check the hardest hitting timings value.";
	
	
	
	
	
	public static String BOOK_SAMPLES_TITLE = "Samples";
	public static String BOOK_SAMPLES_TEXT = "For React to accuratley judge lag, you cannot simply track the Ticks per second. If you dont know what is CAUSING the lag, you can't guarantee a fix. Welcome to react!";
	public static String BOOK_REACTIONS_TITLE = "Reactions";
	public static String BOOK_REACTIONS_TEXT = "Since react is sampling many aspects of the server, we can now determine the source of the lag. This allows us to 'react to it', fixing the problem automatically.";
	public static String BOOK_TRIALERROR_TITLE = "Trial & Error";
	public static String BOOK_TRIALERROR_TEXT = "Since there are many ways to fix one problem, react will try several different things to fix an issue. If it fixes, react keeps using that method. This means react tailors itself to your server over time.";
	public static String BOOK_MONITORING_TITLE = "Monitoring";
	public static String BOOK_MONITORING_TEXT = "React is not opaque. You are able to view everything react sees, to start monitoring, use /react monitor, but you may want to use /re help monitoring first to see how it works.";
	public static String BOOK_CONFIGURATION_TITLE = "Configuration";
	public static String BOOK_CONFIGURATION_TEXT = "React is highly configurable. You can disable any feature you wish, and configure any timings as you like. React can be taylored to literally any server.";
	public static String BOOK_MONITOR_TITLE = "How to Monitor";
	public static String BOOK_MONITOR_TEXT = "The base command for monitoring is /re mon (/react monitor). This command sends title messages to you with numbers. If you don't know what a sample is, use /re help samplers";
	public static String BOOK_TABS_TITLE = "Changing Tabs";
	public static String BOOK_TABS_TEXT = "Each element in the monitor actually contains more samples. Hold Shift and scroll your mouse wheel to select the tab you like. Try it!";
	public static String BOOK_PERFORMANCE_TITLE = "Performence Charting";
	public static String BOOK_PERFORMANCE_TEXT = "You can also view a live graph of your server performence. It is rendered on a map. Use /re map";
	public static String BOOK_PERSISTANCE_TITLE = "Persistance";
	public static String BOOK_PERSISTANCE_TEXT = "If you log off, or the server reboots, your monitor/map mode will be turned back on (unless you turn it off)";
	public static String BOOK_GREETING = "Hello There";
	public static String BOOK_GREETING_TEXT = "React is a plugin that handles server lag in a judge & react way. This plugin does not just remove entities on a timer and call it 'lag remover'. React fixes this problem.";
	
	public static String MESSAGE_PAPER_UNSUPPORTED = "React Timings Processing does not yet support PaperSpigot as PaperSpigot modifies the api for getting timings causing all sorts of issues.";
	public static String MESSAGE_BOOK = "Book given/updated";
	public static String MESSAGE_DUMPED = "Created a dump file to ";
	public static String MESSAGE_MONITOR_LOCK_FAIL = "Please turn on monitoring to lock tabs.";
	public static String MESSAGE_VERBOSEOFF = "Verbose Disabled";
	public static String MESSAGE_VERBOSEON = "Verbose Enabled";
	public static String MESSAGE_VERBOSE = "Verbose: ";
	public static String MESSAGE_LANG_UNKNOWN = "Unknown language";
	public static String MESSAGE_LANG_HELP = "/re lang <language>";
	public static String MESSAGE_NO_DATA = "No Data. This typically means you are not on a bungeecord network.";
	public static String MESSAGE_SERVERCOUNT_A = "There are ";
	public static String MESSAGE_SERVERCOUNT_B = " servers on the network using react.";
	public static String MESSAGE_SERVERCURRENT = "You are on ";
	public static String MESSAGE_BUNGEEOFF = "Bungeecord support is off.";
	public static String MESSAGE_QUERYRESULT = "Queried Result";
	public static String MESSAGE_ERROR_PLUGINUNKNOWN = "Unknown Plugin";
	public static String MESSAGE_ERROR_NONUMBER = "Not a number: ";
	public static String MESSAGE_ERROR_NOTCOMMAND = "Unknown Command. Use /react";
	public static String MESSAGE_HELP_GUESS = "/react guess -m 1024 >> Guess players with 1g of mem";
	public static String MESSAGE_UPDATE_FOUND = "Update Found: ";
	public static String MESSAGE_ERROR_LATESTVERSION = "You have the latest version!";
	public static String MESSAGE_BOOK_CLICK = "Click any of these for a book on them.";
	public static String MESSAGE_HELP_ABOUT_A = "/re help about";
	public static String MESSAGE_HELP_ABOUT_B = "General information on what react is, and how to get started.";
	public static String MESSAGE_HELP_MONITORING_A = "/re help monitoring";
	public static String MESSAGE_HELP_MONITORING_B = "Information on how to monitor.";
	public static String MESSAGE_HELP_REACTIONS_A = "/re help reactions";
	public static String MESSAGE_HELP_REACTIONS_B = "Shows all of the actions that can be used by react.";
	public static String MESSAGE_HELP_SAMPLERS_A = "/re help samplers";
	public static String MESSAGE_HELP_SAMPLERS_B = "Shows all of the samplers react uses.";
	public static String MESSAGE_UNKNOWN_BOOK = "Unknown book. Use /re help";
	public static String MESSAGE_UNKNOWN_ACTION = "Unknown Action. Use /re act for a list of actions.";
	public static String MESSAGE_MEMORY_MAX = "Max Memory: ";
	public static String MESSAGE_MEMORY_USED = "Used Memory: ";
	public static String MESSAGE_GARBAGE = "Garbage: ";
	public static String MESSAGE_PLAYERS = "Players: ";
	public static String MESSAGE_CHUNKS = "Chunks: ";
	public static String MESSAGE_PLUGINS = "Plugins/Other: ";
	public static String MESSAGE_WORLD_WARNING = "WARNING: SERVER WAS LAGGING HARD ON THE LAST TICK";
	public static String MESSAGE_WORLD_SAVEINVALID = "Not Saving... We Just Saved!";
	public static String MESSAGE_WORLD_PRECAUTION = "REACT IS TAKING PRECAUTIONS TO ENSURE NO DATA IS LOST IN THE EVENT OF A CRASH";
	public static String MESSAGE_WORLD_PLAYERSAVE = "SAVING PLAYERS...";
	public static String MESSAGE_WORLD_SAVE = "SAVING WORLD: ";
	public static String MESSAGE_WORLD_SUCCESS = "PRECAUTIONS TAKEN. HOPEFULLY WE WONT CRASH!";
	public static String MESSAGE_WORLD_DISABLE = "HEART BEAT SAVING DISABLED. Not saving worlds or players.";
	public static String MESSAGE_CPUSCORE = "CPU Score: ";
	public static String MESSAGE_MONITORING_ENABLED = "Monitoring Enabled";
	public static String MESSAGE_MONITORING_DISABLED = "Monitoring Disabled";
	public static String MESSAGE_MAPPING_ENABLED = "Mapping Enabled";
	public static String MESSAGE_MAPPING_DISABLED = "Mapping Disabled";
	public static String MESSAGE_INSUFFICIENT_PERMISSION = "Insufficient Permission";
	public static String MESSAGE_SIDED_INGAME = "You must be in-game to use this_";
	public static String MESSAGE_SIDED_CONSOLE = "You must use this from the console.";
	public static String MESSAGE_ACTION_FULLY_AUTOMATIC = " is a fully automatic action.";
	public static String MESSAGE_ACTION_STABILIZING = "Stabilizing...";
	public static String MESSAGE_INTERNAL_RELOADED = "Reloaded";
	public static String MESSAGE_HEARTBEAT_START = "STARTING HEART BEAT THREAD";
	public static String MESSAGE_HOOK_ATTEMPT = "Attempting to hook into Placeholder API";
	public static String MESSAGE_HOOK_SUCCESS = "Hooked into Placeholder API";
	public static String MESSAGE_PLAYER_ONLY = "You must be ingame to use this.";
	public static String MESSAGE_CONSOLE_ONLY = "You must use this from the console.";
	public static String MESSAGE_ISSUES = "Stabilizing...";
	public static String MESSAGE_FIXED = "Fixed!";
	public static String MESSAGE_RELOADED = "React Reloaded";
	public static String MESSAGE_MANUAL = "Manual Action: ";
	public static String MESSAGE_MANUAL_STARTED = " started.";
	public static String MESSAGE_MANUAL_FINISH = "Manual Action: ";
	public static String MESSAGE_MANUAL_FINISHED = " finished ";
	public static String MESSAGE_UPDATE = "Update Found! ";
	
	public static String COMMAND_STATUS = "Sends a book of updated issues React finds.";
	public static String COMMAND_CPUSCORE = "Get your server's CPU score.";
	public static String COMMAND_SERVERS = "List all servers on the network with data.";
	public static String COMMAND_GUESSMEMORY = "Guess server capabilities with X Ram";
	public static String COMMAND_MONITOR = "enables title-text monitoring";
	public static String COMMAND_MAP = "enables visual graphical monitoring";
	public static String COMMAND_RELOAD = "reloads all react configuration files.";
	public static String COMMAND_BOOK = "Sends a book of updated issues react finds.";
	public static String COMMAND_VERSION = "Compares your version with any new ones.";
	public static String COMMAND_HELP = "Sends a book of information on help topics.";
	public static String COMMAND_QUERY = "Query information on plugin(s).";
	public static String COMMAND_LIST = "List all servers on the network with data.";
	public static String COMMAND_ACT = "Run actions (or turn on/off automatic actions)";
	public static String COMMAND_PLUGINS = "List plugin timings";
	public static String COMMAND_UPDATE = "Force Download the latest copy of react.";
	public static String COMMAND_GUESS = "Guess Server Capabilities.";
	public static String COMMAND_TIMINGS = "Send a book of the hardest hitting timings.";
	public static String COMMAND_LANG = "Change or list supported languages";
}
