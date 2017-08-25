package com.volmit.react.sample;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import com.volmit.react.React;
import com.volmit.react.util.C;
import com.volmit.react.util.M;

public class TICK implements Listener
{
	public static long last = M.ms();
	public static long tick = 0;
	public static long length = 0;
	public static double tps = 20;
	public static double choke = 0;
	public static double pcMemUsed;
	public static long memUsed;
	public static long memTotal;
	public static long memFree;
	public static long memAlloc;
	public static long lmem = 0;
	public static long maht;
	public static long mahtx;
	public static long mahs;
	public static long amem;
	public static int gcs;
	public static int gc;
	public static int gcm = 0;
	public static int gcx;
	public static int worlds;
	public static int chunks;
	public static int cload = 0;
	public static int cgen = 0;
	public static int cunload = 0;
	public static int chunkLoads = 0;
	public static int chunkUnloads = 0;
	public static int chunkGens = 0;
	public static int entities;
	public static int drops;
	public static long sampleTime;
	public static long tst;
	public static int rstt = 0;
	public static int rsts = 0;
	public static int redstoneTick;
	public static int redstoneSecond;
	public static int grtt = 0;
	public static int grts = 0;
	public static int growthTick;
	public static int growthSecond;
	public static int butt = 0;
	public static int buts = 0;
	public static int burnTick;
	public static int burnSecond;
	public static int litt = 0;
	public static int lits = 0;
	public static int liquidTick;
	public static int liquidSecond;
	
	public static void tick()
	{
		tst = M.ns();
		tick++;
		length = (M.ms() - last) < 50 ? 50 : M.ms() - last;
		last = M.ms();
		choke = 50.0 / length;
		tps = choke * 20.0;
		memTotal = Runtime.getRuntime().maxMemory();
		memFree = Runtime.getRuntime().freeMemory();
		memAlloc = Runtime.getRuntime().totalMemory();
		memUsed = memAlloc - memFree;
		maht = memUsed - lmem >= 0 ? memUsed - lmem : mahs;
		mahtx += memUsed - lmem >= 0 ? memUsed - lmem : 0;
		gc += memUsed - lmem < 0 ? 1 : 0;
		gcx += memUsed - lmem < 0 ? 1 : 0;
		amem = memUsed - lmem < 0 ? memUsed : amem;
		pcMemUsed = amem / memTotal;
		gcs = tick % 20 == 0 ? gc : gcs;
		gcm = tick % 1200 == 0 ? gcx : gcm;
		mahs = tick % 20 == 0 ? mahtx : mahs;
		mahtx = tick % 20 == 0 ? 0 : mahtx;
		gc = tick % 20 == 0 ? 0 : gc;
		lmem = memUsed;
		worlds = 0;
		chunks = 0;
		worlds = Bukkit.getWorlds().size();
		chunks = countChunks();
		chunkGens = tick % 20 == 0 ? cgen : chunkGens;
		chunkLoads = tick % 20 == 0 ? cload : chunkLoads;
		chunkUnloads = tick % 20 == 0 ? cunload : chunkUnloads;
		cgen = tick % 20 == 0 ? 0 : cgen;
		cload = tick % 20 == 0 ? 0 : cload;
		cunload = tick % 20 == 0 ? 0 : cunload;
		redstoneTick = rstt;
		redstoneSecond = tick % 20 == 0 ? rsts : redstoneSecond;
		liquidSecond = tick % 20 == 0 ? lits : liquidSecond;
		burnSecond = tick % 20 == 0 ? buts : burnSecond;
		growthSecond = tick % 20 == 0 ? grts : growthSecond;
		rsts = tick % 20 == 0 ? 0 : rsts;
		grts = tick % 20 == 0 ? 0 : grts;
		buts = tick % 20 == 0 ? 0 : buts;
		lits = tick % 20 == 0 ? 0 : lits;
		growthTick = grtt;
		liquidTick = litt;
		burnTick = butt;
		grtt = 0;
		butt = 0;
		litt = 0;
		rstt = 0;
		entities = countEntities();
		drops = countDrops();
		sampleTime = M.ns() - tst;
		
		Bukkit.getConsoleSender().sendMessage(C.AQUA + "===============================================");
		
		for(Sampler i : React.i.getSc().getSamplers())
		{
			Bukkit.getConsoleSender().sendMessage(C.WHITE + i.getName() + ": " + i.onFormat());
		}
	}
	
	@EventHandler
	public void on(ChunkLoadEvent e)
	{
		if(e.isNewChunk())
		{
			cgen++;
		}
		
		cload++;
	}
	
	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		cunload++;
	}
	
	@EventHandler
	public void on(BlockRedstoneEvent e)
	{
		rsts++;
		rstt++;
	}
	
	@EventHandler
	public void on(BlockGrowEvent e)
	{
		grts++;
		grtt++;
	}
	
	@EventHandler
	public void on(BlockBurnEvent e)
	{
		buts++;
		butt++;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockPhysicsEvent e)
	{
		if(e.getChangedType().getId() >= 8 && e.getChangedType().getId() <= 11)
		{
			litt++;
			lits++;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockFormEvent e)
	{
		if(Material.getMaterial(e.getNewState().getTypeId()).equals(Material.FIRE))
		{
			buts++;
			butt++;
		}
	}
	
	@EventHandler
	public void on(BlockFadeEvent e)
	{
		if(e.getBlock().getType().equals(Material.FIRE))
		{
			buts++;
			butt++;
		}
	}
	
	@EventHandler
	public void on(BlockSpreadEvent e)
	{
		if(e.getSource().getType().equals(Material.FIRE))
		{
			buts++;
			butt++;
		}
	}
	
	@EventHandler
	public void on(BlockIgniteEvent e)
	{
		buts++;
		butt++;
	}
	
	public static int countChunks()
	{
		int k = 0;
		
		for(World i : Bukkit.getWorlds())
		{
			k += i.getLoadedChunks().length;
		}
		
		return k;
	}
	
	public static int countEntities()
	{
		int k = 0;
		
		for(World i : Bukkit.getWorlds())
		{
			k += i.getEntities().size();
		}
		
		return k;
	}
	
	public static int countDrops()
	{
		int k = 0;
		
		for(World i : Bukkit.getWorlds())
		{
			k += i.getEntitiesByClass(Item.class).size();
		}
		
		return k;
	}
}
