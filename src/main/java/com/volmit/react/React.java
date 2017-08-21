package com.volmit.react;

import org.bukkit.plugin.java.JavaPlugin;
import com.volmit.react.sample.Sampler;
import com.volmit.react.util.Execution;
import com.volmit.react.util.ParallelPoolManager;
import com.volmit.react.util.TICK;

public class React extends JavaPlugin
{
	public static RScheduler rs6;
	public static ParallelPoolManager pool;
	private SampleController sc;
	
	@Override
	public void onEnable()
	{
		rs6 = new RScheduler();
		pool = new ParallelPoolManager(4);
		sc = new SampleController();
		pool.start();
		rs6.start();
		sc.start();
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				TICK.tick();
			}
		}, 0, 0);
		
		rs6.schedRepeat(new Execution()
		{
			@Override
			public void run()
			{
				sc.tick();
				Sampler s = sc.getSampler("TPS");
				System.out.println("AVG: " + s.getValue().formatDoubleForce(2) + " RAW: " + s.getRawValue().formatDoubleForce(2) + " SAT: " + s.getSleeper().getSaturation() + "ms TRE: " + s.getValueTrend().toString());
			}
		});
	}
	
	@Override
	public void onDisable()
	{
		rs6.interrupt();
		pool.shutdown();
	}
}
