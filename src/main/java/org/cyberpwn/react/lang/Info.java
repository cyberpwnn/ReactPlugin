package org.cyberpwn.react.lang;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;

public class Info
{
	public static String URL_LANGUAGE = "https://raw.githubusercontent.com/cyberpwnn/React/master/serve/languages/%s.yml";
	public static String COLOR_A = ChatColor.AQUA + "";
	public static String COLOR_B = ChatColor.DARK_AQUA + "";
	public static String COLOR_C = ChatColor.DARK_GRAY + "";
	public static String COLOR_D = ChatColor.DARK_GRAY + "";
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
	public static String MSG_PERM = "&cInsufficient Permission";
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
	public static TextMap r = null;
	
	public static void buildBlocks()
	{
		r = new TextMap("React");
		TextBlock rSpace = new TextBlock();
		rSpace.add("       ");
		rSpace.add("       ");
		rSpace.add("       ");
		rSpace.add("       ");
		rSpace.add("       ");
		rSpace.add("       ");
		rSpace.add("       ");
		
		TextBlock rA = new TextBlock();
		rA.add("        ");
		rA.add("   /\\   ");
		rA.add("  /  \\  ");
		rA.add(" / /\\ \\ ");
		rA.add("| |__| |");
		rA.add("|______|");
		rA.add("        ");
		
		TextBlock rB = new TextBlock();
		rB.add(" ______  ");
		rB.add("(____  \\ ");
		rB.add(" ____)  )");
		rB.add("|  __  ( ");
		rB.add("| |__)  )");
		rB.add("|______/ ");
		rB.add("         ");
		
		TextBlock rC = new TextBlock();
		rC.add("  ______ ");
		rC.add(" / _____)");
		rC.add("| /      ");
		rC.add("| |      ");
		rC.add("| \\_____ ");
		rC.add(" \\______)");
		rC.add("         ");
		
		TextBlock rD = new TextBlock();
		rD.add(" _____   ");
		rD.add("(____ \\  ");
		rD.add(" _   \\ \\ ");
		rD.add("| |   | |");
		rD.add("| |__/ / ");
		rD.add("|_____/  ");
		rD.add("         ");
		
		TextBlock rE = new TextBlock();
		rE.add(" _______ ");
		rE.add("(_______)");
		rE.add(" _____   ");
		rE.add("|  ___)  ");
		rE.add("| |_____ ");
		rE.add("|_______)");
		rE.add("         ");
		
		TextBlock rF = new TextBlock();
		rF.add(" _______ ");
		rF.add("(_______)");
		rF.add(" _____   ");
		rF.add("|  ___)  ");
		rF.add("| |      ");
		rF.add("|_|      ");
		rF.add("         ");
		
		TextBlock rG = new TextBlock();
		rG.add("  ______ ");
		rG.add(" / _____)");
		rG.add("| /  ___ ");
		rG.add("| | (___)");
		rG.add("| \\____/|");
		rG.add(" \\_____/ ");
		rG.add("         ");
		
		TextBlock rH = new TextBlock();
		rH.add(" _     _ ");
		rH.add("| |   | |");
		rH.add("| |__ | |");
		rH.add("|  __)| |");
		rH.add("| |   | |");
		rH.add("|_|   |_|");
		rH.add("         ");
		
		TextBlock rI = new TextBlock();
		rI.add(" _____ ");
		rI.add("(_____)");
		rI.add("   _   ");
		rI.add("  | |  ");
		rI.add(" _| |_ ");
		rI.add("(_____)");
		rI.add("       ");
		
		TextBlock rJ = new TextBlock();
		rJ.add("   _____ ");
		rJ.add("  (_____)");
		rJ.add("     _   ");
		rJ.add("    | |  ");
		rJ.add(" ___| |  ");
		rJ.add("(____/   ");
		rJ.add("         ");
		
		TextBlock rK = new TextBlock();
		rK.add(" _    _ ");
		rK.add("| |  / )");
		rK.add("| | / / ");
		rK.add("| |< <  ");
		rK.add("| | \\ \\ ");
		rK.add("|_|  \\_)");
		rK.add("        ");
		
		TextBlock rL = new TextBlock();
		rL.add(" _       ");
		rL.add("| |      ");
		rL.add("| |      ");
		rL.add("| |      ");
		rL.add("| |_____ ");
		rL.add("|_______)");
		rL.add("         ");
		
		TextBlock rM = new TextBlock();
		rM.add(" ______  ");
		rM.add("|  ___ \\ ");
		rM.add("| | _ | |");
		rM.add("| || || |");
		rM.add("| || || |");
		rM.add("|_||_||_|");
		rM.add("         ");
		
		TextBlock rN = new TextBlock();
		rN.add(" ______  ");
		rN.add("|  ___ \\ ");
		rN.add("| |   | |");
		rN.add("| |   | |");
		rN.add("| |   | |");
		rN.add("|_|   |_|");
		rN.add("         ");
		
		TextBlock rO = new TextBlock();
		rO.add("  _____  ");
		rO.add(" / ___ \\ ");
		rO.add("| |   | |");
		rO.add("| |   | |");
		rO.add("| |___| |");
		rO.add(" \\_____/ ");
		rO.add("         ");
		
		TextBlock rP = new TextBlock();
		rP.add(" ______  ");
		rP.add("(_____ \\ ");
		rP.add(" _____) )");
		rP.add("|  ____/ ");
		rP.add("| |      ");
		rP.add("|_|      ");
		rP.add("         ");
		
		TextBlock rQ = new TextBlock();
		rQ.add("  _____  ");
		rQ.add(" / ___ \\ ");
		rQ.add("| |   | |");
		rQ.add("| |   |_|");
		rQ.add(" \\ \\____ ");
		rQ.add("  \\_____)");
		rQ.add("         ");
		
		TextBlock rR = new TextBlock();
		rR.add(" ______  ");
		rR.add("(_____ \\ ");
		rR.add(" _____) )");
		rR.add("(_____ ( ");
		rR.add("      | |");
		rR.add("      |_|");
		rR.add("         ");
		
		TextBlock rS = new TextBlock();
		rS.add("    _    ");
		rS.add("   | |   ");
		rS.add("    \\ \\  ");
		rS.add("     \\ \\ ");
		rS.add(" _____) )");
		rS.add("(______/ ");
		rS.add("         ");
		
		TextBlock rT = new TextBlock();
		rT.add(" _______ ");
		rT.add("(_______)");
		rT.add(" _       ");
		rT.add("| |      ");
		rT.add("| |_____ ");
		rT.add(" \\______)");
		rT.add("         ");
		
		TextBlock rU = new TextBlock();
		rU.add(" _     _ ");
		rU.add("| |   | |");
		rU.add("| |   | |");
		rU.add("| |   | |");
		rU.add("| |___| |");
		rU.add(" \\______|");
		rU.add("         ");
		
		TextBlock rV = new TextBlock();
		rV.add(" _    _ ");
		rV.add("| |  | |");
		rV.add("| |  | |");
		rV.add(" \\ \\/ / ");
		rV.add("  \\  /  ");
		rV.add("   \\/   ");
		rV.add("        ");
		
		TextBlock rW = new TextBlock();
		rW.add(" _  _  _ ");
		rW.add("| || || |");
		rW.add("| || || |");
		rW.add("| ||_|| |");
		rW.add("| |___| |");
		rW.add(" \\______|");
		rW.add("         ");
		
		TextBlock rX = new TextBlock();
		rX.add(" _    _ ");
		rX.add("\\ \\  / /");
		rX.add(" \\ \\/ / ");
		rX.add("  )  (  ");
		rX.add(" / /\\ \\ ");
		rX.add("/_/  \\_\\");
		rX.add("        ");
		
		TextBlock rY = new TextBlock();
		rY.add(" _     _ ");
		rY.add("| |   | |");
		rY.add("| |___| |");
		rY.add(" \\_____/ ");
		rY.add("   ___   ");
		rY.add("  (___)  ");
		rY.add("         ");
		
		TextBlock rZ = new TextBlock();
		rZ.add(" _______ ");
		rZ.add("(_______)");
		rZ.add("   __    ");
		rZ.add("  / /    ");
		rZ.add(" / /____ ");
		rZ.add("(_______)");
		rZ.add("         ");
		
		TextBlock ra = new TextBlock();
		ra.add("       ");
		ra.add("       ");
		ra.add("  ____ ");
		ra.add(" / _  |");
		ra.add("( ( | |");
		ra.add(" \\_||_|");
		ra.add("       ");
		
		TextBlock rb = new TextBlock();
		rb.add(" _     ");
		rb.add("| |    ");
		rb.add("| | _  ");
		rb.add("| || \\ ");
		rb.add("| |_) )");
		rb.add("|____/ ");
		rb.add("       ");
		
		TextBlock rc = new TextBlock();
		rc.add("       ");
		rc.add("       ");
		rc.add("  ____ ");
		rc.add(" / ___)");
		rc.add("( (___ ");
		rc.add(" \\____)");
		rc.add("       ");
		
		TextBlock rd = new TextBlock();
		rd.add("     _ ");
		rd.add("    | |");
		rd.add("  _ | |");
		rd.add(" / || |");
		rd.add("( (_| |");
		rd.add(" \\____|");
		rd.add("       ");
		
		TextBlock re = new TextBlock();
		re.add("       ");
		re.add("       ");
		re.add("  ____ ");
		re.add(" / _  )");
		re.add("( (/ / ");
		re.add(" \\____)");
		re.add("       ");
		
		TextBlock rf = new TextBlock();
		rf.add("  ___ ");
		rf.add(" / __)");
		rf.add("| |__ ");
		rf.add("|  __)");
		rf.add("| |   ");
		rf.add("|_|   ");
		rf.add("      ");
		
		TextBlock rg = new TextBlock();
		rg.add("       ");
		rg.add("       ");
		rg.add("  ____ ");
		rg.add(" / _  |");
		rg.add("( ( | |");
		rg.add(" \\_|| |");
		rg.add("(_____|");
		
		TextBlock rh = new TextBlock();
		rh.add(" _     ");
		rh.add("| |    ");
		rh.add("| | _  ");
		rh.add("| || \\ ");
		rh.add("| | | |");
		rh.add("|_| |_|");
		rh.add("       ");
		
		TextBlock ri = new TextBlock();
		ri.add(" _ ");
		ri.add("(_)");
		ri.add(" _ ");
		ri.add("| |");
		ri.add("| |");
		ri.add("|_|");
		ri.add("   ");
		
		TextBlock rj = new TextBlock();
		rj.add("   _ ");
		rj.add("  (_)");
		rj.add("   _ ");
		rj.add("  | |");
		rj.add("  | |");
		rj.add(" _| |");
		rj.add("(__/ ");
		
		TextBlock rk = new TextBlock();
		rk.add(" _     ");
		rk.add("| |    ");
		rk.add("| |  _ ");
		rk.add("| | / )");
		rk.add("| |< ( ");
		rk.add("|_| \\_)");
		rk.add("       ");
		
		TextBlock rl = new TextBlock();
		rl.add(" _ ");
		rl.add("| |");
		rl.add("| |");
		rl.add("| |");
		rl.add("| |");
		rl.add("|_|");
		rl.add("   ");
		
		TextBlock rm = new TextBlock();
		rm.add("       ");
		rm.add("       ");
		rm.add(" ____  ");
		rm.add("|    \\ ");
		rm.add("| | | |");
		rm.add("|_|_|_|");
		rm.add("       ");
		
		TextBlock rn = new TextBlock();
		rn.add("       ");
		rn.add("       ");
		rn.add(" ____  ");
		rn.add("|  _ \\ ");
		rn.add("| | | |");
		rn.add("|_| |_|");
		rn.add("       ");
		
		TextBlock ro = new TextBlock();
		ro.add("       ");
		ro.add("       ");
		ro.add("  ___  ");
		ro.add(" / _ \\ ");
		ro.add("| |_| |");
		ro.add(" \\___/ ");
		ro.add("       ");
		
		TextBlock rp = new TextBlock();
		rp.add("       ");
		rp.add("       ");
		rp.add(" ____  ");
		rp.add("|  _ \\ ");
		rp.add("| | | |");
		rp.add("| ||_/ ");
		rp.add("|_|    ");
		
		TextBlock rq = new TextBlock();
		rq.add("       ");
		rq.add("       ");
		rq.add("  ____ ");
		rq.add(" / _  |");
		rq.add("| | | |");
		rq.add(" \\_|| |");
		rq.add("    |_|");
		
		TextBlock rr = new TextBlock();
		rr.add("       ");
		rr.add("       ");
		rr.add("  ____ ");
		rr.add(" / ___)");
		rr.add("| |    ");
		rr.add("|_|    ");
		rr.add("       ");
		
		TextBlock rs = new TextBlock();
		rs.add("      ");
		rs.add("      ");
		rs.add("  ___ ");
		rs.add(" /___)");
		rs.add("|___ |");
		rs.add("(___/ ");
		rs.add("      ");
		
		TextBlock rt = new TextBlock();
		rt.add("      ");
		rt.add(" _    ");
		rt.add("| |_  ");
		rt.add("|  _) ");
		rt.add("| |__ ");
		rt.add(" \\___)");
		rt.add("      ");
		
		TextBlock ru = new TextBlock();
		ru.add("       ");
		ru.add("       ");
		ru.add(" _   _ ");
		ru.add("| | | |");
		ru.add("| |_| |");
		ru.add(" \\____|");
		ru.add("       ");
		
		TextBlock rv = new TextBlock();
		rv.add("       ");
		rv.add("       ");
		rv.add(" _   _ ");
		rv.add("| | | |");
		rv.add(" \\ V / ");
		rv.add("  \\_/  ");
		rv.add("       ");
		
		TextBlock rw = new TextBlock();
		rw.add("       ");
		rw.add("       ");
		rw.add(" _ _ _ ");
		rw.add("| | | |");
		rw.add("| | | |");
		rw.add(" \\____|");
		rw.add("       ");
		
		TextBlock rx = new TextBlock();
		rx.add("       ");
		rx.add("       ");
		rx.add(" _   _ ");
		rx.add("( \\ / )");
		rx.add(" ) X ( ");
		rx.add("(_/ \\_)");
		rx.add("       ");
		
		TextBlock ry = new TextBlock();
		ry.add("       ");
		ry.add("       ");
		ry.add(" _   _ ");
		ry.add("| | | |");
		ry.add("| |_| |");
		ry.add(" \\__  |");
		ry.add("(____/ ");
		
		TextBlock rz = new TextBlock();
		rz.add("       ");
		rz.add("       ");
		rz.add(" _____ ");
		rz.add("(___  )");
		rz.add(" / __/ ");
		rz.add("(_____)");
		rz.add("       ");
		
		TextBlock r1 = new TextBlock();
		r1.add("  __ ");
		r1.add(" /  |");
		r1.add("/_/ |");
		r1.add("  | |");
		r1.add("  | |");
		r1.add("  |_|");
		r1.add("     ");
		
		TextBlock r2 = new TextBlock();
		r2.add(" ______  ");
		r2.add("(_____ \\ ");
		r2.add("  ____) )");
		r2.add(" /_____/ ");
		r2.add(" _______ ");
		r2.add("(_______)");
		r2.add("         ");
		
		TextBlock r3 = new TextBlock();
		r3.add(" ________");
		r3.add("(_______/");
		r3.add("   ____  ");
		r3.add("  (___ \\ ");
		r3.add(" _____) )");
		r3.add("(______/ ");
		r3.add("         ");
		
		TextBlock r4 = new TextBlock();
		r4.add("   __    ");
		r4.add("  / /    ");
		r4.add(" / /____ ");
		r4.add("|___   _)");
		r4.add("    | |  ");
		r4.add("    |_|  ");
		r4.add("         ");
		
		TextBlock r5 = new TextBlock();
		r5.add(" _______ ");
		r5.add("(_______)");
		r5.add(" ______  ");
		r5.add("(_____ \\ ");
		r5.add(" _____) )");
		r5.add("(______/ ");
		r5.add("         ");
		
		TextBlock r6 = new TextBlock();
		r6.add("    __  ");
		r6.add("   / /  ");
		r6.add("  / /_  ");
		r6.add(" / __ \\ ");
		r6.add("( (__) )");
		r6.add(" \\____/ ");
		r6.add("        ");
		
		TextBlock r7 = new TextBlock();
		r7.add(" _______ ");
		r7.add("(_______)");
		r7.add("      _  ");
		r7.add("     / ) ");
		r7.add("    / /  ");
		r7.add("   (_/   ");
		r7.add("         ");
		
		TextBlock r8 = new TextBlock();
		r8.add("  _____  ");
		r8.add(" / ___ \\ ");
		r8.add("( (   ) )");
		r8.add(" > > < < ");
		r8.add("( (___) )");
		r8.add(" \\_____/ ");
		r8.add("         ");
		
		TextBlock r9 = new TextBlock();
		r9.add("  ____  ");
		r9.add(" / __ \\ ");
		r9.add("( (__) )");
		r9.add(" \\__  / ");
		r9.add("   / /  ");
		r9.add("  /_/   ");
		r9.add("        ");
		
		TextBlock r0 = new TextBlock();
		r0.add("  ______ ");
		r0.add(" / __   |");
		r0.add("| | //| |");
		r0.add("| |// | |");
		r0.add("|  /__| |");
		r0.add(" \\_____/ ");
		r0.add("         ");
		
		TextBlock rPeriod = new TextBlock();
		rPeriod.add("   ");
		rPeriod.add("   ");
		rPeriod.add("   ");
		rPeriod.add("   ");
		rPeriod.add(" _ ");
		rPeriod.add("(_)");
		rPeriod.add("   ");
		
		TextBlock rComma = new TextBlock();
		rComma.add("   ");
		rComma.add("   ");
		rComma.add("   ");
		rComma.add("   ");
		rComma.add(" _ ");
		rComma.add("( )");
		rComma.add("|/ ");
		
		TextBlock rExclaim = new TextBlock();
		rExclaim.add(" _ ");
		rExclaim.add("| |");
		rExclaim.add("| |");
		rExclaim.add("|_|");
		rExclaim.add(" _ ");
		rExclaim.add("|_|");
		rExclaim.add("   ");
		
		TextBlock rQuestion = new TextBlock();
		rQuestion.add(" ____  ");
		rQuestion.add("(___ \\ ");
		rQuestion.add("    ) )");
		rQuestion.add("   /_/ ");
		rQuestion.add("   _   ");
		rQuestion.add("  (_)  ");
		rQuestion.add("       ");
		
		TextBlock rColon = new TextBlock();
		rColon.add("   ");
		rColon.add("   ");
		rColon.add(" _ ");
		rColon.add("(_)");
		rColon.add(" _ ");
		rColon.add("(_)");
		rColon.add("   ");
		
		TextBlock rSingleQuote = new TextBlock();
		rSingleQuote.add(" _ ");
		rSingleQuote.add("( )");
		rSingleQuote.add("|/ ");
		rSingleQuote.add("   ");
		rSingleQuote.add("   ");
		rSingleQuote.add("   ");
		rSingleQuote.add("   ");
		
		TextBlock rDoubleQuote = new TextBlock();
		rDoubleQuote.add(" _  _ ");
		rDoubleQuote.add("( )( )");
		rDoubleQuote.add("|/ |/ ");
		rDoubleQuote.add("      ");
		rDoubleQuote.add("      ");
		rDoubleQuote.add("      ");
		rDoubleQuote.add("      ");
		
		TextBlock rEquals = new TextBlock();
		rEquals.add("     ");
		rEquals.add(" ___ ");
		rEquals.add("(___)");
		rEquals.add(" ___ ");
		rEquals.add("(___)");
		rEquals.add("     ");
		rEquals.add("     ");
		
		TextBlock rPlus = new TextBlock();
		rPlus.add("       ");
		rPlus.add("   _   ");
		rPlus.add(" _| |_ ");
		rPlus.add("(_   _)");
		rPlus.add("  |_|  ");
		rPlus.add("       ");
		rPlus.add("       ");
		
		TextBlock rMinus = new TextBlock();
		rMinus.add("     ");
		rMinus.add("     ");
		rMinus.add(" ___ ");
		rMinus.add("(___)");
		rMinus.add("     ");
		rMinus.add("     ");
		rMinus.add("     ");
		
		TextBlock rAt = new TextBlock();
		rAt.add("          ");
		rAt.add("          ");
		rAt.add("          ");
		rAt.add("     _|_  ");
		rAt.add(" __   |   ");
		rAt.add("(_/|_/|_/ ");
		rAt.add("          ");
		
		TextBlock rPound = new TextBlock();
		rPound.add("   __  _   ");
		rPound.add(" _|  || |_ ");
		rPound.add("(_   ||  _)");
		rPound.add(" _|  || |_ ");
		rPound.add("(_   ||  _)");
		rPound.add("  |__||_|  ");
		rPound.add("           ");
		
		TextBlock rDollarSign = new TextBlock();
		rDollarSign.add("   _   ");
		rDollarSign.add(" _| |_ ");
		rDollarSign.add("|  ___)");
		rDollarSign.add("|___  |");
		rDollarSign.add("(_   _|");
		rDollarSign.add("  |_|  ");
		rDollarSign.add("       ");
		
		TextBlock rPercent = new TextBlock();
		rPercent.add(" _   _ ");
		rPercent.add("(_) | |");
		rPercent.add("   / / ");
		rPercent.add("  / /  ");
		rPercent.add(" / / _ ");
		rPercent.add("|_| (_)");
		rPercent.add("       ");
		
		TextBlock rCarrot = new TextBlock();
		rCarrot.add("    /\\  ");
		rCarrot.add("   //\\\\ ");
		rCarrot.add("  (____)");
		rCarrot.add("        ");
		rCarrot.add("        ");
		rCarrot.add("        ");
		rCarrot.add("        ");
		
		TextBlock rAmpersand = new TextBlock();
		rAmpersand.add("  ___   ");
		rAmpersand.add(" / _ \\  ");
		rAmpersand.add("( (_) ) ");
		rAmpersand.add(" ) _ (  ");
		rAmpersand.add("( (/  \\ ");
		rAmpersand.add(" \\__/\\_)");
		rAmpersand.add("        ");
		
		TextBlock rAsterisk = new TextBlock();
		rAsterisk.add(" _  _  _ ");
		rAsterisk.add("( \\| |/ )");
		rAsterisk.add(" \\  _  / ");
		rAsterisk.add("(_ (_) _)");
		rAsterisk.add(" /     \\ ");
		rAsterisk.add("(_/|_|\\_)");
		rAsterisk.add("         ");
		
		TextBlock rLeftPerenthesis = new TextBlock();
		rLeftPerenthesis.add("   __ ");
		rLeftPerenthesis.add("  / _)");
		rLeftPerenthesis.add(" / /  ");
		rLeftPerenthesis.add("( (   ");
		rLeftPerenthesis.add(" \\ \\_ ");
		rLeftPerenthesis.add("  \\__)");
		rLeftPerenthesis.add("      ");
		
		TextBlock rRightPerenthesis = new TextBlock();
		rRightPerenthesis.add(" __   ");
		rRightPerenthesis.add("(_ \\  ");
		rRightPerenthesis.add("  \\ \\ ");
		rRightPerenthesis.add("   ) )");
		rRightPerenthesis.add(" _/ / ");
		rRightPerenthesis.add("(__/  ");
		rRightPerenthesis.add("      ");
		
		TextBlock rUnderscore = new TextBlock();
		rUnderscore.add("		  ");
		rUnderscore.add("         ");
		rUnderscore.add("         ");
		rUnderscore.add("         ");
		rUnderscore.add(" _______ ");
		rUnderscore.add("(_______)");
		rUnderscore.add("         ");
		
		TextBlock rTilda = new TextBlock();
		rTilda.add("  __  _ ");
		rTilda.add(" /  \\/ )");
		rTilda.add("(_/\\__/ ");
		rTilda.add("        ");
		rTilda.add("        ");
		rTilda.add("        ");
		rTilda.add("        ");
		
		TextBlock rLeftSquareBracket = new TextBlock();
		rLeftSquareBracket.add(" ___ ");
		rLeftSquareBracket.add("|  _)");
		rLeftSquareBracket.add("| |  ");
		rLeftSquareBracket.add("| |  ");
		rLeftSquareBracket.add("| |_ ");
		rLeftSquareBracket.add("|___)");
		rLeftSquareBracket.add("     ");
		
		TextBlock rRightSquareBracket = new TextBlock();
		rRightSquareBracket.add(" ___ ");
		rRightSquareBracket.add("(_  |");
		rRightSquareBracket.add("  | |");
		rRightSquareBracket.add("  | |");
		rRightSquareBracket.add(" _| |");
		rRightSquareBracket.add("(___|");
		rRightSquareBracket.add("     ");
		
		TextBlock rLeftBrace = new TextBlock();
		rLeftBrace.add("       ");
		rLeftBrace.add("  _  _ ");
		rLeftBrace.add(" / )/ )");
		rLeftBrace.add("( (( ( ");
		rLeftBrace.add(" \\_)\\_)");
		rLeftBrace.add("       ");
		rLeftBrace.add("       ");
		
		TextBlock rRightBrace = new TextBlock();
		rRightBrace.add("       ");
		rRightBrace.add(" _  _  ");
		rRightBrace.add("( \\( \\ ");
		rRightBrace.add(" ) )) )");
		rRightBrace.add("(_/(_/ ");
		rRightBrace.add("       ");
		rRightBrace.add("       ");
		
		TextBlock rSlash = new TextBlock();
		rSlash.add("     _ ");
		rSlash.add("    | |");
		rSlash.add("   / / ");
		rSlash.add("  / /  ");
		rSlash.add(" / /   ");
		rSlash.add("|_|    ");
		rSlash.add("       ");
		
		TextBlock rBackSlash = new TextBlock();
		rBackSlash.add(" _     ");
		rBackSlash.add("| |    ");
		rBackSlash.add(" \\ \\   ");
		rBackSlash.add("  \\ \\  ");
		rBackSlash.add("   \\ \\ ");
		rBackSlash.add("    |_|");
		rBackSlash.add("       ");
		
		r.addBlock('A', rA);
		r.addBlock('B', rB);
		r.addBlock('C', rC);
		r.addBlock('D', rD);
		r.addBlock('E', rE);
		r.addBlock('F', rF);
		r.addBlock('G', rG);
		r.addBlock('H', rH);
		r.addBlock('I', rI);
		r.addBlock('J', rJ);
		r.addBlock('K', rK);
		r.addBlock('L', rL);
		r.addBlock('M', rM);
		r.addBlock('N', rN);
		r.addBlock('O', rO);
		r.addBlock('P', rP);
		r.addBlock('Q', rQ);
		r.addBlock('R', rR);
		r.addBlock('S', rS);
		r.addBlock('T', rT);
		r.addBlock('U', rU);
		r.addBlock('V', rV);
		r.addBlock('W', rW);
		r.addBlock('X', rX);
		r.addBlock('Y', rY);
		r.addBlock('Z', rZ);
		r.addBlock('a', ra);
		r.addBlock('b', rb);
		r.addBlock('c', rc);
		r.addBlock('d', rd);
		r.addBlock('e', re);
		r.addBlock('f', rf);
		r.addBlock('g', rg);
		r.addBlock('h', rh);
		r.addBlock('i', ri);
		r.addBlock('j', rj);
		r.addBlock('k', rk);
		r.addBlock('l', rl);
		r.addBlock('m', rm);
		r.addBlock('n', rn);
		r.addBlock('o', ro);
		r.addBlock('p', rp);
		r.addBlock('q', rq);
		r.addBlock('r', rr);
		r.addBlock('s', rs);
		r.addBlock('t', rt);
		r.addBlock('u', ru);
		r.addBlock('v', rv);
		r.addBlock('w', rw);
		r.addBlock('x', rx);
		r.addBlock('y', ry);
		r.addBlock('z', rz);
		r.addBlock(' ', rSpace);
		r.addBlock('1', r1);
		r.addBlock('2', r2);
		r.addBlock('3', r3);
		r.addBlock('4', r4);
		r.addBlock('5', r5);
		r.addBlock('6', r6);
		r.addBlock('7', r7);
		r.addBlock('8', r8);
		r.addBlock('9', r9);
		r.addBlock('0', r0);
		r.addBlock('.', rPeriod);
		r.addBlock(',', rComma);
		r.addBlock('!', rExclaim);
		r.addBlock('?', rQuestion);
		r.addBlock(':', rColon);
		r.addBlock('\'', rSingleQuote);
		r.addBlock('"', rDoubleQuote);
		r.addBlock('=', rEquals);
		r.addBlock('+', rPlus);
		r.addBlock('-', rMinus);
		r.addBlock('@', rAt);
		r.addBlock('#', rPound);
		r.addBlock('$', rDollarSign);
		r.addBlock('%', rPercent);
		r.addBlock('^', rCarrot);
		r.addBlock('&', rAmpersand);
		r.addBlock('*', rAsterisk);
		r.addBlock('(', rLeftPerenthesis);
		r.addBlock(')', rRightPerenthesis);
		r.addBlock('[', rLeftSquareBracket);
		r.addBlock(']', rRightSquareBracket);
		r.addBlock('<', rLeftBrace);
		r.addBlock('>', rRightBrace);
		r.addBlock('/', rSlash);
		r.addBlock('\\', rBackSlash);
		r.check();
	}
	
