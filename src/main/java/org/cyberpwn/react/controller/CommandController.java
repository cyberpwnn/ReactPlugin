package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.api.ReactAPI;
import org.cyberpwn.react.api.U;
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
import org.cyberpwn.react.nms.NMSX;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.Amounts;
import org.cyberpwn.react.util.CPUTest;
import org.cyberpwn.react.util.Callback;
import org.cyberpwn.react.util.CommandRunnable;
import org.cyberpwn.react.util.Configurator;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.FSMap;
import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.GPage;
import org.cyberpwn.react.util.Gui;
import org.cyberpwn.react.util.Gui.Pane;
import org.cyberpwn.react.util.Gui.Pane.Element;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.Platform;
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
	
	@Override
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
			@Override
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
			@Override
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
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				sender.sendMessage(Info.TAG + ChatColor.AQUA + "React " + ChatColor.YELLOW + Version.V);
				React.instance().getUpdateController().version(sender);
			}
		}, L.COMMAND_VERSION, "version", "v", "ver"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				String args[] = getArgs();
				
				if(args.length == 1)
				{
					if(sender instanceof Player)
					{
						Player p = ((Player) sender);
						
						if(p.getItemInHand() != null && p.getItemInHand().getType().equals(Material.BOOK_AND_QUILL))
						{
							sender.sendMessage(Info.TAG + ChatColor.GRAY + "On the first page must be username:password");
							sender.sendMessage(Info.TAG + ChatColor.GRAY + "Then sign and close, and re-run /re auth");
						}
						
						else if(p.getItemInHand() != null && p.getItemInHand().getType().equals(Material.WRITTEN_BOOK))
						{
							boolean f = false;
							BookMeta bm = (BookMeta) p.getItemInHand().getItemMeta();
							
							for(String i : bm.getPages())
							{
								if(i.contains(":"))
								{
									String ux = "";
									String px = "";
									String hx = "NONE";
									
									if(i.split(":").length == 2)
									{
										ux = i.split(":")[0];
										px = i.split(":")[1];
										React.instance().getUpdateController().auth(p, ux, px, hx);
									}
									
									else if(i.split(":").length == 3)
									{
										ux = i.split(":")[0];
										px = i.split(":")[1];
										hx = i.split(":")[2];
										React.instance().getUpdateController().auth(p, ux, px, hx);
									}
									
									p.setItemInHand(new ItemStack(Material.AIR));
									f = true;
									break;
								}
							}
							
							if(!f)
							{
								sender.sendMessage(Info.TAG + ChatColor.GRAY + "On the First page must be username:password");
								sender.sendMessage(Info.TAG + ChatColor.GRAY + "Could not match pattern.");
							}
						}
						
						else
						{
							sender.sendMessage(Info.TAG + ChatColor.GRAY + "* Authentication data is stored in the react cache");
							sender.sendMessage(Info.TAG + ChatColor.GRAY + "* All credentials are encrypted");
							sender.sendMessage(Info.TAG + ChatColor.GRAY + "* Your credentials will only be used for updates");
							sender.sendMessage(Info.TAG + ChatColor.GRAY + "* The command executed will not be logged");
							sender.sendMessage(Info.TAG + ChatColor.RED + "Console Only!");
							sender.sendMessage(Info.TAG + ChatColor.RED + "/react-auth <username> <password>");
							sender.sendMessage(Info.TAG + ChatColor.RED + "2fa: /react-auth <username> <password> <2fa secret>");
							sender.sendMessage(Info.TAG + ChatColor.RED + "/re auth (while holding written book) <u:p[:h]>");
							sender.sendMessage(Info.HR);
						}
					}
				}
			}
		}, L.COMMAND_AUTH, "authenticate", "au", "auth"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
			public void run()
			{
				final CommandSender sender = getSender();
				File root = React.instance().getDataFolder().getParentFile();
				
				if(getArgs().length == 2)
				{
					root = new File(root, getArgs()[1]);
					
					if(!root.exists() || root.isFile())
					{
						sender.sendMessage(Info.TAG + ChatColor.RED + "Not a Folder. Use /re fs (for root)");
						return;
					}
				}
				
				try
				{
					sender.sendMessage(Info.TAG + ChatColor.AQUA + "Listing " + ChatColor.GREEN + root.getPath() + "...");
					
					new FSMap(root, new Callback<GMap<File, Long>>()
					{
						@Override
						public void run()
						{
							if(get().isEmpty())
							{
								sender.sendMessage(Info.TAG + ChatColor.RED + "No folders.");
								return;
							}
							
							GList<Long> order = get().v();
							GList<File> fs = new GList<File>();
							Collections.sort(order);
							
							for(Long i : order)
							{
								for(File j : get().k())
								{
									if(get().get(j) == i)
									{
										fs.add(j);
									}
								}
							}
							
							for(File i : fs)
							{
								sender.sendMessage(Info.TAG + ChatColor.GREEN + "  " + i.getName() + ": " + ChatColor.YELLOW + F.memx(get().get(i) / 1000) + " " + ChatColor.AQUA + i.listFiles().length + " File(s)");
							}
						}
					}).start();
				}
				
				catch(Exception e)
				{
					
				}
			}
		}, "Map the filesystem sizes.", "fs", "files", "filesystem", "file", "folder"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
			public void run()
			{
				new Configurator(getPlayer());
			}
		}, "Configure React ingame via inventory gui's", "configure", "config", "conf"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
			public void run()
			{
				getReact().getChannelListenController().scan(getPlayer());
			}
		}, "Sniff the bungeecord message channel", "sniff"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				GMap<InstabilityCause, Double> load = React.instance().getLagMapController().report();
				GList<Double> sort = new GList<Double>();
				GList<InstabilityCause> order = new GList<InstabilityCause>();
				
				for(InstabilityCause i : load.k())
				{
					sort.add(load.get(i));
				}
				
				Collections.sort(sort);
				Collections.reverse(sort);
				
				for(Double i : sort)
				{
					for(InstabilityCause j : load.k())
					{
						if(load.get(j) == i)
						{
							order.add(j);
						}
					}
				}
				
				getSender().sendMessage(String.format(Info.HRN, "Status"));
				
				for(InstabilityCause i : order)
				{
					if(load.get(i) > 0)
					{
						getSender().sendMessage(ChatColor.GRAY + StringUtils.capitalise(i.name().toLowerCase()) + ": " + ChatColor.AQUA + ChatColor.BOLD + F.pc(load.get(i), 3));
					}
				}
				getSender().sendMessage(Info.HR);
			}
		}, "View overall status load", "status"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
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
					
					p.sendMessage(Info.TAG + ChatColor.DARK_GRAY + "Hover & Click to interact.");
					
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
			@Override
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
							getReact().getConfigurationController().getConfigurations().get(i).onNewConfig(getReact().getConfigurationController().getConfigurations().get(i).getConfiguration());
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
								if(getReact().getConfigurationController().getConfigurations().get(i).getCodeName().equalsIgnoreCase(args[1]))
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
									getReact().getConfigurationController().getConfigurations().get(i).onNewConfig(getReact().getConfigurationController().getConfigurations().get(i).getConfiguration());
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
			@Override
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
			@Override
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
		}, L.COMMAND_BOOK, "book", "report"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				String[] args = getArgs();
				
				if(args.length == 1)
				{
					sender.sendMessage(String.format(Info.HRN, "Region Help"));
					sender.sendMessage(ChatColor.GRAY + "Add Custom Flags to world guard to change how react works in regions.");
					sender.sendMessage(ChatColor.AQUA + "/re rg " + ChatColor.YELLOW + "list " + ChatColor.GRAY + "- List WG Regions");
					sender.sendMessage(ChatColor.AQUA + "/re rg " + ChatColor.YELLOW + "?flag " + ChatColor.GRAY + "- List custom flags");
					sender.sendMessage(ChatColor.AQUA + "/re rg " + ChatColor.YELLOW + "?flag <region> " + ChatColor.GRAY + "- View custom flags for region");
					sender.sendMessage(ChatColor.AQUA + "/re rg " + ChatColor.YELLOW + "!flag <region> " + ChatColor.GRAY + "- Clear all flags from the region");
					sender.sendMessage(ChatColor.AQUA + "/re rg " + ChatColor.YELLOW + "+flag <region> <flag> " + ChatColor.GRAY + "- Add a react flag to a region");
					sender.sendMessage(ChatColor.AQUA + "/re rg " + ChatColor.YELLOW + "-flag <region> <flag> " + ChatColor.GRAY + "- Remove a react flag to a region");
					sender.sendMessage(Info.HR);
				}
				
				if(args.length > 1)
				{
					RegionController rc = getReact().getRegionController();
					String c = args[1];
					
					if(c.equalsIgnoreCase("list"))
					{
						rc.listRegions(sender);
					}
					
					else if(c.equalsIgnoreCase("?flag") && args.length == 2)
					{
						rc.listProperties(sender);
					}
					
					else if(args.length > 2)
					{
						String d = args[2];
						
						if(c.equalsIgnoreCase("!flag"))
						{
							rc.remove(sender, d);
						}
						
						else if(c.equalsIgnoreCase("?flag"))
						{
							rc.view(sender, d);
						}
						
						else if(args.length > 3)
						{
							String e = args[3];
							
							if(c.equalsIgnoreCase("+flag"))
							{
								rc.add(sender, d, e);
							}
							
							else if(c.equalsIgnoreCase("-flag"))
							{
								rc.remove(sender, d, e);
							}
						}
					}
					
					else
					{
						
					}
				}
			}
		}, L.COMMAND_RG, "rg", "reg", "region"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
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
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				String[] args = getArgs();
				
				if(!getReact().getTimingsController().enabled())
				{
					sender.sendMessage(Info.TAG + ChatColor.RED + "Timings are off");
					return;
				}
				
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
			@Override
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
			@Override
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
			@Override
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
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				
				sender.sendMessage(String.format(Info.HRN, "Environment"));
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Version: " + ChatColor.WHITE + "v" + Version.V + " (" + Version.C + ")");
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Distro: " + ChatColor.WHITE + "Production");
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Operating System: " + ChatColor.WHITE + Platform.getName() + " " + ChatColor.GRAY + "(" + Platform.getVersion() + ")");
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Java: " + ChatColor.WHITE + Platform.ENVIRONMENT.getJavaVendor() + " " + ChatColor.GRAY + "(" + Platform.ENVIRONMENT.getJavaVersion() + ")");
				
				if(ReactAPI.getMemoryFree() / 1024 / 1024 < 1024)
				{
					sender.sendMessage(Info.TAG + ChatColor.GREEN + "Multicore Status: " + ChatColor.RED + "OFFLINE " + ChatColor.WHITE + "(Not Enough Free Memory)");
				}
				
				else if(Runtime.getRuntime().maxMemory() / 1024 / 1024 < React.corec() * 768)
				{
					sender.sendMessage(Info.TAG + ChatColor.GREEN + "Multicore Status: " + ChatColor.RED + "OFFLINE " + ChatColor.WHITE + "(Not Enough Usable Memory for " + React.corec() + " processors. You need at least " + ((React.corec() * 768) - Runtime.getRuntime().maxMemory() / 1024 / 1024) + "mb more memory.");
				}
				
				else if(React.instance().getConfiguration().getBoolean("startup.multicore"))
				{
					sender.sendMessage(Info.TAG + ChatColor.GREEN + "Multicore Status: " + ChatColor.GREEN + "ONLINE ");
				}
				
				else
				{
					sender.sendMessage(Info.TAG + ChatColor.GREEN + "Multicore Status: " + ChatColor.RED + "OFFLINE " + ChatColor.WHITE + "(disabled in configuration)");
				}
				
				sender.sendMessage(String.format(Info.HRN, "CPU"));
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Type: " + ChatColor.WHITE + Amounts.to(Platform.CPU.getAvailableProcessors()) + " Core " + Platform.CPU.getArchitecture());
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Utilization: " + ChatColor.WHITE + F.pc(Platform.CPU.getCPULoad(), 1));
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Process Usage: " + ChatColor.WHITE + F.pc(Platform.CPU.getProcessCPULoad(), 1));
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "CoreTick: " + U.status(true));
				
				sender.sendMessage(String.format(Info.HRN, "MEMORY"));
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Physical: " + ChatColor.WHITE + F.memSize(Platform.MEMORY.PHYSICAL.getTotalMemory()) + ChatColor.GRAY + " (" + F.memSize(Platform.MEMORY.PHYSICAL.getUsedMemory()) + " / " + F.memSize(Platform.MEMORY.PHYSICAL.getTotalMemory()) + ")");
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Swap: " + ChatColor.WHITE + F.memSize(Platform.MEMORY.VIRTUAL.getTotalMemory()) + ChatColor.GRAY + " (" + F.memSize(Platform.MEMORY.VIRTUAL.getUsedMemory()) + " / " + F.memSize(Platform.MEMORY.VIRTUAL.getTotalMemory()) + ")");
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Commit: " + ChatColor.WHITE + F.memSize(Platform.MEMORY.VIRTUAL.getCommittedVirtualMemory()));
				
				sender.sendMessage(String.format(Info.HRN, "STORAGE"));
				sender.sendMessage(Info.TAG + ChatColor.GREEN + "Total: " + ChatColor.WHITE + F.memSize(Platform.STORAGE.getAbsoluteTotalSpace()) + ChatColor.GRAY + " (" + F.memSize(Platform.STORAGE.getAbsoluteUsedSpace()) + " / " + F.memSize(Platform.STORAGE.getAbsoluteTotalSpace()) + ")");
				
				for(File i : Platform.STORAGE.getRoots())
				{
					try
					{
						if(new File(".").getCanonicalPath().equals(i.getCanonicalPath()))
						{
							sender.sendMessage(Info.TAG + ChatColor.AQUA + i.toString() + ": " + ChatColor.WHITE + F.memSize(Platform.STORAGE.getTotalSpace(i)) + ChatColor.GRAY + " (" + F.memSize(Platform.STORAGE.getUsedSpace(i)) + " / " + F.memSize(Platform.STORAGE.getTotalSpace(i)) + ")");
							continue;
						}
					}
					
					catch(IOException e)
					{
						
					}
					
					sender.sendMessage(Info.TAG + ChatColor.GREEN + i.toString() + ": " + ChatColor.WHITE + F.memSize(Platform.STORAGE.getTotalSpace(i)) + ChatColor.GRAY + " (" + F.memSize(Platform.STORAGE.getUsedSpace(i)) + " / " + F.memSize(Platform.STORAGE.getTotalSpace(i)) + ")");
				}
				
				sender.sendMessage(Info.HR);
				
			}
		}, "Check information about react and the server", "environment", "env", "ev", "platform", "plat", "sys", "system"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				
				sender.sendMessage(String.format(Info.HRN, "CPU Score"));
				
				sender.sendMessage(Info.TAG + ChatColor.BOLD + ChatColor.GOLD + "1 Thread: " + ChatColor.RESET + ChatColor.GREEN + F.f(CPUTest.singleThreaded(10)));
				
				for(int i = 2; i < Runtime.getRuntime().availableProcessors() + 1; i *= 2)
				{
					sender.sendMessage(Info.TAG + ChatColor.BOLD + ChatColor.GOLD + i + " Threads: " + ChatColor.RESET + ChatColor.GREEN + F.f(CPUTest.multiThreaded(i, 10)));
				}
				
				sender.sendMessage(Info.HR);
			}
		}, L.COMMAND_CPUSCORE, "cpu-score", "cs", "cpu", "cpuscore"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
			public void run()
			{
				getReact().getConfigurationController().rebuildConfigurations(getSender());
			}
		}, L.COMMAND_CLEAN, "clean", "wipe", "clear", "fix"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
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
			@Override
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
					
					if(getArgs()[1].equalsIgnoreCase("-compass"))
					{
						react.getMonitorController().toggleCompass(p);
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
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				
				new ASYNC()
				{
					@Override
					public void async()
					{
						String url = React.dump();
						sender.sendMessage(Info.TAG + ChatColor.GRAY + "Dumped: " + ChatColor.WHITE + url);
					}
				};
			}
		}, L.COMMAND_DUMP, "dump", "d", "du", "out"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
			public void run()
			{
				Player p = getPlayer();
				CommandSender sender = getSender();
				
				if(!getReact().getTimingsController().enabled())
				{
					sender.sendMessage(Info.TAG + ChatColor.RED + "Timings are off");
					return;
				}
				
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
			@Override
			public void run()
			{
				CommandSender sender = getSender();
				
				if(getArgs().length > 1)
				{
					if(react.canFindPlayer(getArgs()[1]))
					{
						Player p = react.findPlayer(getArgs()[1]);
						sender.sendMessage(Info.TAG + ChatColor.AQUA + "Pong[" + p.getName() + "]: " + ChatColor.DARK_GRAY + NMSX.ping(p) + "ms");
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
							int ping = NMSX.ping(i);
							
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
						sender.sendMessage(Info.TAG + ChatColor.AQUA + "Yours: " + ChatColor.DARK_GRAY + NMSX.ping((Player) sender) + "ms");
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
			@Override
			public void run()
			{
				React.instance().getUpdateController().update(getSender());
			}
		}, L.COMMAND_UPDATE, "update", "u", "up"));
		
		commands.add(new ReactCommand(new CommandRunnable()
		{
			@Override
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
	
	@Override
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
		
		if(trigger.equalsIgnoreCase("compile"))
		{
			try
			{
				React.instance().compile();
			}
			
			catch(IOException e)
			{
				
			}
			
			catch(InvalidConfigurationException e)
			{
				
			}
		}
		
		sender.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_ERROR_NOTCOMMAND);
	}
	
	public void tabulate(CommandSender sender, int tab)
	{
		if(tab <= tabulations.size() && tab > 0)
		{
			sender.sendMessage(String.format(Info.HRN, "Commands " + ChatColor.YELLOW + "[" + tab + "/" + tabulations.size() + "]"));
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
				rt.addTextWithHover(i.getTriggers().get(0), RawText.COLOR_YELLOW, "All aliases include: " + i.getTriggers().toString(", "), RawText.COLOR_YELLOW, false, true, false, false, false);
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
				rt.addTextWithHoverCommand("<=<", RawText.COLOR_YELLOW, "/re " + (tab - 1), "Previous Page", RawText.COLOR_YELLOW, true, false, false, true, false);
			}
			
			rt.addText(StringUtils.repeat(" ", 70), RawText.COLOR_DARK_GRAY, false, false, false, true, false);
			
			if(tab == tabulations.size())
			{
				rt.addTextWithHover(">=>", RawText.COLOR_DARK_GRAY, "You are on the last page.", RawText.COLOR_RED, true, false, false, true, false);
			}
			
			else
			{
				rt.addTextWithHoverCommand(">=>", RawText.COLOR_YELLOW, "/re " + (tab + 1), "Next Page", RawText.COLOR_YELLOW, true, false, false, true, false);
			}
			
			sender.sendMessage(ChatColor.DARK_GRAY + "Click the " + ChatColor.YELLOW + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "<=>" + ChatColor.RESET + ChatColor.DARK_GRAY + " buttons to navigate.");
			
			rt.tellRawTo(getReact(), (Player) sender);
		}
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String name, String[] args)
	{
		int len = args.length;
		String sub = len > 0 ? args[0] : "";
		
		if(cmd.getName().equalsIgnoreCase(Info.COMMAND))
		{
			React.instance().setTag();
			
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
						sender.sendMessage(ChatColor.AQUA + "/react " + ChatColor.DARK_GRAY + i.getTriggers().get(0) + ChatColor.DARK_GRAY + StringUtils.repeat(" ", dist - i.getTriggers().get(0).length()) + " - " + i.getDescription());
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
	
	@SuppressWarnings("deprecation")
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
	public void omCommandPre(ServerCommandEvent e)
	{
		if(e.getCommand().equalsIgnoreCase("timings off"))
		{
			React.instance().getTimingsController().off(null);
		}
		
		if(e.getCommand().equalsIgnoreCase("timings on"))
		{
			React.instance().getTimingsController().on(null);
		}
	}
	
	public void handle(CommandSender s, String msg)
	{
		if(msg.startsWith("/react-auth") || msg.startsWith("react-auth"))
		{
			String u = "";
			String p = "";
			String h = "NONE";
			
			if(msg.split(" ").length == 3)
			{
				u = msg.split(" ")[1];
				p = msg.split(" ")[2];
				React.instance().getUpdateController().auth(s, u, p, h);
			}
			
			else if(msg.split(" ").length == 3)
			{
				u = msg.split(" ")[1];
				p = msg.split(" ")[2];
				h = msg.split(" ")[3];
				React.instance().getUpdateController().auth(s, u, p, h);
			}
			
			else
			{
				s.sendMessage(ChatColor.RED + "Console Only!");
				s.sendMessage(ChatColor.RED + "/react-auth <username> <password>");
				s.sendMessage(ChatColor.RED + "2fa: /react-auth <username> <password> <2fa secret>");
			}
		}
	}
	
	@EventHandler
	public void onServer(ServerCommandEvent e)
	{
		if(e.getCommand().startsWith("react-auth"))
		{
			handle(e.getSender(), e.getCommand());
			e.setCancelled(true);
			return;
		}
		
		if(e.getCommand().equalsIgnoreCase("timings off"))
		{
			if(e.getSender().hasPermission("bukkit.command.timings"))
			{
				React.instance().getTimingsController().off(e.getSender());
			}
		}
		
		if(e.getCommand().equalsIgnoreCase("timings on"))
		{
			if(e.getSender().hasPermission("bukkit.command.timings"))
			{
				React.instance().getTimingsController().on(e.getSender());
			}
		}
		
		if(e.getCommand().equalsIgnoreCase("mem") || e.getCommand().equalsIgnoreCase("memory"))
		{
			if(React.isAllowMem())
			{
				e.setCancelled(true);
				
				CommandSender p = e.getSender();
				
				if(e.getSender().hasPermission(Info.PERM_MONITOR))
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
					e.getSender().sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
				}
			}
		}
		
		if(e.getCommand().equalsIgnoreCase("tps"))
		{
			if(React.isAllowMem())
			{
				CommandSender p = e.getSender();
				
				if(e.getSender().hasPermission("bukkit.command.tps"))
				{
					p.sendMessage(Info.TAG + ChatColor.AQUA + "Current TPS (Exact): " + ChatColor.GREEN + F.f(React.instance().getSampleController().getSampleTicksPerSecond().getValue().getDouble(), 9));
				}
				
				else
				{
					e.getSender().sendMessage(L.MESSAGE_INSUFFICIENT_PERMISSION);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPre(PlayerCommandPreprocessEvent e)
	{
		if(e.getMessage().equalsIgnoreCase("/timings off"))
		{
			if(e.getPlayer().hasPermission("bukkit.command.timings"))
			{
				React.instance().getTimingsController().off(e.getPlayer());
			}
		}
		
		if(e.getMessage().equalsIgnoreCase("/timings on"))
		{
			if(e.getPlayer().hasPermission("bukkit.command.timings"))
			{
				React.instance().getTimingsController().on(e.getPlayer());
			}
		}
		
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
		try
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
		
		catch(Exception ee)
		{
			
		}
	}
	
	public String isObfuscated()
	{
		if(getClass().getSimpleName().equals("CommandController"))
		{
			return "Nope";
		}
		
		else
		{
			return "Definitely";
		}
	}
}
