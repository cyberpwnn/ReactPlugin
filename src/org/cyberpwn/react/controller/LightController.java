package org.cyberpwn.react.controller;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.nms.NMS;
import org.cyberpwn.react.util.Cuboid;
import org.cyberpwn.react.util.ExecutiveIterator;
import org.cyberpwn.react.util.ExecutiveRunnable;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Timer;

public class LightController extends Controller implements Configurable
{
	private Boolean running;
	private ClusterConfig cc;
	private GList<Chunk> cache;
	
	public LightController(React react)
	{
		super(react);
		
		this.cc = new ClusterConfig();
		this.cache = new GList<Chunk>();
		this.running = false;
	}
	
	@Override
	public void tick()
	{
		if(!running && !cache.isEmpty())
		{
			running = true;
			relight(cache.get(0));
		}
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("photon.relight.enabled", false);
		cc.set("photon.relight.limits.max-ms", 2);
		cc.set("photon.relight.limits.max-cache-size", 1024);
		cc.set("photon.relight.limits.halt-on-lag", true);
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
		
		if(!cache.contains(c))
		{
			cache.add(c);
		}
	}
	
	public boolean visible(Block block)
	{
		return block.getRelative(BlockFace.UP).getType().equals(Material.AIR) || block.getRelative(BlockFace.NORTH).getType().equals(Material.AIR) || block.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR) || block.getRelative(BlockFace.EAST).getType().equals(Material.AIR) || block.getRelative(BlockFace.WEST).getType().equals(Material.AIR) || block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR);
	}
	
	public void relight(final Chunk chunk)
	{
		// pre
		final Timer t = new Timer();
		final GList<Block> intended = new GList<Block>();
		final Iterator<Block> bks = new Cuboid(chunk.getBlock(0, 0, 0).getLocation(), chunk.getBlock(15, 255, 15).getLocation()).iterator();
		final Integer[] cy = new Integer[] { 0 };
				
		t.start();
		
		new ExecutiveIterator<Block>(cc.getInt("photon.relight.limits.max-ms").longValue(), bks, new ExecutiveRunnable<Block>()
		{
			public void run()
			{
				if(!next().getType().equals(Material.AIR) && visible(next()))
				{
					intended.add(next());
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
						s("Relit in " + F.nsMs(t.getTime(), 0) + "ms " + ChatColor.AQUA + cy[0] + " blocks lit over " + F.f(((double) t.getTime() / 1000000.0) / 50.0) + " ticks" + ChatColor.LIGHT_PURPLE + " visible: " + F.pc(((double)cy[0] / 65536.0), 3) + ChatColor.RED + " " + cache.size() + " left");
						cache.remove(chunk);
						running = false;
					}
				});
			}
		});
	}
}
