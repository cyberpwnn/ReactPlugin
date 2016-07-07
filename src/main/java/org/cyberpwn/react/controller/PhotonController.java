package org.cyberpwn.react.controller;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.Cuboid;
import org.cyberpwn.react.util.ExecutiveIterator;
import org.cyberpwn.react.util.ExecutiveRunnable;
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
	private Average power;
	private Integer k;
	private Integer j;
	
	public PhotonController(React react)
	{
		super(react);
		
		this.cc = new ClusterConfig();
		this.cache = new GList<Chunk>();
		this.prec = new GList<Chunk>();
		this.photons = new GList<Chunk>();
		this.accuracy = new Average(32);
		this.power = new Average(8);
		this.k = 0;
		this.j = 0;
	}
	
	@Override
	public void tick()
	{
		if(getReact().getSampleController().getSampleTicksPerSecond().get().getDouble() < cc.getDouble("photon.relight.constraints.clamp-below-tps"))
		{
			return;
		}
		
		if(k > 4 && photons.isEmpty() && cache.isEmpty() && photons.isEmpty())
		{
			
		}
		
		if(k > 4 && photons.size() < cc.getInt("photon.relight.limits.max-pool-size") && !cache.isEmpty() && !getReact().getActionController().getActionInstabilityCause().isLagging())
		{
			photons.add(cache.get(0));
			
			try
			{
				relight(cache.get(0));
			}
			
			catch(Exception e)
			{
				
			}
			
			prec.add(cache.get(0));
			cache.pop();
			
			if(photons.size() < cc.getInt("photon.relight.limits.max-pool-size"))
			{
				k = 4;
			}
			
			else
			{
				k = 0;
				
				if(cache.size() > 100)
				{
					k = 4;
				}
			}
		}
		
		if(j > 10)
		{
			j = 0;
			
			try
			{
				new ExecutiveIterator<Player>(1.0, new GList<Player>(getReact().onlinePlayers()), new ExecutiveRunnable<Player>()
				{
					@SuppressWarnings("deprecation")
					@Override
					public void run()
					{
						try
						{
							for(Chunk i : W.crad(next().getTargetBlock((HashSet<Byte>) null, 256).getLocation().getChunk(), 3))
							{
								request(i);
								j = 0;
							}
							
						}
						
						catch(Exception e)
						{
							
						}
					}
				}, new Runnable()
				{
					@Override
					public void run()
					{
						j = 0;
					}
				});
			}
			
			catch(Exception e)
			{
				
			}
		}
		
		k++;
		j++;
	}
	
	public void flush()
	{
		prec.clear();
	}
	
	public long getLimit()
	{
		return 1 + (cc.getInt("photon.relight.limits.max-ms") / photons.size());
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("photon.relight.enabled", false, "PHOTON! It removes plenty of lighting glitches with a cost...\nMORE CPU, LESS LIGHTING GLITCHES\nSet this to true to enable it.");
		cc.set("photon.relight.limits.max-ms", 8, "Max ms to use when correcting lighting glitches.");
		cc.set("photon.relight.limits.max-pool-size", 24, "Max amount of simultanious chunk light jobs at once. \nThis splits your max ms to the entire pool");
		cc.set("photon.relight.limits.max-cache-size", 1024, "Max size of the <to be lit> cache.");
		cc.set("photon.relight.constraints.chunk-radius", 2, "Radius of chunks to cache per sample.");
		cc.set("photon.relight.constraints.effectiveness-percent", 0.4, "The percent of effectiveness from 0 to 1.\nThe higher, the better, but more bandwidth and cpu is consumed.");
		cc.set("photon.relight.constraints.clamp-below-tps", 19.7, "Photon will stop lighting chunks after the tps has dropped below this value.\nUsually if you have a great cpu, default is fine.");
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
	
	@SuppressWarnings("deprecation")
	public boolean light(Block b)
	{
		if(!b.getType().equals(Material.AIR) && visible(b) && safe(b))
		{
			Material m = b.getType();
			BlockState bs = b.getState();
			b.setType(Material.REDSTONE_BLOCK);
			b.setType(m);
			b.setData(bs.getRawData());
			
			return true;
		}
		
		return false;
	}
	
	public boolean safe(Block b)
	{
		double k = cc.getDouble("photon.relight.constraints.effectiveness-percent");
		
		if(k > 1.0)
		{
			k = 1.0;
		}
		
		if(k < 0.01)
		{
			k = 0.01;
		}
		
		if(Math.random() < k)
		{
			return false;
		}
		
		if(b.getType().isTransparent())
		{
			return false;
		}
		
		if(b.getType().equals(Material.STATIONARY_WATER))
		{
			return false;
		}
		
		if(!b.getRelative(BlockFace.UP).getType().equals(Material.AIR) && b.getRelative(BlockFace.UP).getType().isTransparent())
		{
			return false;
		}
		
		for(Block i : W.getFaces(b))
		{
			if(i.getType().equals(Material.IRON_DOOR_BLOCK))
			{
				return false;
			}
			
			if(i.getType().equals(Material.WOODEN_DOOR))
			{
				return false;
			}
			
			if(i.getType().isSolid() && i.getType().isTransparent())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public void relight(final Chunk chunk)
	{
		final Timer t = new Timer();
		final GList<Block> intended = new GList<Block>();
		final Iterator<Block> bks = new Cuboid(chunk.getBlock(0, 0, 0).getLocation(), chunk.getBlock(15, 255, 15).getLocation()).iterator();
		final Integer[] cy = new Integer[] { 0 };
		
		t.start();
		
		new ExecutiveIterator<Block>((double)getLimit(), bks, new ExecutiveRunnable<Block>()
		{
			public void run()
			{
				if(light(next()))
				{
					cy[0]++;
					intended.add(next());
				}
			}
		}, new Runnable()
		{
			public void run()
			{
				t.stop();
				accuracy.put(((double) cy[0] / 65536.0));
				power.put((double) cy[0] / (t.getTime() / 50000000) * photons.size());
				photons.remove(chunk);
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
	
	public Double getPower()
	{
		return power.getAverage();
	}
	
	public int relightAll()
	{
		int kx = 0;
		
		flush();
		
		for(World i : Bukkit.getWorlds())
		{
			for(Chunk j : i.getLoadedChunks())
			{
				request(j);
				kx++;
			}
		}
		
		return kx;
	}
}
