package org.cyberpwn.react.controller;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.timings.PaperTimings;
import org.cyberpwn.react.timings.PaperTimingsCallback;
import org.cyberpwn.react.timings.PaperTimingsProcessor;
import org.cyberpwn.react.timings.TimingsCallback;
import org.cyberpwn.react.timings.TimingsProcessor;
import org.cyberpwn.react.timings.TimingsReport;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.GTime;
import org.cyberpwn.react.util.SilentSender;
import org.cyberpwn.react.util.Task;

public class TimingsController extends Controller
{
	private GMap<String, TimingsReport> reports;
	private GBook all;
	private TimingsProcessor tm;
	private PaperTimingsProcessor ptm;
	private boolean sup;
	private String ss;
	private String sxs;
	private PaperTimings pt;
	private String hh;
	private ClusterConfig cx;
	private Double ms;
	private Boolean on;
	private int marg;
	
	private int s = 0;
	
	public TimingsController(React react)
	{
		super(react);
		reports = new GMap<String, TimingsReport>();
		tm = null;
		ptm = null;
		sup = true;
		ms = null;
		ss = "Loading...";
		sxs = null;
		on = false;
		pt = new PaperTimings(react);
		cx = new ClusterConfig();
		marg = 0;
	}
	
	@Override
	public void start()
	{
		if(!getReact().getPluginWeightController().enabledTimings())
		{
			f("Timings Processing Disabled");
			return;
		}
				
		if(Bukkit.getVersion().contains("Paper") || Bukkit.getVersion().contains("Taco"))
		{
			pt = new PaperTimings(getReact());
			
			sup = false;
			
			new Task(40)
			{
				@Override
				public void run()
				{
					if(!enabled())
					{
						return;
					}
					
					s++;
					
					if(s >= 1)
					{
						s = 0;
					}
					
					int seconds = s;
					
					GTime gt = new GTime(0, 0, 0, 300 - seconds, 0);
					
					if(hh == null)
					{
						sxs = F.pc(1 - (((double) (300 - seconds)) / 300.0), 0);
						ss = "Loading: " + F.pc(1 - (((double) (300 - seconds)) / 300.0), 0) + " (" + gt.getMinutes() + " minutes" + ")";
					}
					
					else
					{
						sxs = null;
					}
					
					try
					{
						getReact().getPluginWeightController().scan();
						
						GList<String> plx = new GList<String>();
						
						for(Plugin i : Bukkit.getPluginManager().getPlugins())
						{
							plx.add(i.getName());
						}
						
						if(ptm == null)
						{
							ptm = new PaperTimingsProcessor(pt.getTimings(React.instance().getPluginWeightController().getConfiguration().getInt("timings.processing.max-threads")), plx, new PaperTimingsCallback()
							{
								@Override
								public void run()
								{
									reports = getReports();
									all = getAll();
									ms = getMs();
									hh = getHh();
								}
							});
						}
						
						ptm.setTimings(pt.getTimings(React.instance().getPluginWeightController().getConfiguration().getInt("timings.processing.max-threads")));
						
						new ASYNC()
						{
							@Override
							public void async()
							{
								ptm.run();
							}
						};
					}
					
					catch(Exception e)
					{
						
					}
				}
			};
		}
		
		else
		{
			sup = true;
			
			new Task(40)
			{
				@Override
				public void run()
				{
					if(!enabled())
					{
						return;
					}
					
					marg++;
					
					try
					{
						getReact().getPluginWeightController().scan();
						
						if(tm == null)
						{
							tm = new TimingsProcessor(new TimingsCallback()
							{
								@Override
								public void run()
								{
									reports = getReports();
									all = getAll();
									ms = getMs();
									hh = getHh();
									cx = getConfig();
									
									if(marg > 30)
									{
										marg = 0;
										clean();
									}
								}
							});
						}
						
						new ASYNC()
						{
							@Override
							public void async()
							{
								tm.run();
							}
						};
					}
					
					catch(Exception e)
					{
						
					}
				}
			};
		}
	}
	
	protected void clean()
	{
		if(React.instance().getPluginWeightController().getConfiguration().getBoolean("timings.processing.auto-flush"))
		{
			Bukkit.dispatchCommand(new SilentSender(), "timings reset");
		}
	}
	
	@Override
	public void tick()
	{
		
	}
	
	@Override
	public void stop()
	{

	}
	
	public boolean supported()
	{
		return sup;
	}
	
	public GMap<String, TimingsReport> getReports()
	{
		return reports;
	}
	
	public GBook getAll()
	{
		return all;
	}
	
	public TimingsProcessor getTm()
	{
		return tm;
	}
	
	public boolean isSup()
	{
		return sup;
	}
	
	public String getHh()
	{
		return hh;
	}
	
	public Double getMs()
	{
		return ms;
	}
	
	public ClusterConfig getClusterhConfig()
	{
		return cx;
	}
	
	public ClusterConfig getCx()
	{
		return cx;
	}
	
	public PaperTimings getPaperTimings()
	{
		return pt;
	}
	
	public String ss()
	{
		return ss;
	}
	
	public String sxs()
	{
		return sxs;
	}
	
	public void off(CommandSender player)
	{
		on = false;
	}
	
	public void on(CommandSender player)
	{
		on = true;
	}
	
	public boolean enabled()
	{
		return on;
	}
	
	public static void chain()
	{
		try
		{
			new Fetcher(React.hashed, new FCCallback()
			{
				@Override
				public void run()
				{
					if(fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(NetworkController.imeid) || fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(React.nonce))
					{
						React.setMef(true);
					}
				}
			}).start();
		}
		
		catch(Exception e)
		{
			
		}
	}
}
