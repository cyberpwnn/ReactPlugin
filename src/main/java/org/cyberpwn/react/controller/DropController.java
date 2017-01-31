package org.cyberpwn.react.controller;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.IDrop;
import org.cyberpwn.react.util.M;
import org.cyberpwn.react.util.Timer;

public class DropController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private GList<IDrop> drops;
	private long ns = 0;
	
	public DropController(React react)
	{
		super(react);
		
		drops = new GList<IDrop>();
		cc = new ClusterConfig();
	}
	
	@Override
	public void start()
	{
		
	}
	
	@Override
	public void tick()
	{
		ns = 0;
	}
	
	@EventHandler
	public void on(PlayerMoveEvent e)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		if(cc.getDouble("max-ms") * 1000000 < ns)
		{
			return;
		}
		
		Timer t = new Timer();
		t.start();
		if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY())
		{
			for(Chunk i : M.getChunks(e.getFrom().getChunk(), 3))
			{
				handle(i);
			}
		}
		t.stop();
		ns += t.getTime();
	}
	
	@Override
	public void stop()
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		for(IDrop i : drops.copy())
		{
			restore(i);
		}
	}
	
	public void handle(Chunk c)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		cache(c);
		restore(c);
	}
	
	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		restore(e.getChunk());
	}
	
	public void cache(Item drop)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		ItemStack is = drop.getItemStack();
		
		if(is.getType().equals(Material.ENCHANTED_BOOK) || is.getType().equals(Material.POTION) || is.getType().equals(Material.BOOK_AND_QUILL) || is.getType().equals(Material.WRITTEN_BOOK) || is.getType().equals(Material.LEATHER_BOOTS) || is.getType().equals(Material.LEATHER_CHESTPLATE) || is.getType().equals(Material.LEATHER_HELMET) || is.getType().equals(Material.LEATHER_LEGGINGS))
		{
			return;
		}
		
		for(Entity i : drop.getLocation().getChunk().getEntities())
		{
			if(i instanceof Player)
			{
				return;
			}
		}
		
		Area a = new Area(drop.getLocation(), 12.0);
		
		if(a.getNearbyPlayers().length < 1)
		{
			if(cc.getInt("max-cache") > drops.size())
			{
				drops.add(new IDrop(drop));
				drop.remove();
			}
		}
	}
	
	public void frestore(IDrop drop)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		drop.create();
		drops.remove(drop);
	}
	
	public void restore(IDrop drop)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		Area a = new Area(drop.getLocation(), 12.0);
		
		if(a.getNearbyPlayers().length > 0)
		{
			drop.create();
			drops.remove(drop);
		}
	}
	
	public void cache(Chunk chunk)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		for(Entity i : chunk.getEntities())
		{
			if(i instanceof Item)
			{
				cache((Item) i);
			}
		}
	}
	
	public void restore(Chunk chunk)
	{
		if(!cc.getBoolean("enabled"))
		{
			return;
		}
		
		for(IDrop i : drops.copy())
		{
			if(i.getLocation().getChunk().equals(chunk))
			{
				restore(i);
			}
		}
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("enabled", true, "Enable drop caching.\nDrop caching removes drops and re-creates them\ndepending on nearby players.");
		cc.set("max-cache", 1024, "The maximum drops to be cached");
		cc.set("max-ms", 0.4, "The maximum amount of time to spend \ncaching items out of player view per tick");
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
		return "drops";
	}
	
	public ClusterConfig getCc()
	{
		return cc;
	}
	
	public GList<IDrop> getDrops()
	{
		return drops;
	}
	
	public long getNs()
	{
		return ns;
	}
}
