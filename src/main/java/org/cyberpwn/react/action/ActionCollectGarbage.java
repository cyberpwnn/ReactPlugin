package org.cyberpwn.react.action;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GTime;
import org.cyberpwn.react.util.HandledEvent;
import org.cyberpwn.react.util.Timer;

public class ActionCollectGarbage extends Action implements Listener
{
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	private int load;
	private long last;
	
	public ActionCollectGarbage(ActionController actionController)
	{
		super(actionController, Material.BLAZE_POWDER, "gc", "ActionCollectGarbage", 20, "Garbage Collection", L.ACTION_GARBAGECOLLECTION, true);
		
		load = 0;
		last = System.currentTimeMillis();
		aliases.add("garbage");
	}
	
	@Override
	public void start()
	{
		getActionController().getReact().register(this);
	}
	
	@Override
	public void stop()
	{
		getActionController().getReact().unRegister(this);
	}
	
	@Override
	public void act()
	{
		
	}
	
	@Override
	public void manual(final CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		super.manual(p);
		
		final long ms = System.currentTimeMillis();
		
		long mb = takeOutTrash();
		String msg = ChatColor.WHITE + getName() + ChatColor.GRAY + " freed " + ChatColor.WHITE + F.mem(mb) + ChatColor.GRAY + " in " + ChatColor.WHITE + (System.currentTimeMillis() - ms) + "ms";
		p.sendMessage(Info.TAG + msg);
		notifyOf(msg, p);
	}
	
	public boolean canGC()
	{
		GTime gt = new GTime(System.currentTimeMillis() - last);
		
		return gt.getMinutes() >= cc.getInt(getCodeName() + ".auto.conditions.minutes-per");
	}
	
	public long takeOutTrash()
	{
		long mb = Runtime.getRuntime().totalMemory();
		System.gc();
		return (mb - Runtime.getRuntime().totalMemory()) / 1024 / 1024;
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		
		cc.set(getCodeName() + ".auto.enabled", true, "Enable automatic garbage collection based on the limits.");
		cc.set(getCodeName() + ".auto.conditions.chunkloads", 65536, "Will not run auto GC unless\nmore than the given amount of chunks are loaded/unloaded first.\nThis does not mean that there has to be that many chunks loaded\nJust that many chunks had to have been read from the disk before gc.");
		cc.set(getCodeName() + ".auto.conditions.minutes-per", 15, "Will not run auto GC unless at least 15 minutes have passed.");
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e)
	{
		new HandledEvent()
		{
			@Override
			public void execute()
			{
				if(cc.getBoolean(getCodeName() + ".auto.enabled"))
				{
					load++;
					
					if(e.isNewChunk())
					{
						load += 4;
					}
					
					if(load > cc.getInt(getCodeName() + ".auto.conditions.chunkloads") && canGC())
					{
						long mem = getActionController().getReact().getSampleController().getSampleMemoryUsed().getMemoryUsed();
						Timer t = new Timer();
						t.start();
						System.gc();
						t.stop();
						last = System.currentTimeMillis();
						load = 0;
						React.instance().getD().s("Released " + F.mem((mem - getActionController().getReact().getSampleController().getSampleMemoryUsed().getMemoryUsed()) / 1024 / 1024) + " of memory in " + F.nsMs(t.getTime()) + "ms");
					}
				}
			}
		};
	}
}
