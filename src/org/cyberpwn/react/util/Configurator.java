package org.cyberpwn.react.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.Cluster;
import org.cyberpwn.react.cluster.ClusterBoolean;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.ClusterConfig.ClusterDataType;
import org.cyberpwn.react.cluster.ClusterDouble;
import org.cyberpwn.react.cluster.ClusterInteger;
import org.cyberpwn.react.cluster.ClusterString;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.controller.ConfigurationController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.util.Gui.Pane;
import org.cyberpwn.react.util.Gui.Pane.Element;

@SuppressWarnings("deprecation")
public class Configurator implements Listener
{
	public static Configurator instance;
	private Gui gui;
	private Player p;
	private ConfigurationController c;
	private boolean listen;
	private String value;
	private Runnable runListen;
	private boolean registered = false;
	
	public Configurator(Player player)
	{
		instance = this;
		React.instance().register(this);
		this.registered = true;
		this.p = player;
		this.gui = new Gui(p, React.instance());
		this.listen = false;
		this.c = React.instance().getConfigurationController();
		this.runListen = null;
		this.value = null;
		
		Pane sel = gui.new Pane("Select a Configuration");
		
		int ind = 0;
		
		for(final File i : c.getConfigurations().k())
		{
			final Configurable co = c.getConfigurations().get(i);
			
			Element e = sel.new Element(ChatColor.LIGHT_PURPLE + co.getCodeName() + ".yml", Material.SLIME_BALL, ind);
			e.addInfo("Located at ");
			e.addInfo(i.getPath().replaceFirst("plugins", ""));
			e.addBullet("Click to begin editing.");
			e.setQuickRunnable(new Runnable()
			{
				@Override
				public void run()
				{
					final ClusterConfig cc = co.getConfiguration().copy();
					
					doConfiguration(i, cc, co);
				}
			});
			
			ind++;
		}
		
		sel.setDefault();
		gui.show();
	}
	
	private void register()
	{
		if(!registered)
		{
			registered = true;
			React.instance().register(this);
		}
	}
	
