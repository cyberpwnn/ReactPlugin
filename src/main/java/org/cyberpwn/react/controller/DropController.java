package org.cyberpwn.react.controller;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.ExecutiveIterator;
import org.cyberpwn.react.util.ExecutiveRunnable;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.IDrop;

public class DropController extends Controller implements Configurable
{
	private double range;
	private GList<Chunk> bl;
	private ClusterConfig cc;
	private GList<IDrop> drops;
	private boolean running;
	private double max;
	private boolean br;
	
	public DropController(React react)
	{
		super(react);
		
		cc = new ClusterConfig();
		drops = new GList<IDrop>();
		max = 1.0;
		bl = new GList<Chunk>();
		range = 12;
		br = false;
		running = false;
	}
	
	@EventHandler
	public void on(PlayerMoveEvent e)
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY())
		{
			handle();
		}
	}
	
	@Override
	public void stop()
	{
		for(IDrop i : drops.copy())
		{
			restore(i);
		}
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("range", 12, "The range a player must be to see drops");
		cc.set("enable", false, "Enable or disable this feature");
		cc.set("size", 256, "The max size of the cache");
		cc.set("max-time", 0.75, "The max milliseconds to spend on caching.");
	}
	
	@Override
	public void onReadConfig()
	{
		range = cc.getInt("range");
		max = cc.getDouble("max-time");
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "drops";
	}
	
	public void handle()
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		if(running)
		{
			return;
		}
		
		br = false;
		
		running = true;
		
		GList<Item> data = new GList<Item>();
		
		for(World i : Bukkit.getWorlds())
		{
			for(Entity j : i.getEntities())
			{
				if(j instanceof Item)
				{
					data.add((Item) j);
				}
			}
		}
		
		Collections.shuffle(data);
		
		new ExecutiveIterator<Item>(max, data, new ExecutiveRunnable<Item>()
		{
			@Override
			public void run(Item next)
			{
				if(br)
				{
					cancel();
					return;
				}
				
				handle(next);
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				GList<IDrop> dro = drops.copy();
				Collections.shuffle(dro);
				
				new ExecutiveIterator<IDrop>(max, dro, new ExecutiveRunnable<IDrop>()
				{
					@Override
					public void run(IDrop next)
					{
						if(br)
						{
							cancel();
							return;
						}
						
						handle(next);
					}
				}, new Runnable()
				{
					@Override
					public void run()
					{
						br = false;
						running = false;
					}
				});
			}
		});
	}
	
	public void handle(Item item)
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		if(!can(item))
		{
			return;
		}
		
		Area a = new Area(item.getLocation(), range);
		
		if(a.getNearbyPlayers().length == 0)
		{
			cache(item);
		}
	}
	
	public void handle(IDrop drop)
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		drop.update();
		
		if(drop.shouldDie())
		{
			drops.remove(drop);
			return;
		}
		
		Area a = new Area(drop.getLocation(), range);
		
		if(a.getNearbyPlayers().length > 0)
		{
			restore(drop);
		}
	}
	
	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		bl.add(e.getChunk());
		
		for(IDrop i : drops.copy())
		{
			if(i.getLocation().getChunk().equals(e.getChunk()))
			{
				restore(i);
			}
		}
	}
	
	@EventHandler
	public void on(ChunkLoadEvent e)
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		bl.remove(e.getChunk());
	}
	
	public void cache(Item item)
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		IDrop d = new IDrop(item);
		item.remove();
		drops.add(d);
	}
	
	public void restore(IDrop drop)
	{
		if(!cc.getBoolean("enable"))
		{
			return;
		}
		
		if(!drops.contains(drop))
		{
			return;
		}
		
		drops.remove(drop);
		drop.create();
	}
	
	public boolean can(Item item)
	{
		if(!cc.getBoolean("enable"))
		{
			return false;
		}
		
		Material m = item.getItemStack().getType();
		
		if(drops.size() > cc.getInt("size"))
		{
			return false;
		}
		
		if(bl.contains(item.getLocation().getChunk()))
		{
			return false;
		}
		
		if(item.getTicksLived() < 40)
		{
			return false;
		}
		
		switch(m)
		{
			case BOOK_AND_QUILL:
				return false;
			case POTION:
				return false;
			case WRITTEN_BOOK:
				return false;
			case LEATHER_BOOTS:
				return false;
			case LEATHER_CHESTPLATE:
				return false;
			case LEATHER_HELMET:
				return false;
			case LEATHER_LEGGINGS:
				return false;
			case ENCHANTED_BOOK:
				return false;
			case YELLOW_FLOWER:
				return false;
			case SEEDS:
				return false;
			case TORCH:
				return false;
			case RAILS:
				return false;
			case RED_MUSHROOM:
				return false;
			case RED_ROSE:
				return false;
			case STRING:
				return false;
			case GRAVEL:
				return false;
			case SAND:
				return false;
			case SUGAR_CANE:
				return false;
			case BROWN_MUSHROOM:
				return false;
			default:
				return true;
		}
	}
	
	public double getRange()
	{
		return range;
	}
	
	public void setRange(double range)
	{
		this.range = range;
	}
	
	public ClusterConfig getCc()
	{
		return cc;
	}
	
	public void setCc(ClusterConfig cc)
	{
		this.cc = cc;
	}
	
	public GList<IDrop> getDrops()
	{
		return drops;
	}
	
	public void setDrops(GList<IDrop> drops)
	{
		this.drops = drops;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public double getMax()
	{
		return max;
	}
	
	public void setMax(double max)
	{
		this.max = max;
	}
}
