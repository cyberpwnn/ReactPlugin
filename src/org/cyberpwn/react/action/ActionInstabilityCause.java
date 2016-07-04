package org.cyberpwn.react.action;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitTask;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.json.RawText;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.GPage;
import org.cyberpwn.react.util.GStub;
import org.cyberpwn.react.util.GTime;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.MathUtils;
import org.cyberpwn.react.util.Verbose;

public class ActionInstabilityCause extends Action
{
	private GMap<Player, Float> speeds;
	private final GMap<InstabilityCause, Integer> problems;
	private final GList<InstabilityCause> notified;
	private boolean force;
	private boolean lagging;
	private long maxRedstone;
	private final GList<GStub> stubs;
	public static GList<GStub> issues = new GList<GStub>();
	
	public ActionInstabilityCause(ActionController actionController)
	{
		super(actionController, Material.STONE, "x", "ActionInstabilityCause", 10, "Instability Trace", L.ACTION_INSTABILITYCAUSE, false);
		
		problems = new GMap<InstabilityCause, Integer>();
		notified = new GList<InstabilityCause>();
		stubs = new GList<GStub>();
		speeds = new GMap<Player, Float>();
		lagging = false;
		force = false;
		maxRedstone = -1;
	}
	
	public void act()
	{
		if(actionController.getReact().getConfig().getBoolean("runtime.disable-reactions") || React.isMef())
		{
			return;
		}
		
		for(InstabilityCause i : new GList<InstabilityCause>(problems.keySet()))
		{
			problems.put(i, problems.get(i) - 1);
			
			if(problems.get(i) <= 0)
			{
				problems.remove(i);
				notified.remove(i);
				
				if(i.equals(InstabilityCause.LAG))
				{
					continue;
				}
				
				if(i.equals(InstabilityCause.CHUNK_GEN))
				{
					if(cc.getBoolean(getCodeName() + ".slow-fast-flyers-temporarily"))
					{
						if(!speeds.isEmpty())
						{
							for(Player k : speeds.k())
							{
								try
								{
									k.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_SLOWED_FIXED);
									k.setFlySpeed(speeds.get(k));
								}
								
								catch(Exception e)
								{
									
								}
							}
						}
					}
					
					speeds.clear();
				}
				
				for(Player j : getActionController().getReact().getServer().getOnlinePlayers())
				{
					if(j.hasPermission(Info.PERM_MONITOR) && i.isTalkative() && React.isNf())
					{
						if(getActionController().getReact().getPlayerController().exists(j) && getActionController().getReact().getPlayerController().gpd(j).getIgnored().contains(i.toString()))
						{
							continue;
						}
						
						j.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + i.getName() + ": " + ChatColor.GREEN + ChatColor.UNDERLINE + L.MESSAGE_FIXED);
					}
				}
			}
		}
		
		final SampleController s = getActionController().getReact().getSampleController();
		Long memoryMax = s.getSampleMemoryUsed().getMemoryMax();
		Long memoryUsed = s.getSampleMemoryUsed().getValue().getLong();
		Long spms = s.getSampleMemorySweepFrequency().getValue().getLong();
		Long liquid = s.getSampleLiquidFlowPerSecond().getValue().getLong();
		Long redstone = s.getSampleRedstoneUpdatesPerSecond().getValue().getLong();
		Long chunkGenPerSecond = s.getSampleChunkGenPerSecond().getValue().getLong();
		Long chunks = s.getSampleChunksLoaded().getValue().getLong();
		Long tnt = s.getSampleTNTPerSecond().getValue().getLong();
		Long chunksMb = (long) (((double) chunks) * 6.4);
		Long entities = s.getSampleEntities().getValue().getLong();
		Long drops = s.getSampleDrops().getValue().getLong();
		final Double tps = s.getSampleTicksPerSecond().getValue().getDouble();
		Double memoryPercent = (double) memoryUsed / (double) memoryMax;
		Double memoryChunkPercent = (double) chunksMb / (double) memoryUsed;
		
		if(tps < cc.getDouble(getCodeName() + ".low.tps"))
		{
			lagging = true;
			Verbose.x("instability", "LOW TPS: Analysing...");
			
			if(s.getExternalSampleWorldBorder().filling())
			{
				problems.put(InstabilityCause.WORLD_BORDER, 20);
				Verbose.x("instability", "- WORLD BORDER");
				return;
			}
			
			if(spms < cc.getInt(getCodeName() + ".high.spms") && memoryPercent > cc.getDouble(getCodeName() + ".high.memory.percent"))
			{
				problems.put(InstabilityCause.MEMORY, 20);
				Verbose.x("instability", "- MEMORY: " + memoryPercent);
				
				if(memoryChunkPercent > cc.getDouble(getCodeName() + ".high.memory.chunk.percent"))
				{
					problems.put(InstabilityCause.CHUNKS, 20);
					Verbose.x("instability", "- CHUNKS: " + memoryChunkPercent);
				}
			}
			
			if(liquid > cc.getInt(getCodeName() + ".high.liquid"))
			{
				problems.put(InstabilityCause.LIQUID, 40);
				Verbose.x("instability", "- LIQUID: " + liquid);
			}
			
			if(redstone > cc.getInt(getCodeName() + ".high.redstone"))
			{
				problems.put(InstabilityCause.REDSTONE, 30);
				Verbose.x("instability", "- REDSTONE: " + redstone);
			}
			
			if(tnt > cc.getInt(getCodeName() + ".high.tnt"))
			{
				problems.put(InstabilityCause.TNT_EXPLOSIONS, 100);
				Verbose.x("instability", "- TNT: " + tnt);
			}
			
			if(chunkGenPerSecond / 2 > cc.getInt(getCodeName() + ".high.chunk.generation"))
			{
				problems.put(InstabilityCause.CHUNK_GEN, 15);
				Verbose.x("instability", "- CHUNKGEN/s: " + chunkGenPerSecond);
				
				if(cc.getBoolean(getCodeName() + ".slow-fast-flyers-temporarily"))
				{
					for(Player i : getActionController().getReact().onlinePlayers())
					{
						if(i.isFlying() && i.getFlySpeed() > 0.4 && !speeds.containsKey(i))
						{
							speeds.put(i, i.getFlySpeed());
							i.sendMessage(Info.TAG + Info.COLOR_ERR + L.MESSAGE_SLOWED);
							i.setFlySpeed(0.4f);
						}
					}
				}
			}
			
			if(entities > cc.getInt(getCodeName() + ".high.entity.count"))
			{
				problems.put(InstabilityCause.ENTITIES, 100);
				Verbose.x("instability", "- ENTITIES: " + entities);
			}
		}
		
		else
		{
			lagging = false;
			
			if(redstone > maxRedstone)
			{
				maxRedstone = redstone;
				Verbose.x("limit", "Max Redstone Reached since Load: " + maxRedstone);
			}
		}
		
		if(drops > cc.getInt(getCodeName() + ".high.drop.count"))
		{
			actionController.getActionTeleportDrops().turnOn();
			Verbose.x("instability", "- HIGH DROP: " + drops);
		}
		
		else
		{
			actionController.getActionTeleportDrops().turnOff();
		}
		
		if(problems.containsKey(InstabilityCause.REDSTONE))
		{
			actionController.getActionSuppressRedstone().freeze();
			Verbose.x("instability", "- REDSTONE FREEZE SMALL ENABLED");
			
			actionController.getReact().scheduleSyncTask(20, new Runnable()
			{
				@Override
				public void run()
				{
					if(!force)
					{
						Verbose.x("instability", "- REDSTONE: FORCE: TRUE");
						force = true;
						
						if(s.getSampleTicksPerSecond().getValue().getDouble() < 11.0)
						{
							Verbose.x("instability", "- REDSTONE: TPS: < 11tps!!!");
							if(problems.containsKey(InstabilityCause.REDSTONE))
							{
								if(React.isNf())
								{
									for(Player j : getActionController().getReact().getServer().getOnlinePlayers())
									{
										if(j.hasPermission(Info.PERM_MONITOR))
										{
											if(getActionController().getReact().getPlayerController().exists(j) && getActionController().getReact().getPlayerController().gpd(j).getIgnored().contains(InstabilityCause.REDSTONE))
											{
												continue;
											}
											
											j.sendMessage(ChatColor.GOLD + "Redstone is making the server unplayable. " + Info.COLOR_ERR + " Applying Force.");
										}
									}
								}
								
								Verbose.x("instability", "- REDSTONE: TPS: TOO MUCH CPU TIME!");
								actionController.getActionSuppressRedstone().freezeAll();
							}
						}
						
						else
						{
							actionController.getReact().scheduleSyncTask(50, new Runnable()
							{
								@Override
								public void run()
								{
									if(problems.containsKey(InstabilityCause.REDSTONE))
									{
										Verbose.x("instability", "- REDSTONE: TOO LONG TO FIX: FORCE: TRUE");
										
										if(React.isNf())
										{
											for(Player j : getActionController().getReact().getServer().getOnlinePlayers())
											{
												if(j.hasPermission(Info.PERM_MONITOR))
												{
													if(getActionController().getReact().getPlayerController().exists(j) && getActionController().getReact().getPlayerController().gpd(j).getIgnored().contains(InstabilityCause.REDSTONE))
													{
														continue;
													}
													
													j.sendMessage(ChatColor.GOLD + "Redstone is still lagging. " + Info.COLOR_ERR + " Applying Force.");
												}
											}
										}
										
										actionController.getActionSuppressRedstone().freezeAll();
									}
								}
							});
						}
					}
				}
			});
		}
		
		else
		{
			actionController.getActionSuppressRedstone().unfreeze();
			actionController.getActionSuppressRedstone().unfreezeAll();
			force = false;
		}
		
		if(problems.containsKey(InstabilityCause.LIQUID))
		{
			actionController.getActionSuppressLiquid().freeze();
			Verbose.x("instability", "- LIQUID FREEZE: ON");
		}
		
		else
		{
			actionController.getActionSuppressLiquid().unfreeze();
		}
		
		if(problems.containsKey(InstabilityCause.TNT_EXPLOSIONS))
		{
			actionController.getActionSuppressTnt().freeze();
			Verbose.x("instability", "- TNT: KILL: ON");
		}
		
		else
		{
			actionController.getActionSuppressTnt().unfreeze();
		}
		
		if(problems.size() == 0 && tps < cc.getDouble(getCodeName() + ".low.tps"))
		{
			Verbose.x("instability", "- UNKNOWN LAG SOURCE! USING LOG JUDGEMENT");
			InstabilityCause ins = InstabilityCause.LAG;
			
			double pcliquid = MathUtils.percent(0, liquid, cc.getInt(getCodeName() + ".high.liquid"));
			double pcredstone = MathUtils.percent(0, redstone, cc.getInt(getCodeName() + ".high.redstone"));
			double pctnt = MathUtils.percent(0, liquid, cc.getInt(getCodeName() + ".high.tnt"));
			
			Verbose.x("instability", "-- LIQ: " + pcliquid);
			Verbose.x("instability", "-- RED: " + pcredstone);
			Verbose.x("instability", "-- TNT: " + pctnt);
			
			double[] ch = new double[] { pcliquid, pcredstone, pctnt };
			double hpc = 33.0;
			int index = -1;
			
			for(int i = 0; i < ch.length; i++)
			{
				if(ch[i] > hpc)
				{
					hpc = ch[i];
					index = i;
				}
			}
			
			switch(index)
			{
				case 0:
					ins = InstabilityCause.LIQUID;
				case 1:
					ins = InstabilityCause.REDSTONE;
				case 2:
					ins = InstabilityCause.TNT_EXPLOSIONS;
			}
			
			Verbose.x("instability", "--- SELECTED: " + ins.toString());
			problems.put(ins, 30);
		}
		
		for(InstabilityCause i : problems.keySet())
		{
			getActionController().getReact().getSampleController().getSampleHitRate().hit(i);
			
			if(!notified.contains(i))
			{
				if(i.equals(InstabilityCause.LAG))
				{
					continue;
				}
				
				for(Player j : getActionController().getReact().getServer().getOnlinePlayers())
				{
					if(j.hasPermission(Info.PERM_MONITOR) && i.isTalkative() && React.isNf())
					{
						if(getActionController().getReact().getPlayerController().exists(j) && getActionController().getReact().getPlayerController().gpd(j).getIgnored().contains(i.toString()))
						{
							continue;
						}
						
						if(i.equals(InstabilityCause.REDSTONE))
						{
							Chunk c = actionController.getReact().getSampleController().getSampleRedstoneUpdatesPerSecond().getChunk();
							RawText t = new RawText();
							
							t.addText(i.getName() + ": " + L.MESSAGE_ISSUES, RawText.COLOR_RED);
							t.addTextWithHoverCommand(" at " + c.getWorld().getName() + " [" + c.getX() + "," + c.getZ() + "]", RawText.COLOR_GOLD, "/tp " + c.getBlock(0, 0, 0).getX() + " " + level(c.getWorld(), c.getBlock(0, 0, 0).getX(), c.getBlock(0, 0, 0).getZ()) + " " + c.getBlock(0, 0, 0).getZ(), "Click to teleport near this area. Please be careful, as the location is not accurate.", RawText.COLOR_GREEN);
							t.tellRawTo(getActionController().getReact(), j);
						}
						
						else
						{
							j.sendMessage(Info.TAG + ChatColor.LIGHT_PURPLE + i.getName() + ": " + Info.COLOR_ERR + ChatColor.BOLD + L.MESSAGE_ISSUES.replaceAll("\\.{3}", ChatColor.MAGIC + "..."));
						}
					}
				}
				
				notified.add(i);
				
				if(!stubs.isEmpty() && stubs.get(stubs.size() - 1).getTitle().equals(i.getName()))
				{
					stubs.remove(stubs.size() - 1);
				}
				
				stubs.add(new GStub(i.getName(), i.getProblem()));
				
				if(stubs.size() > 64)
				{
					stubs.remove(0);
				}
			}
		}
		
		issues = stubs.copy().removeDuplicates();
	}
	
	public GMap<String, Double> pollHeaftSamples()
	{
		GMap<String, Double> polled = new GMap<String, Double>();
		
		for(Samplable i : getActionController().getReact().getSampleController().getSamples().keySet())
		{
			polled.put(i.getName(), (double) (i.getReactionTime()) / 1000000.0);
		}
		
		return polled;
	}
	
	public GMap<String, Double> pollHeaftActions()
	{
		GMap<String, Double> polled = new GMap<String, Double>();
		
		for(Actionable i : getActionController().getActions().keySet())
		{
			polled.put(F.trim(i.getName(), 12), (double) (i.getReactionTime()) / 1000000.0);
		}
		
		return polled;
	}
	
	public GList<String> sortHeaft(GMap<String, Double> heaft)
	{
		GList<String> order = new GList<String>();
		GList<Double> vv = heaft.v();
		Collections.sort(vv);
		
		for(Double i : vv)
		{
			for(String j : heaft.keySet())
			{
				if(heaft.get(j) == i)
				{
					order.add(j);
				}
			}
		}
		
		return order.reverse();
	}
	
	public String pagePollSamples()
	{
		String s = "";
		
		GMap<String, Double> h = pollHeaftSamples();
		GList<String> o = sortHeaft(h);
		
		for(String i : o)
		{
			if(h.get(i) > 5.0)
			{
				s = s + ChatColor.DARK_AQUA + i + ": " + ChatColor.DARK_RED + ChatColor.BOLD + F.f(h.get(i), 2) + "ms\n";
			}
			
			else if(h.get(i) > 2.5)
			{
				s = s + ChatColor.DARK_AQUA + i + ": " + ChatColor.DARK_RED + F.f(h.get(i), 2) + "ms\n";
			}
			
			else
			{
				s = s + ChatColor.DARK_AQUA + i + ": " + ChatColor.DARK_BLUE + F.f(h.get(i), 2) + "ms\n";
			}
		}
		
		return s;
	}
	
	public String pagePollActions()
	{
		String s = "";
		
		GMap<String, Double> h = pollHeaftActions();
		GList<String> o = sortHeaft(h);
		
		for(String i : o)
		{
			if(h.get(i) > 5.0)
			{
				s = s + ChatColor.DARK_AQUA + i + ": " + ChatColor.DARK_RED + ChatColor.BOLD + F.f(h.get(i), 2) + "ms\n";
			}
			
			else if(h.get(i) > 2.5)
			{
				s = s + ChatColor.DARK_AQUA + i + ": " + ChatColor.DARK_RED + F.f(h.get(i), 2) + "ms\n";
			}
			
			else
			{
				s = s + ChatColor.DARK_AQUA + i + ": " + ChatColor.DARK_BLUE + F.f(h.get(i), 2) + "ms\n";
			}
		}
		
		return s;
	}
	
	public GBook query()
	{
		GBook book = new GBook(Info.COLOR_ERR + "" + problems.size() + " Server Issue(s)");
		GPage status = new GPage();
		GPage timings = new GPage();
		GPage actions = new GPage();
		status.put("Issues", "There are currently " + problems.size() + " Issue(s) and " + stubs.size() + " (or more) past Issue(s). You can view them here any time.");
		book.addPage(status);
		GPage stats = new GPage();
		status.put("React Hit Summary", getActionController().getReact().getSampleController().getSampleHitRate().getTextSmall());
		stats.put("React Hits", getActionController().getReact().getSampleController().getSampleHitRate().getText());
		timings.put("Timings: " + F.f(getActionController().getReact().getSampleController().getSampleReactionTime().getValue().getDouble() / 1000000.0, 2), pagePollSamples());
		actions.put("Last Action Times", pagePollActions());
		book.addPage(stats);
		book.addPage(timings);
		book.addPage(actions);
		
		int pgs = 0;
		
		for(InstabilityCause i : problems.k().reverse())
		{
			GPage page = new GPage();
			page.put(i.getName() + "\n" + Info.COLOR_ERR + ChatColor.BOLD + " (Current Issue)", i.getProblem());
			book.addPage(page);
			pgs++;
		}
		
		for(GStub i : stubs.copy().reverse())
		{
			GPage page = new GPage();
			GTime time = new GTime(System.currentTimeMillis() - i.getTime().getTotalDuration());
			page.put(i.getTitle() + "\n" + Info.COLOR_ERR + ChatColor.BOLD + " (" + time.ago() + ")", i.getText());
			book.addPage(page);
			pgs++;
			
			if(pgs > 81)
			{
				break;
			}
		}
		
		return book;
	}
	
	public void manual(CommandSender p)
	{
		p.sendMessage(getName() + L.MESSAGE_ACTION_FULLY_AUTOMATIC);
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public boolean isLagging()
	{
		return lagging;
	}
	
	public int level(World w, int x, int z)
	{
		for(int i = 255; i > 0; i--)
		{
			if(!w.getBlockAt(x, i, z).getType().equals(Material.AIR))
			{
				return i + 1;
			}
		}
		
		return 255;
	}
	
	public void onNewConfig()
	{
		super.onNewConfig();
		
		cc.set(getCodeName() + ".low.tps", 16.8, "What is considered a low tps? Lag?");
		cc.set(getCodeName() + ".high.spms", 1, "Low SPMS");
		cc.set(getCodeName() + ".high.memory.percent", 0.8, "A high percent?");
		cc.set(getCodeName() + ".high.memory.chunk.percent", 0.9);
		cc.set(getCodeName() + ".high.liquid", 4096, "High liquids flow counts?");
		cc.set(getCodeName() + ".high.redstone", 4096, "High redstone counts?");
		cc.set(getCodeName() + ".high.tnt", 256, "High tnt/s counts?");
		cc.set(getCodeName() + ".high.chunk.generation", 18, "High chunk gens/s counts?");
		cc.set(getCodeName() + ".high.entity.count", 8192, "High counts?");
		cc.set(getCodeName() + ".high.drop.count", 1024);
		cc.set(getCodeName() + ".high.worldedit.bps", 10000, "High world edit BPS?");
		cc.set(getCodeName() + ".slow-fast-flyers-temporarily", true, "Slow down flyers who are flying at \nvery fast speeds until chunk lag has stopped");
	}
	
	public GPage queryPlugin(String name)
	{
		Plugin p = null;
		for(Plugin i : getActionController().getReact().getServer().getPluginManager().getPlugins())
		{
			if(i.getName().toLowerCase().contains(name.toLowerCase()))
			{
				p = i;
			}
		}
		
		if(p == null)
		{
			return null;
		}
		
		PluginDescriptionFile pdf = p.getDescription();
		GPage general = new GPage();
		general.put(p.getName(), ChatColor.DARK_AQUA + "Version: " + ChatColor.DARK_RED + pdf.getVersion() + "\n" + ChatColor.DARK_AQUA + "Enabled: " + ChatColor.DARK_RED + (p.isEnabled() ? "Yes" : "No") + "\n" + ChatColor.DARK_AQUA + "Sync Tasks: " + ChatColor.DARK_RED + tasks(p).size() + "\n" + ChatColor.DARK_AQUA + "Event Listeners: " + ChatColor.DARK_RED + listeners(p).size());
		
		return general;
	}
	
	public long guessPlayerLimit()
	{
		long playermb = getActionController().getReact().getSampleController().getSampleMemoryPerPlayer().getValue().getLong();
		long playerch = (long) (((((Bukkit.getServer().getViewDistance() * 2) + 1) * 2)) / 12.8);
		
		long playerimp = playermb + playerch;
		long max = getActionController().getReact().getSampleController().getSampleMemoryUsed().getMemoryMax() / 1024 / 1024;
		
		max -= getActionController().getReact().getSampleController().getSampleMemoryPerPlayer().base();
		return max / playerimp;
	}
	
	public String guessRedstoneLimit()
	{
		if(maxRedstone < 1)
		{
			maxRedstone = cc.getInt(getCodeName() + ".high.redstone");
		}
		
		return F.f(maxRedstone);
	}
	
	public long guessPlayerLimit(long mb)
	{
		long playermb = getActionController().getReact().getSampleController().getSampleMemoryPerPlayer().getValue().getLong();
		long playerch = (long) (((((Bukkit.getServer().getViewDistance() * 2) + 1) * 2)) / 12.8);
		
		long playerimp = playermb + playerch;
		long max = mb;
		
		max -= getActionController().getReact().getSampleController().getSampleMemoryPerPlayer().base();
		return max / playerimp;
	}
	
	public long guessPlayerOneChunk(long mb)
	{
		long playermb = getActionController().getReact().getSampleController().getSampleMemoryPerPlayer().getValue().getLong();
		long playerch = (long) (((((Bukkit.getServer().getViewDistance() * 2) + 1) * 2)) / 12.8);
		
		long playerimp = playermb - playerch;
		long max = mb;
		
		return max / playerimp;
	}
	
	public long guessPlayerOneChunk()
	{
		long playermb = getActionController().getReact().getSampleController().getSampleMemoryPerPlayer().getValue().getLong();
		long playerch = (long) (((((Bukkit.getServer().getViewDistance() * 2) + 1) * 2)) / 12.8);
		
		long playerimp = playermb - playerch;
		long max = getActionController().getReact().getSampleController().getSampleMemoryUsed().getMemoryMax() / 1024 / 1024;
		
		return max / playerimp;
	}
	
	public String queryGuess(long mb)
	{
		return Info.COLOR_ERR + "Guess: " + guessPlayerLimit(mb) + " ~ " + guessPlayerOneChunk(mb);
	}
	
	public GBook queryGuess()
	{
		GBook book = new GBook(ChatColor.AQUA + "React's Guess");
		GPage pg1 = new GPage();
		pg1.put("Player Limits", ChatColor.DARK_RED + "Guess: " + guessPlayerLimit() + " ~ " + guessPlayerOneChunk() + "\n" + ChatColor.DARK_RED + "Redstone: " + guessRedstoneLimit());
		book.addPage(pg1);
		return book;
	}
	
	public GList<BukkitTask> tasks(Plugin plugin)
	{
		GList<BukkitTask> br = new GList<BukkitTask>();
		
		for(BukkitTask i : getActionController().getReact().getServer().getScheduler().getPendingTasks())
		{
			if(i.getOwner().equals(plugin))
			{
				br.add(i);
			}
		}
		
		return br;
	}
	
	public GList<RegisteredListener> listeners(Plugin plugin)
	{
		return new GList<RegisteredListener>(HandlerList.getRegisteredListeners(plugin));
	}
	
	public GMap<InstabilityCause, Integer> getProblems()
	{
		return problems;
	}
	
	public GList<InstabilityCause> getNotified()
	{
		return notified;
	}
	
	public GList<GStub> getStubs()
	{
		return stubs;
	}
	
	public boolean issues()
	{
		if(!problems.isEmpty())
		{
			for(InstabilityCause i : problems.k())
			{
				if(!i.equals(InstabilityCause.LAG))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