	public static void mw(String f, ChatColor... c)
	{
		if(c.length == 0)
		{
			for(String i : r.build(f))
			{
				m(i);
			}
			
			return;
		}
		
		for(String i : r.build(f, c))
		{
			m(i);
		}
	}
	
	public static String[] mv(String f, ChatColor... c)
	{
		GList<String> v = new GList<String>();
		
		if(c.length == 0)
		{
			for(String i : r.build(f))
			{
				v.add(i);
			}
			
			return v.toArray(new String[v.size()]);
		}
		
		for(String i : r.build(f, c))
		{
			v.add(i);
		}
		
		return v.toArray(new String[v.size()]);
	}
	
	public static String mk(String f, ChatColor... c)
	{
		GList<String> v = new GList<String>();
		
		if(c.length == 0)
		{
			for(String i : r.build(f))
			{
				v.add(i);
			}
			
			String r = "\n";
			
			for(String i : v)
			{
				r = r + i + "\n";
			}
			
			return r;
		}
		
		for(String i : r.build(f, c))
		{
			v.add(i);
		}
		
		String r = "\n";
		
		for(String i : v)
		{
			r = r + i + "\n";
		}
		
		return r;
	}
	
	public static void splash()
	{
		buildBlocks();
		String[] mv = mv("REACT", ChatColor.AQUA, ChatColor.DARK_GRAY, ChatColor.DARK_GRAY);
		String n = "\n";
		
		m(F.color("" + n + "" + n + "&b                 /|                    " + n + "&b               //&8..                    " + n + "&b             //&8....                    " + n + "&b           //&8......                    " + n + "&b         //&8........                    " + n + "&b       //&8..........                    " + n + "&b     //&8............            &b|       " + n + "&b     |&8:::::........          ::&b|       " + mv[0] + n + "&b     |&8::::::::::...       :::::&b|       " + mv[1] + n + "&b     |&8:::::::::::::  ::::::::::&b|       " + mv[2] + n + "&b     |&8:::::::::::  ...:::::::::&b|       " + mv[3] + n + "&b     |&8::::::       ........::::&b|       " + mv[4] + n + "&b     |&8::           ............&b|       " + mv[5] + n + "&b     |             &8..........&b//        " + mv[6] + n + "&b                   &8........&b//          " + n + "&b                   &8......&b//            " + n + "&b                   &8....&b//              " + n + "&b                   &8..&b//                " + n + "&b                   |/                  " + n + "" + n + ""));
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
		React.instance().setTag();
	}
}
