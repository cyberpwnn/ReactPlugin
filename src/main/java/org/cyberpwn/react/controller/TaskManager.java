package org.cyberpwn.react.controller;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.network.ReactServer;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.Q;
import org.cyberpwn.react.util.Q.P;
import org.cyberpwn.react.util.Timer;
import net.md_5.bungee.api.ChatColor;

public class TaskManager extends Controller implements Configurable
{
	private GMap<Q.P, GList<Q>> queue;
	private ClusterConfig cc;
	private double throttle;
	private double delay;
	private double overflow;
	private Average usage;
	
	public TaskManager(React react)
	{
		super(react);
		
		cc = new ClusterConfig();
	}
	
	@Override
	public void start()
	{
		throttle = cc.getDouble("max-ms");
		delay = cc.getInt("cycle-interval");
		queue = new GMap<Q.P, GList<Q>>();
		usage = new Average(100);
	}
	
	public void queue(Q t)
	{
		try
		{
			if(!queue.containsKey(t.getPriority()))
			{
				queue.put(t.getPriority(), new GList<Q>());
			}
			
			queue.get(t.getPriority()).add(t);
			
			if(queue.get(t.getPriority()).size() > 8192)
			{
				return;
			}
			
			for(P i : queue.k())
			{
				while(queue.get(i).size() > 8192)
				{
					queue.get(i).remove(0);
				}
			}
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void tick()
	{
		try
		{
			if(delay > 0)
			{
				delay--;
			}
			
			delay = cc.getInt("cycle-interval");
			overflow = cycle();
			
			if(overflow / 1000000.0 > throttle)
			{
				delay += (cc.getInt("cycle-interval") * (overflow / 1000000.0 / throttle));
			}
			
			usage.put(overflow / 1000000.0);
			
			ReactServer.size = getSize();
			ReactServer.perc = getUsagePercent();
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void report()
	{
		for(P i : topDown())
		{
			int v = 0;
			
			if(queue.containsKey(i))
			{
				v = queue.get(i).size();
			}
			
			o(i.toString() + ": " + ChatColor.GREEN + F.f(v));
		}
		
		w("-----------------------------------------");
	}
	
	public int getSize()
	{
		int s = 0;
		
		for(P i : queue.k())
		{
			s += queue.get(i).size();
		}
		
		return s;
	}
	
	public long cycle()
	{
		if(queue.isEmpty())
		{
			return 0;
		}
		
		if(queue.size() > cc.getInt("boost.trigger-size"))
		{
			throttle = cc.getDouble("boost.boost-ms");
		}
		
		throttle = cc.getDouble("max-ms");
		double time = throttle * 1000000.0;
		
		while(time > 0)
		{
			time -= execute();
		}
		
		Timer tx = new Timer();
		tx.start();
		clean();
		tx.stop();
		
		return (long) (-(time) + tx.getTime());
	}
	
	public void clean()
	{
		if(getSize() > cc.getInt("skip-low-priority-trigger"))
		{
			for(Q.P i : topDown())
			{
				if(queue.containsKey(i))
				{
					for(Q j : queue.get(i).copy())
					{
						if(j.skippable())
						{
							queue.get(i).remove(j);
						}
					}
				}
			}
		}
		
		for(Q.P i : topDown())
		{
			if(queue.containsKey(i) && queue.get(i).isEmpty())
			{
				queue.remove(i);
			}
		}
	}
	
	public long execute()
	{
		Timer tx = new Timer();
		tx.start();
		long txx = 0;
		
		for(Q.P i : topDown())
		{
			if(queue.containsKey(i) && !queue.get(i).isEmpty())
			{
				txx = execute(queue.get(i).pop());
			}
		}
		
		tx.stop();
		return tx.getTime() + txx;
	}
	
	public GList<Q.P> topDown()
	{
		return new GList<Q.P>().qadd(P.HIGHEST).qadd(P.HIGH).qadd(P.NORMAL).qadd(P.LOW).qadd(P.LOWEST);
	}
	
	public long execute(Q t)
	{
		Timer tx = new Timer();
		tx.start();
		
		try
		{
			t.run();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		tx.stop();
		return tx.getTime();
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("max-ms", 0.3, "The maximum time in milliseconds react can use every cycle.");
		cc.set("cycle-interval", 1, "The interval to 'do work' in ticks");
		cc.set("skip-low-priority-trigger", 64, "Skip low priority tasks when the queue overflows this throttle");
		cc.set("boost.boost-ms", 0.6, "The boost max time in milliseconds react can use");
		cc.set("boost.trigger-size", 256, "The amount of queued jobs size trigger to enable boosting.");
	}
	
	@Override
	public void onReadConfig()
	{
		
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "task-manager";
	}
	
	public double getUsage()
	{
		return usage.getAverage();
	}
	
	public double getUsagePercent()
	{
		return getUsage() / throttle;
	}
	
	public boolean isBoosting()
	{
		return throttle > cc.getDouble("max-ms");
	}
	
	public GMap<P, GList<Q>> getQueue()
	{
		return queue;
	}
}
