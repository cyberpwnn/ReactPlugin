package com.volmit.react;

import org.bukkit.plugin.java.JavaPlugin;
import com.volmit.cache.CachedSet;
import com.volmit.react.sample.TICK;
import com.volmit.react.sample.TickTimer;
import com.volmit.react.util.Execution;
import com.volmit.react.util.ParallelPoolManager;
import com.volmit.timings.StackTraceProbe;

public class React extends JavaPlugin
{
	public static Thread mainThread;
	public static RScheduler rs6;
	public static ParallelPoolManager pool;
	private SampleController sc;
	public static React i;
	private TickTimer timer;
	private StackTraceProbe probe;
	private CachedSet cacheSet;
	
	@Override
	public void onEnable()
	{
		// Preinit
		i = this;
		mainThread = Thread.currentThread();
		
		// Initialize Threads
		probe = new StackTraceProbe(mainThread);
		timer = new TickTimer();
		rs6 = new RScheduler();
		pool = new ParallelPoolManager(4);
		sc = new SampleController();
		cacheSet = new CachedSet(12, 32);
		
		// Start Threads
		pool.start();
		rs6.start();
		sc.start();
		timer.start();
		
		getServer().getPluginManager().registerEvents(cacheSet, this);
		
		// Tick Sample
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				TICK.tick();
				cacheSet.tick();
			}
		}, 0, 0);
		
		// Repeat per tick async
		rs6.schedRepeat(new Execution()
		{
			@Override
			public void run()
			{
				sc.tick();
			}
		});
	}
	
	@Override
	public void onDisable()
	{
		// Stop Threads
		rs6.interrupt();
		pool.shutdown();
		timer.interrupt();
	}
	
	public SampleController getSc()
	{
		return sc;
	}
	
	public void reportSpike()
	{
		probe.clear();
		probe.scan();
		// TODO Use the fucking data
	}
}
