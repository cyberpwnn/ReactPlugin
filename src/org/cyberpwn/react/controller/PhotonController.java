package org.cyberpwn.react.controller;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.nms.NMS;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.Cuboid;
import org.cyberpwn.react.util.ExecutiveIterator;
import org.cyberpwn.react.util.ExecutiveRunnable;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Timer;
import org.cyberpwn.react.util.W;

public class PhotonController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private GList<Chunk> photons;
	private GList<Chunk> prec;
	private GList<Chunk> cache;
	private Average accuracy;
	private Integer k;
	
	public PhotonController(React react)
	{
		super(react);
		
		this.cc = new ClusterConfig();
		this.cache = new GList<Chunk>();
		this.prec = new GList<Chunk>();
		this.photons = new GList<Chunk>();
		this.accuracy = new Average(32);
		this.k = 0;
	}
	
	@Override
	public void tick()
	{
		if(k > 4 && photons.size() < cc.getInt("photon.relight.limits.max-pool-size") && !cache.isEmpty() && !getReact().getActionController().getActionInstabilityCause().isLagging())
		{
			photons.add(cache.get(0));
			relight(cache.get(0));
			prec.add(cache.get(0));
			cache.pop();
			
			if(photons.size() < cc.getInt("photon.relight.limits.max-pool-size"))
			{
				k = 4;
			}
			
			else
			{
				k = 0;
				
				if(photons.size() > 100)
				{
					k = 4;
				}
			}
		}
		
		k++;
	}
	
	public long getLimit()
	{
		return 1 + (cc.getInt("photon.relight.limits.max-ms") / photons.size());
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("photon.relight.enabled", true);
		cc.set("photon.relight.limits.max-ms", 8);
		cc.set("photon.relight.limits.max-pool-size", 4);
		cc.set("photon.relight.limits.max-cache-size", 1024);
		cc.set("photon.relight.limits.halt-on-lag", true);
		cc.set("photon.relight.constraints.chunk-radius", 3);
	}
	
	@Override
	public void onReadConfig()
	{
		// Dynamic
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "photon";
	}
	
	public void request(Chunk c)
	{
		if(!cc.getBoolean("photon.relight.enabled"))
		{
			return;
		}
		
		if(cache.size() >= cc.getInt("photon.relight.limits.max-cache-size"))
		{
			return;
		}
		
		if(!cache.contains(c) && !prec.contains(c))
		{
			cache.addFirst(c);
		}
	}
	
	public boolean visible(Block block)
	{
		return block.getRelative(BlockFace.UP).getType().equals(Material.AIR) || block.getRelative(BlockFace.NORTH).getType().equals(Material.AIR) || block.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR) || block.getRelative(BlockFace.EAST).getType().equals(Material.AIR) || block.getRelative(BlockFace.WEST).getType().equals(Material.AIR) || block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR);
	}
	
	@EventHandler
	public void onChunk(ChunkUnloadEvent e)
	{
		prec.remove(e.getChunk());
		cache.remove(e.getChunk());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		if(!e.getFrom().getChunk().equals(e.getTo().getChunk()))
		{
			if(cc.getInt("photon.relight.constraints.chunk-radius") > 0)
			{
				for(Chunk i : W.crad(e.getTo().getChunk(), cc.getInt("photon.relight.constraints.chunk-radius")))
				{
					request(i);
				}
			}
			
			else
			{
				request(e.getTo().getChunk());
			}
		}
	}
	
	public void relight(final Chunk chunk)
	{
		// pre
		final Timer t = new Timer();
		final GList<Block> intended = new GList<Block>();
		final Iterator<Block> bks = new Cuboid(chunk.getBlock(0, 0, 0).getLocation(), chunk.getBlock(15, 255, 15).getLocation()).iterator();
		final Integer[] cy = new Integer[] { 0 };
		
		t.start();
		
		final String s = "lim: " + getLimit();
		new ExecutiveIterator<Block>(getLimit(), bks, new ExecutiveRunnable<Block>()
		{
			@SuppressWarnings("deprecation")
			public void run()
			{
				if(!next().getType().equals(Material.AIR) && visible(next()))
				{
					intended.add(next());
					
					Block b = next();
					Material m = b.getType();
					BlockState bs = b.getState();
					b.setType(Material.STONE);
					b.setType(m);
					b.setData(bs.getRawData());
					b.getState().update(false, true);
				}
			}
		}, new Runnable()
		{
			public void run()
			{
				new ExecutiveIterator<Block>(cc.getInt("photon.relight.limits.max-ms").longValue(), intended, new ExecutiveRunnable<Block>()
				{
					public void run()
					{
						NMS.instance().relight(next().getLocation());
						cy[0]++;
					}
				}, new Runnable()
				{
					public void run()
					{
						t.stop();
						accuracy.put(((double) cy[0] / 65536.0));
						s("Relit in " + F.nsMs(t.getTime(), 0) + "ms " + ChatColor.AQUA + cy[0] + " blocks lit over " + F.f(((double) t.getTime() / 1000000.0) / 50.0) + " ticks" + ChatColor.LIGHT_PURPLE + " visible: " + F.pc(((double) cy[0] / 65536.0), 3) + ChatColor.RED + " " + photons.size() + " in pool " + ChatColor.YELLOW + cache.size() + " cached" + ChatColor.BLUE + " " + s + " " + ChatColor.GOLD + "prec: " + prec.size());
						photons.remove(chunk);
					}
				});
			}
		});
	}

	public ClusterConfig getCc()
	{
		return cc;
	}

	public GList<Chunk> getPhotons()
	{
		return photons;
	}

	public GList<Chunk> getPrec()
	{
		return prec;
	}

	public GList<Chunk> getCache()
	{
		return cache;
	}

	public Integer getK()
	{
		return k;
	}
	
	public Double getAccuracy()
	{
		return accuracy.getAverage();
	}
}
