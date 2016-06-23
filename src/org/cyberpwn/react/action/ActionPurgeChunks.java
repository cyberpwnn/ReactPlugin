package org.cyberpwn.react.action;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ChunkPurgeEvent;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.M;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.Verbose;

public class ActionPurgeChunks extends Action implements Listener
{
	private int limit;
	
	public ActionPurgeChunks(ActionController actionController)
	{
		super(actionController, Material.GRASS, "purge-chunks", "ActionPurgeChunks", 20, "Purge Chunks", L.ACTION_PURGECHUNKS, true);
		
		limit = 60;
	}
	
	public void act()
	{
		limit--;
		
		if(limit <= 0)
		{
			limit = 0;
			
			if(getActionController().getReact().getSampleController().getSampleChunksLoaded().getValue().getDouble() / getActionController().getReact().onlinePlayers().length > cc.getInt("culls.limit-per-player"))
			{
				purge(null);
				limit = cc.getInt("culls.limit-interval-seconds");
			}
		}
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
		
		actionController.getReact().scheduleSyncTask(1, new Runnable()
		{
			@Override
			public void run()
			{
				purge(p);
			}
		});
	}
	
	public void purge(CommandSender p)
	{
		ChunkPurgeEvent cpe = new ChunkPurgeEvent();
		React.instance().getServer().getPluginManager().callEvent(cpe);
		GList<Chunk> ignore = new GList<Chunk>(cpe.getIgnore());
		
		long limit = cc.getInt("limit-ms");
		long mlimit = limit / Bukkit.getServer().getWorlds().size();
		
		if(p != null)
		{
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Purging Chunks...");
		}
		
		for(World i : Bukkit.getServer().getWorlds())
		{
			purge(i, mlimit, p, ignore);
		}
	}
	
	public void purge(final World world, final long limit, final CommandSender p, final GList<Chunk> ignore)
	{
		final Iterator<Chunk> it = new GList<Chunk>(world.getLoadedChunks()).iterator();
		final int[] mx = new int[] { 0 };
		
		if(p != null)
		{
			p.sendMessage(Info.TAG + ChatColor.GREEN + "Started Cull @ " + world.getName());
		}
		
		new Task(0)
		{
			public void run()
			{
				long ms = M.ms();
				int m = 0;
				
				while(it.hasNext() && M.ms() - ms < limit)
				{
					Chunk c = it.next();
					boolean safe = true;
					
					for(Entity i : c.getEntities())
					{
						if(E.isNPC(i))
						{
							safe = false;
							break;
						}
					}
					
					if(ignore.contains(c))
					{
						safe = false;
					}
					
					if(safe)
					{
						if(c.unload(true, true))
						{
							m++;
						}
					}
					
					else
					{
						Verbose.x("Purger", "Ignoring Chunk @ " + c.getWorld().getName() + " [" + c.getX() + "," + c.getZ() + "]" + " as it has npcs in it or has been ignored.");
					}
				}
				
				mx[0] += m;
				
				Verbose.x("Chunk Culler", "Unloaded " + m + " " + world.getName() + " chunks in " + (M.ms() - ms) + "ms");
				
				if(!it.hasNext())
				{
					if(p != null)
					{
						p.sendMessage(Info.TAG + ChatColor.GREEN + "Finished Cull @ " + world.getName() + ", culled " + mx[0] + " chunks.");
					}
					
					cancel();
				}
			}
		};
	}
	
	public void onReadConfig()
	{
		super.onReadConfig();
		
		limit = cc.getInt("culls.limit-interval-seconds");
	}
	
	public void onNewConfig()
	{
		super.onNewConfig();
		
		cc.set("limit-ms", 26);
		cc.set("culls.limit-interval-seconds", 60);
		cc.set("culls.limit-per-player", 750);
	}
}
