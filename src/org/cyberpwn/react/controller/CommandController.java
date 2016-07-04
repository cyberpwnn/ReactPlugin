package org.cyberpwn.react.controller;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
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
import org.cyberpwn.react.cluster.Cluster;
import org.cyberpwn.react.cluster.ClusterBoolean;
import org.cyberpwn.react.cluster.ClusterConfig.ClusterDataType;
import org.cyberpwn.react.cluster.ClusterDouble;
import org.cyberpwn.react.cluster.ClusterInteger;
import org.cyberpwn.react.cluster.ClusterString;
import org.cyberpwn.react.json.RawText;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.network.ReactServer;
import org.cyberpwn.react.nms.NMS;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.CPUTest;
import org.cyberpwn.react.util.CommandRunnable;
import org.cyberpwn.react.util.Configurator;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GPage;
import org.cyberpwn.react.util.Gui;
import org.cyberpwn.react.util.Gui.Pane;
import org.cyberpwn.react.util.Gui.Pane.Element;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.PlayerData;
import org.cyberpwn.react.util.ReactCommand;
import org.cyberpwn.react.util.Verbose;

public class CommandController extends Controller implements CommandExecutor
{
	private GBook bookAbout;
	private GBook bookReactions;
	private GBook bookSamplers;
	private GBook bookMonitoring;
	private GList<ReactCommand> commands;
	private GList<GList<ReactCommand>> tabulations;
	
	public CommandController(final React react)
	{
		super(react);
		
		commands = new GList<ReactCommand>();
		tabulations = new GList<GList<ReactCommand>>();
	}
	
