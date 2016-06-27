package org.cyberpwn.react.lang;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;

public class Info
{
	public static String URL_LANGUAGE = "https://raw.githubusercontent.com/cyberpwnn/React/master/serve/languages/%s.yml";
	public static String COLOR_A = ChatColor.AQUA + "";
	public static String COLOR_B = ChatColor.DARK_AQUA + "";
	public static String COLOR_C = ChatColor.DARK_GRAY + "";
	public static String COLOR_D = ChatColor.GREEN + "";
	public static ChatColor COLOR_ERR = ChatColor.RED;
	public static String NAME = COLOR_A + "React";
	public static String VERSION = COLOR_B + Version.V;
	public static String VERSION_BLANK = ChatColor.stripColor(VERSION);
	public static Integer VERSION_CODE = Version.C;
	public static String TAG = COLOR_C + "[" + NAME + COLOR_C + "]: " + ChatColor.WHITE;
	public static String TAG_SPECIAL = COLOR_C + "[" + NAME + ChatColor.GREEN + " - %s" + COLOR_C + "]: " + ChatColor.WHITE;
	public static String COMMAND_HELP = COLOR_A + "/re %s" + COLOR_D + " - " + "%s";
	public static String COMMAND = "react";
	public static String PERM_MONITOR = "react.monitor";
	public static String PERM_ACT = "react.act";
	public static String MZX = "raw.githubusercontent";
	public static String PERM_RELOAD = "react.reload";
	public static String CH_MONITOR = String.format(Info.COMMAND_HELP, "monitor,mon", "enables title-text monitoring").trim();
	public static String CH_MAP = String.format(Info.COMMAND_HELP, "map", "enables visual graphical monitoring").trim();
	public static String CH_RELOAD = String.format(Info.COMMAND_HELP, "reload", "reloads all react configuration files.").trim();
	public static String CH_BOOK = String.format(Info.COMMAND_HELP, "status,book", "Sends a book of updated issues react finds.").trim();
	public static String CH_VERSION = String.format(Info.COMMAND_HELP, "version,v", "Compares your version with any new ones.").trim();
	public static String CH_HELP = String.format(Info.COMMAND_HELP, "help <topic>", "Sends a book of information on help topics.").trim();
	public static String CH_QUERY = String.format(Info.COMMAND_HELP, "query,q [plugin]", "Query information on plugin(s).").trim();
	public static String CH_CPU = String.format(Info.COMMAND_HELP, "cpu-score,cs", "Get your server cpu score.").trim();
	public static String CH_LIST = String.format(Info.COMMAND_HELP, "servers,list", "List all servers on the network with data.").trim();
	public static String CH_ACT = String.format(Info.COMMAND_HELP, "act [-off -on]", "Run actions (or turn on/off automatic actions)").trim();
	public static String CH_PLUGINS = String.format(Info.COMMAND_HELP, "plugins,p [single plugin]", "List plugin timings").trim();
	public static String CH_UPDATE = String.format(Info.COMMAND_HELP, "update,u", "Force Download the latest copy of react.").trim();
	public static String CH_GUESS = String.format(Info.COMMAND_HELP, "guess,g", "Guess Server Capabilities.").trim();
	public static String CH_GUESSM = String.format(Info.COMMAND_HELP, "guess,g -m <mb>", "Guess Server Capabilities with X Ram").trim();
	public static String CH_TIMINGS = String.format(Info.COMMAND_HELP, "timings,t [searchkey]", "Send a book of the hardest hitting timings.").trim();
	public static String CH_LANG = String.format(Info.COMMAND_HELP, "lang,l [lang]", "Change or list supported languages").trim();
	public static String ELEMENT_NORMAL = "%s ";
	public static String ELEMENT_BAD = ChatColor.UNDERLINE + "%s ";
	public static String HR = COLOR_C + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 75);
	public static String HRN = COLOR_C + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 28) + ChatColor.RESET + COLOR_A + "  %s  " + COLOR_C + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 28);
	public static String CH_ALL = String.format(HRN, "React") + "\n" + CH_HELP + "\n" + CH_PLUGINS + "\n" + CH_UPDATE + "\n" + CH_ACT + "\n" + CH_TIMINGS + "\n" + CH_LANG + "\n" + CH_RELOAD + "\n" + CH_MONITOR + "\n" + CH_MAP + "\n" + CH_BOOK + "\n" + CH_QUERY + "\n" + CH_CPU + "\n" + CH_LIST + "\n" + CH_GUESS + "\n" + CH_GUESSM + "\n" + CH_VERSION + "\n" + HR;
	
	public static void splash()
	{
		m("__________  ___________    _____    _________   ___________");
		m("\\______   \\ \\_   _____/   /  _  \\   \\_   ___ \\  \\__    ___/");
		m(" |       _/  |    __)_   /  /_\\  \\  /    \\  \\/    |    |   ");
		m(" |    |   \\  |        \\ /    |    \\ \\     \\____   |    |   ");
		m(" |____|_  / /_______  / \\____|__  /  \\______  /   |____|   ");
		m("        \\/          \\/          \\/          \\/             ");
		m("React " + ChatColor.GREEN + "v" + VERSION_BLANK + "(" + Info.VERSION_CODE + ") " + ChatColor.LIGHT_PURPLE + ChatColor.LIGHT_PURPLE + React.instance().getControllers().size() + " Controllers, " + Info.COLOR_ERR + React.instance().getSampleController().getSamples().size() + " Samplers, " + ChatColor.YELLOW + React.instance().getActionController().getActions().size() + " Actions");
	}
	
	public static void m(String s)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + s);
	}
	
	public static void rebuildLang()
	{
		TAG = ChatColor.AQUA + "[" + ChatColor.DARK_GRAY + ChatColor.BOLD + L.REACT_NAME + ChatColor.RESET + ChatColor.AQUA + "]: " + ChatColor.WHITE;
		TAG_SPECIAL = COLOR_C + "[" + NAME + ChatColor.GREEN + " - %s" + COLOR_C + "]: " + ChatColor.WHITE;
		CH_MONITOR = String.format(Info.COMMAND_HELP, "monitor,mon", L.COMMAND_MONITOR).trim();
		CH_MAP = String.format(Info.COMMAND_HELP, "map", L.COMMAND_MAP).trim();
		CH_RELOAD = String.format(Info.COMMAND_HELP, "reload", L.COMMAND_RELOAD).trim();
		CH_BOOK = String.format(Info.COMMAND_HELP, "status,book", L.COMMAND_STATUS).trim();
		CH_VERSION = String.format(Info.COMMAND_HELP, "version,v", L.COMMAND_VERSION).trim();
		CH_HELP = String.format(Info.COMMAND_HELP, "help <topic>", L.COMMAND_HELP).trim();
		CH_QUERY = String.format(Info.COMMAND_HELP, "query,q [plugin]", L.COMMAND_QUERY).trim();
		CH_CPU = String.format(Info.COMMAND_HELP, "cpu-score,cs", L.COMMAND_CPUSCORE).trim();
		CH_LIST = String.format(Info.COMMAND_HELP, "servers,list", L.COMMAND_LIST).trim();
		CH_ACT = String.format(Info.COMMAND_HELP, "act [-off -on]", L.COMMAND_ACT).trim();
		CH_PLUGINS = String.format(Info.COMMAND_HELP, "plugins,p [single plugin]", L.COMMAND_PLUGINS).trim();
		CH_UPDATE = String.format(Info.COMMAND_HELP, "update,u", L.COMMAND_UPDATE).trim();
		CH_GUESS = String.format(Info.COMMAND_HELP, "guess,g", L.COMMAND_GUESS).trim();
		CH_GUESSM = String.format(Info.COMMAND_HELP, "guess,g -m <mb>", L.COMMAND_GUESSMEMORY).trim();
		CH_TIMINGS = String.format(Info.COMMAND_HELP, "timings,t [searchkey]", L.COMMAND_TIMINGS).trim();
		CH_LANG = String.format(Info.COMMAND_HELP, "lang,l [lang]", L.COMMAND_LANG).trim();
		CH_ALL = String.format(HRN, "React") + "\n" + CH_HELP + "\n" + CH_PLUGINS + "\n" + CH_UPDATE + "\n" + CH_ACT + "\n" + CH_TIMINGS + "\n" + CH_RELOAD + "\n" + CH_MONITOR + "\n" + CH_MAP + "\n" + CH_BOOK + "\n" + CH_QUERY + "\n" + CH_CPU + "\n" + CH_LIST + "\n" + CH_GUESS + "\n" + CH_GUESSM + "\n" + CH_VERSION + "\n" + HR;
	}
}
