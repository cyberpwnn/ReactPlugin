package org.cyberpwn.react.controller;

import java.util.Date;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.api.ReactAPI;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.json.RawText;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.network.ReactServer;
import org.cyberpwn.react.nms.NMS;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.CPUTest;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.GPage;
import org.cyberpwn.react.util.Gui;
import org.cyberpwn.react.util.Gui.Pane;
import org.cyberpwn.react.util.Gui.Pane.Element;
import org.cyberpwn.react.util.Verbose;

public class CommandController extends Controller implements CommandExecutor
{
	private GBook bookAbout;
	private GBook bookReactions;
	private GBook bookSamplers;
	private GBook bookMonitoring;
	
	public CommandController(final React react)
	{
		super(react);
		
		react.getCommand(Info.COMMAND).setExecutor(this);
		
		react.scheduleSyncTask(0, new Runnable()
		{
			@Override
			public void run()
			{
				bookReactions = new GBook(ChatColor.GREEN + L.REACTIONS);
				
				for(Actionable i : react.getActionController().getActions().keySet())
				{
					bookReactions.addPage(new GPage().put(i.getName(), i.getDescription()));
				}
				
				bookSamplers = new GBook(ChatColor.GREEN + L.SAMPLERS);
				
				for(Samplable i : react.getSampleController().getSamples().keySet())
				{
					bookSamplers.addPage(new GPage().put(i.getName() + "\n" + ChatColor.RESET + ChatColor.DARK_GREEN + ChatColor.stripColor(i.formatted()), i.getExplaination()));
				}
				
				bookMonitoring = new GBook(ChatColor.GREEN + L.MONITORING);
				bookMonitoring.addPage(new GPage().put(L.BOOK_MONITOR_TITLE, L.BOOK_MONITOR_TEXT));
				bookMonitoring.addPage(new GPage().put(L.BOOK_TABS_TITLE, L.BOOK_TABS_TEXT));
				bookMonitoring.addPage(new GPage().put(L.BOOK_PERFORMANCE_TITLE, L.BOOK_PERFORMANCE_TEXT));
				bookMonitoring.addPage(new GPage().put(L.BOOK_PERSISTANCE_TITLE, L.BOOK_PERSISTANCE_TEXT));
				
				bookAbout = new GBook(ChatColor.GREEN + "About React");
				bookAbout.addPage(new GPage().put(L.BOOK_GREETING, L.BOOK_GREETING_TEXT));
				bookAbout.addPage(new GPage().put(L.BOOK_SAMPLES_TITLE, L.BOOK_SAMPLES_TEXT));
				bookAbout.addPage(new GPage().put(L.BOOK_REACTIONS_TITLE, L.BOOK_REACTIONS_TEXT));
				bookAbout.addPage(new GPage().put(L.BOOK_TRIALERROR_TITLE, L.BOOK_TRIALERROR_TEXT));
				bookAbout.addPage(new GPage().put(L.BOOK_MONITORING_TITLE, L.BOOK_MONITORING_TEXT));
				bookAbout.addPage(new GPage().put(L.BOOK_CONFIGURATION_TITLE, L.BOOK_CONFIGURATION_TEXT));
			}
		});
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void msg(CommandSender sender, String msg)
	{
		sender.sendMessage(msg);
	}
	
	public boolean onCommand(final CommandSender sender, Command cmd, String name, String[] args)
	{
		boolean isPlayer = (sender instanceof Player);
		Player p = isPlayer ? (Player) sender : null;
		int len = args.length;
		String sub = len > 0 ? args[0] : "";
		
		if(cmd.getName().equalsIgnoreCase(Info.COMMAND))
		{
			if(React.isMef())
			{
				sender.sendMessage(ChatColor.RED + "I'm sorry. I cant help you if you won't do the same.");
				return true;
			}
			
			if(!sender.hasPermission(Info.PERM_ACT) && !sender.hasPermission(Info.PERM_MONITOR) && !sender.hasPermission(Info.PERM_RELOAD))
			{
				sender.sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
				return true;
			}
			
			if(len == 0)
			{
				msg(sender, Info.CH_ALL);
			}
			
			else
			{
				
				 ////////////////////////////////////////////////////////////////////
				//////////////////////    TODO COMMANDS     //////////////////////////
				 ////////////////////////////////////////////////////////////////////
				if(sub.equalsIgnoreCase("reload") || sub.equalsIgnoreCase("restart"))
				{
					react.onReload(sender);
				}
				
				else if(sub.equalsIgnoreCase("update") || sub.equalsIgnoreCase("u"))
				{
					React.instance().update(sender);
				}
				
				else if(sub.equalsIgnoreCase("ping") || sub.equalsIgnoreCase("pong"))
				{
					sender.sendMessage(String.format(Info.HRN, "Pong"));
					
					int highest = Integer.MIN_VALUE;
					int lowest = Integer.MAX_VALUE;
					String nh = "";
					String nl = "";
					
					for(Player i : getReact().onlinePlayers())
					{
						try
						{
							int ping = NMS.instance().ping(i);
							
							if(ping > highest)
							{
								highest = ping;
								nh = i.getName();
							}
							
							if(ping < lowest)
							{
								lowest = ping;
								nl = i.getName();
							}
						}
						
						catch(Exception e)
						{
							sender.sendMessage(ChatColor.RED + "Failed to Ping... Unknown Version?");
							break;
						}
					}
					
					if(!nh.equals(""))
					{
						sender.sendMessage(Info.TAG + ChatColor.RED + "Highest: " + ChatColor.GOLD + nh + " (" + highest + "ms)");
					}
					
					if(!nl.equals(""))
					{
						sender.sendMessage(Info.TAG + ChatColor.GREEN + "Lowest: " + ChatColor.AQUA + nl + " (" + lowest + "ms)");
					}
					
					try
					{
						sender.sendMessage(Info.TAG + ChatColor.AQUA + "Yours: " + ChatColor.LIGHT_PURPLE + NMS.instance().ping((Player) sender) + "ms");
					}
					
					catch(Exception e)
					{
						
					}
					
					sender.sendMessage(Info.HR);
				}
				
				else if(sub.equalsIgnoreCase("timings") || sub.equalsIgnoreCase("t"))
				{
					if(isPlayer)
					{
						if(args.length == 2)
						{
							if(react.getTimingsController().getAll() == null)
							{
								sender.sendMessage(Info.TAG + ChatColor.RED + "Data has not been prepared yet. Please wait up to 5 minutes.");
								return true;
							}
							
							GBook book = react.getTimingsController().getAll();
							book.filterTiming(args[1]);
							ItemStack is = book.toBook();
							p.getInventory().remove(is);
							p.getInventory().addItem(is);
							p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_BOOK + " [" + args[1] + "]");
						}
						
						else
						{
							if(react.getTimingsController().getAll() == null)
							{
								sender.sendMessage(Info.TAG + ChatColor.RED + "Data has not been prepared yet. Please wait up to 5 minutes.");
								return true;
							}
							
							ItemStack is = react.getTimingsController().getAll().toBook();
							p.getInventory().remove(is);
							p.getInventory().addItem(is);
							p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_BOOK);
						}
					}
					
					else
					{
						
					}
				}
				
				else if(sub.equalsIgnoreCase("dump") || sub.equalsIgnoreCase("d"))
				{
					React.dump();
					Date d = new Date();
					@SuppressWarnings("deprecation")
					String nme = "React/dumps/" + (d.getHours() + 1) + "-" + (d.getMinutes() + 1) + "-" + (d.getSeconds() + 1) + "-" + d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getYear() + ".yml";
					sender.sendMessage(Info.TAG + L.MESSAGE_DUMPED + nme);
				}
				
				else if(sub.equalsIgnoreCase("monitor") || sub.equalsIgnoreCase("mon"))
				{
					if(args.length == 2)
					{
						if(args[1].equalsIgnoreCase("-c"))
						{
							sender.sendMessage(react.getMonitorController().getMs().getRoot());
							return true;
						}
						
						if(args[1].equalsIgnoreCase("-v"))
						{
							react.getMonitorController().toggleDisp(p);
							return true;
						}
						
						if(args[1].equalsIgnoreCase("-lock"))
						{
							if(react.getMonitorController().isMonitoring(p))
							{
								react.getMonitorController().lock(p);
							}
							
							else
							{
								p.sendMessage(L.MESSAGE_MONITOR_LOCK_FAIL);
							}
							
							return true;
						}
					}
					
					if(isPlayer)
					{
						react.getMonitorController().toggleMonitoring(p);
					}
					
					else
					{
						sender.sendMessage(react.getMonitorController().getMs().getRoot());
					}
				}
				
				else if(sub.equalsIgnoreCase("verbose") || sub.equalsIgnoreCase("vb"))
				{
					if(isPlayer)
					{
						if(Verbose.mrx.contains(p))
						{
							Verbose.mrx.remove(p);
							p.sendMessage(ChatColor.RED + L.MESSAGE_VERBOSEOFF);
						}
						
						else
						{
							Verbose.mrx.add(p);
							p.sendMessage(ChatColor.GREEN + L.MESSAGE_VERBOSEON);
						}
					}
					
					else
					{
						React.setVerbose(!React.isVerbose());
						sender.sendMessage(L.MESSAGE_VERBOSE + React.isVerbose());
					}
				}
				
				else if(sub.equalsIgnoreCase("cpu-score") || sub.equalsIgnoreCase("cs"))
				{
					sender.sendMessage(Info.TAG + ChatColor.BOLD + ChatColor.GOLD + "CPU Score: " + ChatColor.RESET + ChatColor.GREEN + F.f(CPUTest.singleThreaded(50)));
				}
				
				else if(sub.equalsIgnoreCase("client") || sub.equalsIgnoreCase("net"))
				{
					sender.sendMessage(String.format(Info.HRN, "React Server"));
					
					if(!React.instance().getConfiguration().getBoolean("react-remote.enable"))
					{
						sender.sendMessage(Info.TAG + ChatColor.GOLD + "(!) React server is disabled.");
					}
					
					sender.sendMessage(Info.TAG + ChatColor.AQUA + "Requests: " + ChatColor.GREEN + F.f(ReactServer.requests));
					sender.sendMessage(Info.HR);
				}
				
				else if(sub.equalsIgnoreCase("servers") || sub.equalsIgnoreCase("list"))
				{
					if(getReact().getBungeeController().getConfiguration().getBoolean("support-bungeecord"))
					{
						if(getReact().getBungeeController().getName().equals("1337"))
						{
							sender.sendMessage(ChatColor.RED + L.MESSAGE_NO_DATA + ChatColor.GOLD + " If you are on a network, give react at least one minute after startup to talk to the other servers.");
						}
						
						else
						{
							GMap<String, JSONObject> da = getReact().getBungeeController().getData();
							sender.sendMessage(Info.TAG + ChatColor.BOLD + ChatColor.AQUA + L.MESSAGE_SERVERCOUNT_A + (da.size() + 1) + L.MESSAGE_SERVERCOUNT_B);
							sender.sendMessage(Info.TAG + ChatColor.AQUA + L.MESSAGE_SERVERCURRENT + getReact().getBungeeController().getName());
							
							for(String i : da.keySet())
							{
								sender.sendMessage(ChatColor.LIGHT_PURPLE + i + ChatColor.RESET + getReact().getMonitorController().fromServer(i));
							}
						}
					}
					
					else
					{
						sender.sendMessage(L.MESSAGE_BUNGEEOFF);
					}
				}
				
				else if(sub.equalsIgnoreCase("query") || sub.equalsIgnoreCase("q"))
				{
					if(args.length == 2)
					{
						GPage pg = react.getActionController().getActionInstabilityCause().queryPlugin(args[1]);
						GBook book = new GBook(ChatColor.AQUA + L.MESSAGE_QUERYRESULT).addPage(pg);
						
						if(book == null)
						{
							msg(sender, ChatColor.RED + L.MESSAGE_ERROR_PLUGINUNKNOWN);
						}
						
						else
						{
							if(isPlayer)
							{
								p.getInventory().addItem(book.toBook());
							}
							
							else
							{
								sender.sendMessage(book.toString());
							}
						}
					}
					
					else
					{
						GBook book = new GBook(ChatColor.AQUA + L.MESSAGE_QUERYRESULT);
						
						for(Plugin i : react.getServer().getPluginManager().getPlugins())
						{
							book.addPage(react.getActionController().getActionInstabilityCause().queryPlugin(i.getName()));
						}
						
						if(isPlayer)
						{
							p.getInventory().addItem(book.toBook());
						}
						
						else
						{
							sender.sendMessage(book.toString());
						}
					}
				}
				
				else if(sub.equalsIgnoreCase("plugins") || sub.equalsIgnoreCase("p"))
				{
					if(args.length == 2)
					{
						for(Plugin i : Bukkit.getPluginManager().getPlugins())
						{
							if(i.getName().toLowerCase().contains(args[1].toLowerCase()))
							{
								getReact().getPluginWeightController().report(sender, i);
								return true;
							}
						}
						
						sender.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_ERROR_PLUGINUNKNOWN);
					}
					
					else
					{
						getReact().getPluginWeightController().report(sender);
					}
				}
				
				else if(sub.equalsIgnoreCase("map") || sub.equalsIgnoreCase("graph"))
				{
					if(isPlayer)
					{
						react.getMonitorController().toggleMapping(p);
					}
					
					else
					{
						msg(sender, L.MESSAGE_PLAYER_ONLY);
					}
				}
				
				else if(sub.equalsIgnoreCase("status") || sub.equalsIgnoreCase("book"))
				{
					if(args.length == 2)
					{
						if(args[1].equalsIgnoreCase("-c"))
						{
							sender.sendMessage(react.getActionController().getActionInstabilityCause().query().toString());
							return true;
						}
					}
					
					if(isPlayer)
					{
						p.getInventory().remove(react.getActionController().getActionInstabilityCause().query().toBook());
						p.getInventory().addItem(react.getActionController().getActionInstabilityCause().query().toBook());
						p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_BOOK);
					}
					
					else
					{
						sender.sendMessage(react.getActionController().getActionInstabilityCause().query().toString());
					}
				}
				