	public void start()
	{
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
					bookSamplers.addPage(new GPage().put(i.getName() + "\n" + ChatColor.RESET + ChatColor.DARK_GREEN + ChatColor.stripColor(i.formatted(false)), i.getExplaination()));
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
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				final CommandSender sender = getSender();
				String[] args = getArgs();
				
				if(!sender.hasPermission(Info.PERM_ACT))
				{
					sender.sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
					return;
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
								return;
							}
						}
					}
					
					sender.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_UNKNOWN_ACTION);
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
		}, L.COMMAND_ACT, "act", "action", "a"));
					
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				String[] args = getArgs();
				
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
							msg(sender, Info.COLOR_ERR + L.MESSAGE_UNKNOWN_BOOK);
						}
					}
				}
						
				else
				{
					sender.sendMessage(L.MESSAGE_PLAYER_ONLY);
				}
			}
		}, L.COMMAND_HELP, "help", "h"));
				
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				React.instance().getUpdateController().checkVersion(sender);
			}
		}, L.COMMAND_VERSION, "version", "v"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				sender.sendMessage(Info.TAG + ChatColor.BLUE + "Requested to relight " + getReact().getPhotonController().relightAll() + " chunks.");
			}
		}, "Requests all chunks be relighted.", "relightall", "pb", "light"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				new Configurator(getPlayer());
			}
		}, "Configure React ingame via inventory gui's", "configure", "config", "conf"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				if(isPlayer())
				{
					RawText a = new RawText();
					RawText g = new RawText();
					Player p = getPlayer();
					GList<String> allowed = new GList<String>(InstabilityCause.ign());
					GList<String> ignored = getReact().getPlayerController().gpd(getPlayer()).getIgnored();
					allowed.remove(ignored);
					
					for(int i = 0; i < 14; i++)
					{
						p.sendMessage(" ");
					}
					
					p.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + "Hover & Click to interact.");
					
					if(getArgs().length == 2)
					{
						String tg = getArgs()[1];
						
						if(allowed.contains(tg))
						{
							PlayerData pd = getReact().getPlayerController().gpd(getPlayer());
							pd.getIgnored().add(tg);
							getReact().getPlayerController().spd(p, pd);
							getReact().getPlayerController().save(p);
							allowed = new GList<String>(InstabilityCause.ign());
							ignored = getReact().getPlayerController().gpd(getPlayer()).getIgnored();
							allowed.remove(ignored);
						}
						
						else if(ignored.contains(tg))
						{
							PlayerData pd = getReact().getPlayerController().gpd(getPlayer());
							pd.getIgnored().remove(tg);
							getReact().getPlayerController().spd(p, pd);
							getReact().getPlayerController().save(p);
							allowed = new GList<String>(InstabilityCause.ign());
							ignored = getReact().getPlayerController().gpd(getPlayer()).getIgnored();
							allowed.remove(ignored);
						}
					}
					
					for(String i : allowed)
					{
						a.addTextWithHoverCommand(i, RawText.COLOR_GREEN, "/re ignore " + i, "Click to ignore " + InstabilityCause.valueOf(i).getName(), RawText.COLOR_RED);
						a.addText("  ");
					}
					
					for(String i : ignored)
					{
						g.addTextWithHoverCommand(i, RawText.COLOR_RED, "/re ignore " + i, "Click to allow " + InstabilityCause.valueOf(i).getName(), RawText.COLOR_GREEN);
						g.addText("  ");
					}
					
					p.sendMessage(String.format(Info.HRN, "Allowed"));
					
					if(allowed.isEmpty())
					{
						p.sendMessage(Info.TAG + ChatColor.GREEN + "No allowed instability messages.");
					}
					
					else
					{
						a.tellRawTo(getReact(), p);
					}
					
					p.sendMessage(String.format(Info.HRN, "Ignored"));
					
					if(ignored.isEmpty())
					{
						p.sendMessage(Info.TAG + ChatColor.RED + "No ignored instability messages.");
					}
					
					else
					{
						g.tellRawTo(getReact(), p);
					}
					
					p.sendMessage(Info.HR);
				}
				
				else
				{
					getSender().sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_PLAYER_ONLY);
				}
			}
		}, "Ignore instability messages", "ignore", "ign"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				String[] args = getArgs();
				
				if(args.length < 3)
				{
					sender.sendMessage(Info.TAG + ChatColor.AQUA + "/re cfs [path] [params...]");
					sender.sendMessage(Info.TAG + ChatColor.AQUA + "/re cfs [/,nm] [-list,-get,-set:val,-reset]");
					sender.sendMessage(Info.TAG + ChatColor.AQUA + "/re cfs /root/ -list");
				}
				
				else if(args[1].equalsIgnoreCase("/root/"))
				{
					if(args[2].equalsIgnoreCase("-list") || args[2].equalsIgnoreCase("-get"))
					{
						for(File i : getReact().getConfigurationController().getConfigurations().k())
						{
							sender.sendMessage(ChatColor.GREEN + "$: " + getReact().getConfigurationController().getConfigurations().get(i).getCodeName());
						}
					}
					
					if(args[2].equalsIgnoreCase("-reset"))
					{
						for(File i : getReact().getConfigurationController().getConfigurations().k())
						{
							getReact().getConfigurationController().getConfigurations().get(i).onNewConfig();
							getReact().getDataController().saveFileConfig(i, getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().toYaml(), getReact().getConfigurationController().getConfigurations().get(i));
							sender.sendMessage(ChatColor.GREEN + "$: " + getReact().getConfigurationController().getConfigurations().get(i).getCodeName() + " RESET");
						}
						
						getReact().onReload(sender);
					}
				}
				
				else
				{
					String r = args[1];
					
					if(args[1].contains("/"))
					{
						r = args[1].split("/")[0];
						String k = args[1].split("/")[1];
						
						if(args[2].equalsIgnoreCase("-list") || args[2].equalsIgnoreCase("-get"))
						{
							for(File i : getReact().getConfigurationController().getConfigurations().k())
							{
								if(getReact().getConfigurationController().getConfigurations().get(i).getCodeName().equalsIgnoreCase(r))
								{
									for(String j : getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().k())
									{
										if(j.equalsIgnoreCase(k))
										{
											sender.sendMessage(ChatColor.GREEN + "$: " + getReact().getConfigurationController().getConfigurations().get(i).getCodeName() + "/" + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().get(j).getKey() + ": " + ChatColor.YELLOW + "(" + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().get(j).getType().toString() + ") " + ChatColor.AQUA + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getAbstract(j));
											break;
										}
									}
									
									break;
								}
							}
						}
						
						if(args[2].toLowerCase().startsWith("-set:"))
						{
							String value = args[2].substring(5);
							
							for(File i : getReact().getConfigurationController().getConfigurations().k())
							{
								if(getReact().getConfigurationController().getConfigurations().get(i).getCodeName().equalsIgnoreCase(r))
								{
									for(String j : getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().k())
									{
										if(j.equalsIgnoreCase(k))
										{
											Cluster c = getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().get(j);
											
											try
											{
												if(c.getType().equals(ClusterDataType.BOOLEAN))
												{
													getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().put(j, new ClusterBoolean(j, Boolean.valueOf(value)));
													getReact().getDataController().saveFileConfig(i, getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().toYaml(), getReact().getConfigurationController().getConfigurations().get(i));
													getReact().onReload(sender);
												}
												
												else if(c.getType().equals(ClusterDataType.INTEGER))
												{
													getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().put(j, new ClusterInteger(j, Integer.valueOf(value)));
													getReact().getDataController().saveFileConfig(i, getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().toYaml(), getReact().getConfigurationController().getConfigurations().get(i));
													getReact().onReload(sender);
												}
												
												else if(c.getType().equals(ClusterDataType.STRING))
												{
													getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().put(j, new ClusterString(j, value));
													getReact().getDataController().saveFileConfig(i, getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().toYaml(), getReact().getConfigurationController().getConfigurations().get(i));
													getReact().onReload(sender);
												}
												
												else if(c.getType().equals(ClusterDataType.DOUBLE))
												{
													getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().put(j, new ClusterDouble(j, Double.valueOf(value)));
													getReact().getDataController().saveFileConfig(i, getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().toYaml(), getReact().getConfigurationController().getConfigurations().get(i));
													getReact().onReload(sender);
												}
												
												else
												{
													sender.sendMessage(ChatColor.GREEN + "$ Unsupported");
												}
											}
												
											catch(Exception e)
											{
												sender.sendMessage(ChatColor.GREEN + "$ Failed. Unsupported Data For the specified data type.");
											}
											
											sender.sendMessage(ChatColor.GREEN + "$: " + getReact().getConfigurationController().getConfigurations().get(i).getCodeName() + "/" + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().get(j).getKey() + ": " + ChatColor.YELLOW + "(" + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().get(j).getType().toString() + ") " + ChatColor.AQUA + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getAbstract(j));
											break;
										}
									}
									
									break;
								}
							}
						}
						
						if(args[2].equalsIgnoreCase("-reset"))
						{
							sender.sendMessage(ChatColor.GREEN + "$ Unsupported");
						}
					}
											
					else
					{
						if(args[2].equalsIgnoreCase("-list") || args[2].equalsIgnoreCase("-get"))
						{
							for(File i : getReact().getConfigurationController().getConfigurations().k())
							{
								if(getReact().getConfigurationController().getConfigurations().get(i).getCodeName().equalsIgnoreCase(r))
								{
									for(String j : getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().k())
									{
										sender.sendMessage(ChatColor.GREEN + "$: " + getReact().getConfigurationController().getConfigurations().get(i).getCodeName() + "/" + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().get(j).getKey() + ": " + ChatColor.YELLOW + "(" + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getData().get(j).getType().toString() + ") " + ChatColor.AQUA + getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().getAbstract(j));
									}
									
									break;
								}
							}
						}
						
						if(args[2].equalsIgnoreCase("-reset"))
						{
							for(File i : getReact().getConfigurationController().getConfigurations().k())
							{
								if(getReact().getConfigurationController().getConfigurations().get(i).getCodeName().equalsIgnoreCase(r))
								{
									getReact().getConfigurationController().getConfigurations().get(i).onNewConfig();
									getReact().getDataController().saveFileConfig(i, getReact().getConfigurationController().getConfigurations().get(i).getConfiguration().toYaml(), getReact().getConfigurationController().getConfigurations().get(i));
									sender.sendMessage(ChatColor.GREEN + "$: " + getReact().getConfigurationController().getConfigurations().get(i).getCodeName() + " RESET");
									break;
								}
							}
							
							getReact().onReload(sender);
						}
					}
				}
			}
		}, "Directly manipulate config values without gui access.", "cfs"));
			
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				String[] args = getArgs();
				Boolean isPlayer = isPlayer();
				
				if(args.length >= 2)
				{
					if(args[1].equalsIgnoreCase("-c"))
					{
						sender.sendMessage(react.getActionController().getActionInstabilityCause().queryGuess().toString());
						return;
					}
					
					if(args[1].equalsIgnoreCase("-m"))
					{
						if(args.length == 3)
						{
							try
							{
								sender.sendMessage(react.getActionController().getActionInstabilityCause().queryGuess(Long.valueOf(args[2]).longValue()));
								return;
							}
							
							catch(Exception e)
							{
								sender.sendMessage(Info.COLOR_ERR + L.MESSAGE_ERROR_NONUMBER + args[3]);
							}
						}
							
						else
						{
							sender.sendMessage(Info.COLOR_ERR + L.MESSAGE_HELP_GUESS);
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
		}, L.COMMAND_GUESS, "guess", "g"));
				
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				String[] args = getArgs();
				Boolean isPlayer = isPlayer();
				
				if(args.length == 2)
				{
					if(args[1].equalsIgnoreCase("-c"))
					{
						sender.sendMessage(react.getActionController().getActionInstabilityCause().query().toString());
						return;
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
		}, L.COMMAND_BOOK, "status", "book", "report"));
				
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				Boolean isPlayer = isPlayer();
				
				if(isPlayer)
				{
					react.getMonitorController().toggleMapping(p);
				}
				
				else
				{
					msg(sender, L.MESSAGE_PLAYER_ONLY);
				}
			}
		}, L.COMMAND_MAP, "map", "graph"));
				
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				String[] args = getArgs();
				
				if(args.length == 2)
				{
					for(Plugin i : Bukkit.getPluginManager().getPlugins())
					{
						if(i.getName().toLowerCase().contains(args[1].toLowerCase()))
						{
							getReact().getPluginWeightController().report(sender, i);
							return;
						}
					}
					
					sender.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_ERROR_PLUGINUNKNOWN);
				}
				
				else
				{
					getReact().getPluginWeightController().report(sender);
				}
			}
		}, L.COMMAND_PLUGINS, "plugins", "pl", "p"));
				
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				String[] args = getArgs();
				Boolean isPlayer = isPlayer();
				
				if(args.length == 2)
				{
					GPage pg = react.getActionController().getActionInstabilityCause().queryPlugin(args[1]);
					GBook book = new GBook(ChatColor.AQUA + L.MESSAGE_QUERYRESULT).addPage(pg);
					
					if(book == null)
					{
						msg(sender, Info.COLOR_ERR + L.MESSAGE_ERROR_PLUGINUNKNOWN);
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
		}, L.COMMAND_QUERY, "query", "q"));
					
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				
				sender.sendMessage(String.format(Info.HRN, "React Server"));
				
				if(!React.instance().getConfiguration().getBoolean("react-remote.enable"))
				{
					sender.sendMessage(Info.TAG + ChatColor.GOLD + "(!) React server is disabled.");
				}
				
				sender.sendMessage(Info.TAG + ChatColor.AQUA + "Requests: " + ChatColor.GREEN + F.f(ReactServer.requests));
				sender.sendMessage(Info.HR);
			}
		}, L.COMMAND_CLIENT, "client", "net", "clients"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				if(isPlayer())
				{
					getReact().getScoreboardController().toggleMonitoring(getPlayer());
				}
				
				else
				{
					getSender().sendMessage(Info.TAG + ChatColor.RED + L.MESSAGE_PLAYER_ONLY);
				}
			}
		}, L.COMMAND_SCOREBOARD, "scoreboard", "sc", "board", "sboard"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				sender.sendMessage(Info.TAG + ChatColor.BOLD + ChatColor.GOLD + "CPU Score: " + ChatColor.RESET + ChatColor.GREEN + F.f(CPUTest.singleThreaded(50)));
			}
		}, L.COMMAND_CPUSCORE, "cpu-score", "cs", "cpu", "cpuscore"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				
				if(isPlayer())
				{
					if(Verbose.mrx.contains(p))
					{
						Verbose.mrx.remove(p);
						p.sendMessage(Info.COLOR_ERR + L.MESSAGE_VERBOSEOFF);
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
		}, L.COMMAND_VERBOSE, "verbose", "vb", "debug"));
				
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				if(getArgs().length == 2)
				{
					if(getArgs()[1].equalsIgnoreCase("-c"))
					{
						sender.sendMessage(react.getMonitorController().getMs().getRoot());
						return;
					}
					
					if(getArgs()[1].equalsIgnoreCase("-v"))
					{
						react.getMonitorController().toggleDisp(p);
						return;
					}
					
					if(getArgs()[1].equalsIgnoreCase("-lock"))
					{
						if(react.getMonitorController().isMonitoring(p))
						{
							react.getMonitorController().lock(p);
						}
						
						else
						{
							p.sendMessage(L.MESSAGE_MONITOR_LOCK_FAIL);
						}
						
						return;
					}
				}
				
				if(isPlayer())
				{
					react.getMonitorController().toggleMonitoring(p);
				}
						
				else
				{
					sender.sendMessage(react.getMonitorController().getMs().getRoot());
				}
			}
		}, L.COMMAND_MONITOR, "monitor", "mon", "m"));
				
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				React.dump();
				Date d = new Date();
				@SuppressWarnings("deprecation")
				String nme = "React/dumps/" + (d.getHours() + 1) + "-" + (d.getMinutes() + 1) + "-" + (d.getSeconds() + 1) + "-" + d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getYear() + ".yml";
				sender.sendMessage(Info.TAG + L.MESSAGE_DUMPED + nme);
			}
		}, L.COMMAND_DUMP, "dump", "d", "du", "out"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				
				if(isPlayer())
				{
					if(getArgs().length == 2)
					{
						if(react.getTimingsController().getAll() == null)
						{
							sender.sendMessage(Info.TAG + Info.COLOR_ERR + "Data has not been prepared yet. Please wait up to 5 minutes.");
							return;
						}
						
						GBook book = react.getTimingsController().getAll();
						book.filterTiming(getArgs()[1]);
						ItemStack is = book.toBook();
						p.getInventory().remove(is);
						p.getInventory().addItem(is);
						p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_BOOK + " [" + getArgs()[1] + "]");
					}
					
					else
					{
						if(react.getTimingsController().getAll() == null)
						{
							sender.sendMessage(Info.TAG + Info.COLOR_ERR + "Data has not been prepared yet. Please wait up to 5 minutes.");
							return;
						}
						
						ItemStack is = react.getTimingsController().getAll().toBook();
						p.getInventory().remove(is);
						p.getInventory().addItem(is);
						p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_BOOK);
					}
				}
					
				else
				{
					if(getArgs().length == 2)
					{
						if(react.getTimingsController().getAll() == null)
						{
							sender.sendMessage(Info.TAG + Info.COLOR_ERR + "Data has not been prepared yet. Please wait up to 5 minutes.");
							return;
						}
						
						GBook book = react.getTimingsController().getAll();
						book.filterTiming(getArgs()[1]);
						sender.sendMessage(book.toString());
					}
					
					else
					{
						if(react.getTimingsController().getAll() == null)
						{
							sender.sendMessage(Info.TAG + Info.COLOR_ERR + "Data has not been prepared yet. Please wait up to 5 minutes.");
							return;
						}
						
						sender.sendMessage(react.getTimingsController().getAll().toString());
					}
				}
			}
		}, L.COMMAND_TIMINGS, "timings", "t", "tim"));
					
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				
				if(getArgs().length > 1)
				{
					if(react.canFindPlayer(getArgs()[1]))
					{
						Player p = react.findPlayer(getArgs()[1]);
						sender.sendMessage(Info.TAG + ChatColor.AQUA + "Pong[" + p.getName() + "]: " + ChatColor.LIGHT_PURPLE + NMS.instance().ping(p) + "ms");
					}
					
					else
					{
						sender.sendMessage(Info.TAG + Info.COLOR_ERR + "Cannot find player.");
					}
				}
					
				else
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
							sender.sendMessage(Info.COLOR_ERR + "Failed to Ping... Unknown Version?");
							break;
						}
					}
					
					if(!nh.equals(""))
					{
						sender.sendMessage(Info.TAG + Info.COLOR_ERR + "Highest: " + ChatColor.GOLD + nh + " (" + highest + "ms)");
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
			}
		}, L.COMMAND_PING, "ping", "pong", "png"));
					
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				React.instance().getUpdateController().update(getSender());
			}
		}, L.COMMAND_UPDATE, "update", "u", "up"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			public void run()
			{
				CommandSender sender = getSender();
				getReact().onReload(sender);
			}
		}, L.COMMAND_RELOAD, "reload", "reset", "restart", "reboot"));
		
		GList<ReactCommand> co = commands.copy();
		
		while(!co.isEmpty())
		{
			GList<ReactCommand> inx = new GList<ReactCommand>();
			
			for(int i = 0; i < 8; i++)
			{
				if(co.isEmpty())
				{
					break;
				}
				
				inx.add(co.pop());
			}
			
			tabulations.add(inx);
		}
	}
	
	public void stop()
	{
		
	}
	
	public void msg(CommandSender sender, String msg)
	{
		sender.sendMessage(msg);
	}
	
	public void fireCommand(final CommandSender sender, String trigger, String[] args)
	{
		for(ReactCommand i : commands)
		{
			if(i.onCommand(trigger, sender, args))
			{
				return;
			}
		}
		
		sender.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_ERROR_NOTCOMMAND);
	}
	
	public void tabulate(CommandSender sender, int tab)
	{
		if(tab <= tabulations.size() && tab > 0)
		{
			sender.sendMessage(String.format(Info.HRN, "Commands " + ChatColor.LIGHT_PURPLE + "[" + tab + "/" + tabulations.size() + "]"));
			sender.sendMessage(ChatColor.DARK_GRAY + "Hover over any element for more information.");
			
			int dist = 0;
			
			for(ReactCommand i : tabulations.get(tab - 1))
			{
				if(dist < i.getTriggers().get(0).length())
				{
					dist = i.getTriggers().get(0).length();
				}
			}
			
			for(ReactCommand i : tabulations.get(tab - 1))
			{
				RawText rt = new RawText();
				rt.addTextWithHover("/react", RawText.COLOR_AQUA, "Use /re for short", RawText.COLOR_AQUA, true, false, false, false, false);
				rt.addText(" ");
				rt.addTextWithHover(i.getTriggers().get(0), RawText.COLOR_LIGHT_PURPLE, "All aliases include: " + i.getTriggers().toString(", "), RawText.COLOR_LIGHT_PURPLE, false, true, false, false, false);
				rt.addText(" ");
				rt.addText(StringUtils.repeat(" ", dist - i.getTriggers().get(0).length()));
				rt.addText(" = ", RawText.COLOR_DARK_AQUA, false, false, false, true, false);
				rt.addText(" ");
				
				if(i.getDescription().length() > 35)
				{
					String des = i.getDescription().substring(0, 24) + "...";
					String lef = "..." + i.getDescription().substring(20);
					rt.addTextWithHover(des, RawText.COLOR_DARK_AQUA, lef, RawText.COLOR_DARK_AQUA);
				}
				
				else
				{
					rt.addText(i.getDescription(), RawText.COLOR_DARK_AQUA);
				}
				
				rt.tellRawTo(getReact(), (Player) sender);
			}
			
			RawText rt = new RawText();
			
			if(tab == 1)
			{
				rt.addTextWithHover("<=<", RawText.COLOR_DARK_GRAY, "You are on the first page.", RawText.COLOR_RED, true, false, false, true, false);
			}
			
			else
			{
				rt.addTextWithHoverCommand("<=<", RawText.COLOR_LIGHT_PURPLE, "/re " + (tab - 1), "Previous Page", RawText.COLOR_LIGHT_PURPLE, true, false, false, true, false);
			}
			
			rt.addText(StringUtils.repeat(" ", 70), RawText.COLOR_DARK_GRAY, false, false, false, true, false);
			
			if(tab == tabulations.size())
			{
				rt.addTextWithHover(">=>", RawText.COLOR_DARK_GRAY, "You are on the last page.", RawText.COLOR_RED, true, false, false, true, false);
			}
			
			else
			{
				rt.addTextWithHoverCommand(">=>", RawText.COLOR_LIGHT_PURPLE, "/re " + (tab + 1), "Next Page", RawText.COLOR_LIGHT_PURPLE, true, false, false, true, false);
			}
			
			sender.sendMessage(ChatColor.DARK_GRAY + "Click the " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "<=>" + ChatColor.RESET + ChatColor.DARK_GRAY + " buttons to navigate.");
			
			rt.tellRawTo(getReact(), (Player) sender);
		}
	}
	
	public boolean onCommand(final CommandSender sender, Command cmd, String name, String[] args)
	{
		int len = args.length;
		String sub = len > 0 ? args[0] : "";
		
		if(cmd.getName().equalsIgnoreCase(Info.COMMAND))
		{
			if(React.isMef())
			{
				sender.sendMessage(Info.COLOR_ERR + "I'm sorry. I cant help you if you won't do the same.");
				return true;
			}
			
			if(!sender.hasPermission(Info.PERM_ACT) && !sender.hasPermission(Info.PERM_MONITOR) && !sender.hasPermission(Info.PERM_RELOAD))
			{
				sender.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_INSUFFICIENT_PERMISSION);
				return true;
			}
			
			if(len == 0)
			{
				if(sender instanceof Player)
				{
					tabulate(sender, 1);
				}
				
				else
				{
					int dist = 0;
					
					for(ReactCommand i : commands)
					{
						if(dist < i.getTriggers().get(0).length())
						{
							dist = i.getTriggers().get(0).length();
						}
					}
					
					for(ReactCommand i : commands)
					{
						sender.sendMessage(ChatColor.AQUA + "/react " + ChatColor.LIGHT_PURPLE + i.getTriggers().get(0) + ChatColor.DARK_GRAY + StringUtils.repeat(" ", dist - i.getTriggers().get(0).length()) + " - " + i.getDescription());
					}
				}
			}
			
			else
			{
				try
				{
					int m = Integer.valueOf(sub);
					
					if(m <= tabulations.size() && m > 0)
					{
						tabulate(sender, m);
						return true;
					}
				}
				
				catch(NumberFormatException e)
				{
					
				}
				
				fireCommand(sender, sub, args);
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
					p.sendMessage(Info.TAG + ChatColor.AQUA + "Current TPS (Exact): " + ChatColor.GREEN + F.f(React.instance().getSampleController().getSampleTicksPerSecond().getValue().getDouble(), 9));
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
