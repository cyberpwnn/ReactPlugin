package org.cyberpwn.react.action;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.object.GTime;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Timer;

import net.md_5.bungee.api.ChatColor;

public class ActionCollectGarbage extends Action implements Listener
{
	private int load;
	private long last;
	
	public ActionCollectGarbage(ActionController actionController)
	{
		super(actionController, Material.BLAZE_POWDER, "gc", "ActionCollectGarbage", 20, "Garbage Collection", L.ACTION_GARBAGECOLLECTION, true);
		
		load = 0;
		last = System.currentTimeMillis();
	}
	
	public void start()
	{
		getActionController().getReact().register(this);
	}
	
	public void stop()
	{
		getActionController().getReact().unRegister(this);
	}
	
	public void act()
	{
		
	}
	
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
		
		actionController.getReact().scheduleSyncTask(1, new Runnable()
		{
			@Override
			public void run()
			{
				takeOutTrash();
				p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
			}
		});
	}
	
	public boolean canGC()
	{
		GTime gt = new GTime(System.currentTimeMillis() - last);
		
		return gt.getMinutes() >= cc.getInt(getCodeName() + ".auto.conditions.minutes-per");
	}
	
	public void takeOutTrash()
	{
		System.gc();
	}
	
	public void onNewConfig()
	{
		super.onNewConfig();
		
		cc.set(getCodeName() + ".auto.enabled", true);
		cc.set(getCodeName() + ".auto.conditions.chunkloads", 65536);
		cc.set(getCodeName() + ".auto.conditions.minutes-per", 15);
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e)
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
}