	private void doConfiguration(final File file, final ClusterConfig cc, final Configurable co)
	{
		gui.close();
		gui.getPanes().clear();
		destroy();
		register();
		gui = new Gui(p, React.instance());
		final Pane pane = gui.new Pane("Configure " + co.getCodeName());
		
		int ind = 0;
		
		for(final String i : cc.getData().k())
		{
			final Cluster l = cc.getData().get(i);
			String vv = "";
			Material mat = null;
			
			if(l.getType().equals(ClusterDataType.BOOLEAN))
			{
				vv = String.valueOf(((ClusterBoolean) l).get());
				
				if(((ClusterBoolean) l).get())
				{
					mat = Material.GLOWSTONE_DUST;
				}
				
				else
				{
					mat = Material.REDSTONE;
				}
			}
			
			else if(l.getType().equals(ClusterDataType.DOUBLE))
			{
				vv = String.valueOf(((ClusterDouble) l).get());
				mat = Material.WATER_BUCKET;
			}
			
			else if(l.getType().equals(ClusterDataType.INTEGER))
			{
				vv = String.valueOf(((ClusterInteger) l).get());
				mat = Material.MILK_BUCKET;
			}
			
			else if(l.getType().equals(ClusterDataType.STRING))
			{
				vv = String.valueOf(((ClusterString) l).get());
				mat = Material.SIGN;
			}
			
			else
			{
				continue;
			}
			
			final String kvv = vv;
			String s = ChatColor.AQUA + "(" + l.getType() + ") " + ChatColor.GOLD + i.split("\\.")[i.split("\\.").length - 1] + ": " + ChatColor.GREEN + vv;
			final Element e = pane.new Element(s, mat, ind);
			Integer imx = 0;
			GList<String> trace = new GList<String>(i.split("\\."));
			
			for(String j : trace)
			{
				String xs = "";
				
				if(trace.size() - 1 == imx)
				{
					xs = ChatColor.AQUA + StringUtils.repeat("  ", imx) + j + ": " + ChatColor.LIGHT_PURPLE + vv;
				}
				
				else
				{
					xs = ChatColor.DARK_AQUA + StringUtils.repeat("  ", imx) + j + ": ";
				}
				
				e.addBullet(xs);
				
				imx++;
			}
			
			e.setQuickRunnable(new Runnable()
			{
				@Override
				public void run()
				{
					if(l.getType().equals(ClusterDataType.BOOLEAN))
					{
						ClusterBoolean cb = new ClusterBoolean(i, !cc.getBoolean(i));
						cc.getData().put(i, cb);
						pane.breakElements();
						gui.close();
						
						new TaskLater(1)
						{
							public void run()
							{
								React.instance().getConfigurationController().getCache().put(file, cc);
								p.sendMessage(Info.TAG + ChatColor.AQUA + "Committed. " + ChatColor.GOLD + "There are " + gitted());
								React.instance().getConfigurationController().flush(p);
								doConfiguration(file, cc.copy(), co);
							}
						};
					}
					
					else if(l.getType().equals(ClusterDataType.DOUBLE))
					{
						gui.close();
						p.sendMessage(Info.TAG + ChatColor.GREEN + ChatColor.BOLD + "Please type the number (eg. 2.434)");
						p.sendMessage(Info.TAG + ChatColor.GOLD + "Current Value: " + ChatColor.UNDERLINE + kvv);
						listen = true;
						runListen = new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									Double d = Double.valueOf(value);
									ClusterDouble cb = new ClusterDouble(i, d);
									cc.getData().put(i, cb);
									pane.breakElements();
									gui.close();
									
									new TaskLater(1)
									{
										public void run()
										{
											React.instance().getConfigurationController().getCache().put(file, cc);
											p.sendMessage(Info.TAG + ChatColor.AQUA + "Committed. " + ChatColor.GOLD + "There are " + gitted());
											React.instance().getConfigurationController().flush(p);
											doConfiguration(file, cc.copy(), co);
										}
									};
								}
								
								catch(Exception e)
								{
									p.sendMessage(Info.TAG + Info.COLOR_ERR + "Invalid Data Type for DOUBLE. (eg. 6.31238)");
									doConfiguration(file, cc.copy(), co);
								}
							}
						};
					}
								
					else if(l.getType().equals(ClusterDataType.INTEGER))
					{
						gui.close();
						p.sendMessage(Info.TAG + ChatColor.GREEN + ChatColor.BOLD + "Please type the number (eg. 54)");
						p.sendMessage(Info.TAG + ChatColor.GOLD + "Current Value: " + ChatColor.UNDERLINE + kvv);
						listen = true;
						runListen = new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									Integer d = Integer.valueOf(value);
									ClusterInteger cb = new ClusterInteger(i, d);
									cc.getData().put(i, cb);
									pane.breakElements();
									gui.close();
									
									new TaskLater(1)
									{
										public void run()
										{
											React.instance().getConfigurationController().getCache().put(file, cc);
											p.sendMessage(Info.TAG + ChatColor.AQUA + "Committed. " + ChatColor.GOLD + "There are " + gitted());
											React.instance().getConfigurationController().flush(p);
											doConfiguration(file, cc.copy(), co);
										}
									};
								}
								
								catch(Exception e)
								{
									p.sendMessage(Info.TAG + Info.COLOR_ERR + "Invalid Data Type for INTEGER. (eg. 61)");
									doConfiguration(file, cc.copy(), co);
								}
							}
						};
					}
								
					else if(l.getType().equals(ClusterDataType.STRING))
					{
						gui.close();
						p.sendMessage(Info.TAG + ChatColor.GREEN + ChatColor.BOLD + "Please type the number (eg. abcd-2$/@)");
						p.sendMessage(Info.TAG + ChatColor.GOLD + "Current Value: " + ChatColor.UNDERLINE + kvv);
						listen = true;
						runListen = new Runnable()
						{
							@Override
							public void run()
							{
								String vv = value;
								ClusterString cb = new ClusterString(i, vv);
								cc.getData().put(i, cb);
								pane.breakElements();
								gui.close();
								
								new TaskLater(1)
								{
									public void run()
									{
										React.instance().getConfigurationController().getCache().put(file, cc);
										p.sendMessage(Info.TAG + ChatColor.AQUA + "Committed. " + ChatColor.GOLD + "There are " + gitted());
										React.instance().getConfigurationController().flush(p);
										doConfiguration(file, cc.copy(), co);
									}
								};
							}
						};
					}
					
					else
					{
						
					}
				}
			});
					
			ind++;
		}
		
		pane.setDefault();
		gui.show();
	}
	
	private String gitted()
	{
		int compared = compare();
		
		if(compared == 0)
		{
			return "no changes to be saved";
		}
		
		else
		{
			return compared + " changes in " + React.instance().getConfigurationController().getCache().size() + " files.";
		}
	}
	
	private int compare()
	{
		int changes = 0;
		
		for(File i : React.instance().getConfigurationController().getConfigurations().k())
		{
			ClusterConfig a = React.instance().getConfigurationController().getConfigurations().get(i).getConfiguration();
			
			if(React.instance().getConfigurationController().getCache().containsKey(i))
			{
				changes += compare(a, React.instance().getConfigurationController().getCache().get(i));
			}
		}
		
		return changes;
	}
	
	private int compare(ClusterConfig a, ClusterConfig b)
	{
		return a.toYaml().saveToString().split("\n").length;
	}
	
	public void destroy()
	{
		React.instance().unRegister(this);
		registered = false;
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent e)
	{
		if(listen && e.getPlayer().equals(p))
		{
			value = e.getMessage();
			runListen.run();
			listen = false;
			runListen = null;
			value = null;
			e.setCancelled(true);
			e.setMessage(null);
		}
	}
}