				else if(sub.equalsIgnoreCase("guess") || sub.equalsIgnoreCase("g"))
				{
					if(args.length >= 2)
					{
						if(args[1].equalsIgnoreCase("-c"))
						{
							sender.sendMessage(react.getActionController().getActionInstabilityCause().queryGuess().toString());
							return true;
						}
						
						if(args[1].equalsIgnoreCase("-m"))
						{
							if(args.length == 3)
							{
								try
								{
									sender.sendMessage(react.getActionController().getActionInstabilityCause().queryGuess(Long.valueOf(args[2]).longValue()));
									return true;
								}
								
								catch(Exception e)
								{
									sender.sendMessage(ChatColor.RED + L.MESSAGE_ERROR_NONUMBER + args[3]);
								}
							}
							
							else
							{
								sender.sendMessage(ChatColor.RED + L.MESSAGE_HELP_GUESS);
							}
						}
					}
					
					if(isPlayer)
					{
						p.getInventory().remove(react.getActionController().getActionInstabilityCause().queryGuess().toBook());
						p.getInventory().addItem(react.getActionController().getActionInstabilityCause().queryGuess().toBook());
						p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_BOOK);
					}
					
					else
					{
						sender.sendMessage(react.getActionController().getActionInstabilityCause().query().toString());
					}
				}
				
				else if(sub.equalsIgnoreCase("version") || sub.equalsIgnoreCase("v"))
				{
					React.instance().checkVersion(sender);
				}
				
				else if(sub.equalsIgnoreCase("help"))
				{
					if(sender instanceof Player)
					{
						if(args.length == 1)
						{
							sender.sendMessage(String.format(Info.HRN, "React"));
							sender.sendMessage(Info.COLOR_B + L.MESSAGE_BOOK_CLICK);
							RawText rt = new RawText();
							rt.addTextWithHoverCommand("About\n", RawText.COLOR_GREEN, L.MESSAGE_HELP_ABOUT_A, L.MESSAGE_HELP_ABOUT_B, RawText.COLOR_AQUA);
							rt.addTextWithHoverCommand("Monitoring\n", RawText.COLOR_GREEN, L.MESSAGE_HELP_MONITORING_A, L.MESSAGE_HELP_MONITORING_B, RawText.COLOR_AQUA);
							rt.addTextWithHoverCommand("Reactions\n", RawText.COLOR_GREEN, L.MESSAGE_HELP_REACTIONS_A, L.MESSAGE_HELP_REACTIONS_B, RawText.COLOR_AQUA);
							rt.addTextWithHoverCommand("Samplers\n", RawText.COLOR_GREEN, L.MESSAGE_HELP_SAMPLERS_A, L.MESSAGE_HELP_SAMPLERS_B, RawText.COLOR_AQUA);
							rt.tellRawTo(react, (Player) sender);
							sender.sendMessage(Info.HR);
						}
						
						else if(args.length == 2)
						{
							if(args[1].equalsIgnoreCase("about"))
							{
								((Player) sender).getInventory().remove(bookAbout.toBook());
								((Player) sender).getInventory().addItem(bookAbout.toBook());
							}
							
							else if(args[1].equalsIgnoreCase("monitoring"))
							{
								((Player) sender).getInventory().remove(bookMonitoring.toBook());
								((Player) sender).getInventory().addItem(bookMonitoring.toBook());
							}
							
							else if(args[1].equalsIgnoreCase("reactions"))
							{
								((Player) sender).getInventory().remove(bookReactions.toBook());
								((Player) sender).getInventory().addItem(bookReactions.toBook());
							}
							
							else if(args[1].equalsIgnoreCase("samplers"))
							{
								((Player) sender).getInventory().remove(bookSamplers.toBook());
								((Player) sender).getInventory().addItem(bookSamplers.toBook());
							}
							
							else
							{
								msg(sender, ChatColor.RED + L.MESSAGE_UNKNOWN_BOOK);
							}
						}
					}
					
					else
					{
						sender.sendMessage(L.MESSAGE_PLAYER_ONLY);
					}
				}
				
				else if(sub.equalsIgnoreCase("act"))
				{
					if(!sender.hasPermission(Info.PERM_ACT))
					{
						sender.sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
						return true;
					}
					
					if(args.length == 2)
					{
						for(final Actionable i : react.getActionController().getActions().k())
						{
							if(i.isManual())
							{
								if(args[1].equalsIgnoreCase(i.getKey()))
								{
									i.manual(sender);
									return true;
								}
							}
						}
						
						sender.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_UNKNOWN_ACTION);
						for(final Actionable i : react.getActionController().getActions().k())
						{
							if(i.isManual())
							{
								sender.sendMessage(ChatColor.AQUA + "/re act " + i.getKey() + ChatColor.GREEN + " - " + i.getDescription());
							}
						}
					}
					
					else
					{
						if(sender instanceof Player)
						{
							final Gui ui = new Gui((Player) sender, react);
							final Pane pane = ui.new Pane(L.GUI_ACTIONS);
							Integer x = 0;
							
							for(final Actionable i : react.getActionController().getActions().k())
							{
								if(i.isManual())
								{
									Element e = pane.new Element(ChatColor.AQUA + i.getName(), i.getMaterial(), x);
									
									for(String j : WordUtils.wrap(i.getDescription(), 32).split("\n"))
									{
										e.addInfo(j.trim());
									}
									
									e.addRequirement("/re act " + i.getKey());
									
									e.setQuickRunnable(new Runnable()
									{
										@Override
										public void run()
										{
											ui.close();
											i.manual(sender);
										}
									});
									
									x++;
								}
							}
							
							pane.setDefault();
							ui.show();
						}
						
						else
						{
							sender.sendMessage(L.MESSAGE_PLAYER_ONLY);
							for(final Actionable i : react.getActionController().getActions().k())
							{
								if(i.isManual())
								{
									sender.sendMessage(ChatColor.AQUA + "/re act " + i.getKey() + ChatColor.GREEN + " - " + i.getDescription());
								}
							}
						}
					}
				}
				
				else
				{
					sender.sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_ERROR_NOTCOMMAND);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	public void onDrop(final PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType().equals(Material.WRITTEN_BOOK) && e.getPlayer().getItemInHand().hasItemMeta())
			{
				BookMeta bm = (BookMeta) e.getPlayer().getItemInHand().getItemMeta();
				final ItemStack is = e.getPlayer().getItemInHand();
				
				if(bm.getAuthor().equals(Info.NAME))
				{
					react.scheduleSyncTask(1, new Runnable()
					{
						@Override
						public void run()
						{
							e.getPlayer().getInventory().remove(is);
						}
					});
				}
			}
		}
	}
	
	@EventHandler
	public void onCommandPre(PlayerCommandPreprocessEvent e)
	{
		if(e.getMessage().equalsIgnoreCase("/mem") || e.getMessage().equalsIgnoreCase("/memory"))
		{
			if(React.isAllowMem())
			{
				e.setCancelled(true);
				
				Player p = e.getPlayer();
				
				if(e.getPlayer().hasPermission(Info.PERM_MONITOR))
				{
					long d = ((long) ReactAPI.getMemoryUsed()) - (getReact().getSampleController().getSampleChunkMemory().getValue().getLong() + (getReact().getSampleController().getSampleMemoryPerPlayer().getValue().getLong() * getReact().onlinePlayers().length));
					
					if(d < 0)
					{
						d = ((long) ReactAPI.getMemoryUsed()) - (getReact().getSampleController().getSampleChunkMemory().getValue().getLong());
					}
					
					if(d < 0)
					{
						d = 0;
					}
					
					p.sendMessage(String.format(Info.HRN, "Memory"));
					p.sendMessage(Info.TAG + ChatColor.AQUA + L.MESSAGE_MEMORY_MAX + ChatColor.GOLD + F.mem((long) (ReactAPI.getMemoryMax() / 1024 / 1024)));
					p.sendMessage(Info.TAG + ChatColor.AQUA + L.MESSAGE_MEMORY_USED + ChatColor.GOLD + F.mem((long) (ReactAPI.getMemoryUsed())) + " (" + F.pc(getReact().getSampleController().getSampleMemoryUsed().getPercent(), 0) + ")");
					p.sendMessage(Info.TAG + ChatColor.AQUA + L.MESSAGE_GARBAGE + ChatColor.GOLD + F.mem((long) (ReactAPI.getMemoryGarbage())));
					p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_PLAYERS + ChatColor.GOLD + F.mem(getReact().getSampleController().getSampleMemoryPerPlayer().getValue().getLong() * getReact().onlinePlayers().length));
					p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_CHUNKS + ChatColor.GOLD + F.mem(getReact().getSampleController().getSampleChunkMemory().getValue().getLong()));
					p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_PLUGINS + ChatColor.GOLD + F.mem(d));
					p.sendMessage(Info.TAG + ChatColor.GOLD + L.MESSAGE_UPTIME + ChatColor.YELLOW + getReact().getUptime().toString());
					p.sendMessage(Info.HR);
				}
				
				else
				{
					e.getPlayer().sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
				}
			}
		}
		
		if(e.getMessage().equalsIgnoreCase("/tps"))
		{
			if(React.isAllowMem())
			{
				Player p = e.getPlayer();
				
				if(e.getPlayer().hasPermission("bukkit.command.tps"))
				{
					p.sendMessage(Info.TAG + ChatColor.AQUA + "Current TPS (Exact): " + ChatColor.GREEN + F.f(ReactAPI.getSampleTicksPerSecond(), 9));
				}
				
				else
				{
					e.getPlayer().sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
				}
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		if(e.getItemDrop().getItemStack() != null && e.getItemDrop().getItemStack().getType().equals(Material.WRITTEN_BOOK) && e.getItemDrop().getItemStack().hasItemMeta())
		{
			BookMeta bm = (BookMeta) e.getItemDrop().getItemStack().getItemMeta();
			
			if(bm.getAuthor().equals(Info.NAME))
			{
				E.r(e.getItemDrop());
			}
		}
	}
}
