package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import com.boydti.fawe.object.RunnableVal;
import com.boydti.fawe.util.TaskManager;

public class FAU
{
	private static WQ q = null;
	
	public static boolean canRun()
	{
		return Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null;
	}
	
	private static <T> T sync(RunnableVal<T> t)
	{
		return TaskManager.IMP.sync(t);
	}
	
	public static void sync(Runnable runnable)
	{
		if(!canRun())
		{
			return;
		}
		
		sync(new RunnableVal<Boolean>()
		{
			@Override
			public void run(Boolean arg0)
			{
				runnable.run();
			}
		});
	}
	
	public static void async(Runnable runnable)
	{
		if(!canRun())
		{
			return;
		}
		
		TaskManager.IMP.async(runnable);
	}
	
	public static WQ q()
	{
		if(!canRun())
		{
			return null;
		}
		
		if(q == null)
		{
			q = new WQ();
		}
		
		return q;
	}
}
