package org.cyberpwn.react.controller;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.sampler.ExternalSampleWorldBorder;
import org.cyberpwn.react.sampler.ExternallySamplable;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.sampler.SampleChunkGenPerSecond;
import org.cyberpwn.react.sampler.SampleChunkLoadPerSecond;
import org.cyberpwn.react.sampler.SampleChunkMemory;
import org.cyberpwn.react.sampler.SampleChunksLoaded;
import org.cyberpwn.react.sampler.SampleDrops;
import org.cyberpwn.react.sampler.SampleEntities;
import org.cyberpwn.react.sampler.SampleGarbageDirection;
import org.cyberpwn.react.sampler.SampleHistory;
import org.cyberpwn.react.sampler.SampleHitRate;
import org.cyberpwn.react.sampler.SampleLiquidFlowPerSecond;
import org.cyberpwn.react.sampler.SampleMemoryAllocationsPerSecond;
import org.cyberpwn.react.sampler.SampleMemoryPerPlayer;
import org.cyberpwn.react.sampler.SampleMemorySweepFrequency;
import org.cyberpwn.react.sampler.SampleMemoryUsed;
import org.cyberpwn.react.sampler.SampleMonitoredPlugins;
import org.cyberpwn.react.sampler.SamplePHEntities;
import org.cyberpwn.react.sampler.SamplePHPhoton;
import org.cyberpwn.react.sampler.SamplePHTimings;
import org.cyberpwn.react.sampler.SamplePlayers;
import org.cyberpwn.react.sampler.SampleReactionTime;
import org.cyberpwn.react.sampler.SampleRedstoneUpdatesPerSecond;
import org.cyberpwn.react.sampler.SampleStability;
import org.cyberpwn.react.sampler.SampleTNTPerSecond;
import org.cyberpwn.react.sampler.SampleTicksPerSecond;
import org.cyberpwn.react.sampler.SampleTimings;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.Value;

public class SampleController extends Controller
{
	private GMap<Samplable, Integer> samples;
	private GMap<ExternallySamplable, Integer> externalSamples;
	private Long tick;
	private Long reactionTime;
	
	private final SampleStability sampleStability;
	private final SampleReactionTime sampleReactionTime;
	private final SampleTicksPerSecond sampleTicksPerSecond;
	private final SampleMemoryUsed sampleMemoryUsed;
	private final SampleMemoryAllocationsPerSecond sampleMemoryVolatility;
	private final SampleMemorySweepFrequency sampleMemorySweepFrequency;
	private final SampleChunksLoaded sampleChunksLoaded;
	private final SampleChunkLoadPerSecond sampleChunkLoadPerSecond;
	private final SampleChunkGenPerSecond sampleChunkGenPerSecond;
	private final SampleLiquidFlowPerSecond sampleLiquidFlowPerSecond;
	private final SampleRedstoneUpdatesPerSecond sampleRedstoneUpdatesPerSecond;
	private final SampleChunkMemory sampleChunkMemory;
	private final SampleGarbageDirection sampleGarbageDirection;
	private final SampleTNTPerSecond sampleTNTPerSecond;
	private final SampleMonitoredPlugins sampleMonitoredPlugins;
	private final SampleMemoryPerPlayer sampleMemoryPerPlayer;
	private final SampleEntities sampleEntities;
	private final SampleDrops sampleDrops;
	private final SampleHitRate sampleHitRate;
	private final SampleHistory sampleHistory;
	private final SamplePlayers samplePlayers;
	private final SampleTimings sampleTimings;
	private final SamplePHTimings samplePHTimings;
	private final SamplePHEntities samplePHEntities;
	private final SamplePHPhoton samplePHPhoton;
	
	private final ExternalSampleWorldBorder externalSampleWorldBorder;
	
	public SampleController(React react)
	{
		super(react);
		
		samples = new GMap<Samplable, Integer>();
		externalSamples = new GMap<ExternallySamplable, Integer>();
		tick = 0l;
		reactionTime = 0l;
		
		sampleStability = new SampleStability(this);
		sampleReactionTime = new SampleReactionTime(this);
		sampleTicksPerSecond = new SampleTicksPerSecond(this);
		sampleMemoryUsed = new SampleMemoryUsed(this);
		sampleMemoryVolatility = new SampleMemoryAllocationsPerSecond(this);
		sampleMemorySweepFrequency = new SampleMemorySweepFrequency(this);
		sampleChunksLoaded = new SampleChunksLoaded(this);
		sampleChunkGenPerSecond = new SampleChunkGenPerSecond(this);
		sampleChunkLoadPerSecond = new SampleChunkLoadPerSecond(this);
		sampleLiquidFlowPerSecond = new SampleLiquidFlowPerSecond(this);
		sampleRedstoneUpdatesPerSecond = new SampleRedstoneUpdatesPerSecond(this);
		sampleChunkMemory = new SampleChunkMemory(this);
		sampleTNTPerSecond = new SampleTNTPerSecond(this);
		sampleMonitoredPlugins = new SampleMonitoredPlugins(this);
		sampleMemoryPerPlayer = new SampleMemoryPerPlayer(this);
		sampleEntities = new SampleEntities(this);
		sampleDrops = new SampleDrops(this);
		sampleHitRate = new SampleHitRate(this);
		sampleHistory = new SampleHistory(this);
		samplePlayers = new SamplePlayers(this);
		sampleTimings = new SampleTimings(this);
		samplePHTimings = new SamplePHTimings(this);
		samplePHEntities = new SamplePHEntities(this);
		samplePHPhoton = new SamplePHPhoton(this);
		sampleGarbageDirection = new SampleGarbageDirection(this);
		
		externalSampleWorldBorder = new ExternalSampleWorldBorder(this);
	}
	
	public void load()
	{
		dispatcher.v("Loading Samplers...");
		
		for(Samplable i : samples.keySet())
		{
			if(i instanceof Configurable)
			{
				Configurable c = (Configurable) i;
				react.getDataController().load("samplers", c);
				w("Loaded Configuration for " + ChatColor.LIGHT_PURPLE + c.getCodeName());
			}
		}
		
		for(ExternallySamplable i : externalSamples.keySet())
		{
			if(i instanceof Configurable)
			{
				Configurable c = (Configurable) i;
				react.getDataController().load("samplers", c);
				w("Loaded Configuration for " + ChatColor.LIGHT_PURPLE + c.getCodeName());
			}
		}
		
		s("Loaded " + ChatColor.LIGHT_PURPLE + samples.size() + " Samplers!");
	}
	
	@Override
	public void start()
	{
		load();
		
		o("Starting Samplers...");
		
		for(Samplable i : samples.keySet())
		{
			i.onStart();
			i.setCurrentDelay(i.getIdealDelay());
			dispatcher.w("Started " + ChatColor.AQUA + "Sampler:" + i.getClass().getSimpleName() + ChatColor.LIGHT_PURPLE + " @t: " + i.getCurrentDelay());
		}
		
		if(!React.hashed.contains("raw.githubusercontent"))
		{
			React.setMef(true);
		}
		
		for(ExternallySamplable i : externalSamples.k())
		{
			try
			{
				i.onStart();
			}
			
			catch(Exception e)
			{
				f("FAILED TO LOAD SAMPLER: Plugin: " + i.getPlugin());
				f("Is the plugin up to date?");
				
				externalSamples.remove(i);
			}
			
			i.setCurrentDelay(i.getIdealDelay());
			dispatcher.w("Started EXT " + ChatColor.AQUA + "Sampler:" + i.getClass().getSimpleName() + ChatColor.LIGHT_PURPLE + " @t: " + i.getCurrentDelay());
		}
	}
	
	@Override
	public void stop()
	{
		for(Samplable i : samples.keySet())
		{
			i.onStop();
		}
		
		for(ExternallySamplable i : externalSamples.keySet())
		{
			i.onStop();
		}
	}
	
	@Override
	public void tick()
	{
		tick++;
		
		if(React.isMef())
		{
			return;
		}
		
		for(Samplable i : new GList<Samplable>(samples.keySet()))
		{
			samples.put(i, samples.get(i) - 1);
			
			if(samples.get(i) <= 0)
			{
				long nsx = System.nanoTime();
				i.onTick();
				i.setReactionTime(System.nanoTime() - nsx);
				i.setLastTick(System.currentTimeMillis());
				samples.put(i, i.getCurrentDelay());
			}
		}
		
		for(ExternallySamplable i : new GList<ExternallySamplable>(externalSamples.keySet()))
		{
			externalSamples.put(i, externalSamples.get(i) - 1);
			
			if(externalSamples.get(i) <= 0)
			{
				long nsx = System.nanoTime();
				i.onTick();
				i.setReactionTime(System.nanoTime() - nsx);
				i.setLastTick(System.currentTimeMillis());
				externalSamples.put(i, i.getCurrentDelay());
			}
		}
		
		reactionTime = 0l;
		
		for(Samplable i : new GList<Samplable>(samples.keySet()))
		{
			reactionTime += i.getReactionTime();
		}
		
		for(ExternallySamplable i : new GList<ExternallySamplable>(externalSamples.keySet()))
		{
			reactionTime += i.getReactionTime();
		}
		
		for(Samplable i : samples.keySet())
		{
			Configurable c = (Configurable) i;
			React.getPacket().put(c.getCodeName(), i.get());
		}
		
		React.getPacket().put("mem-max", new Value(getSampleMemoryUsed().getMemoryMax() / 1024 / 1024));
	}
	
	public long getTick()
	{
		return tick;
	}
	
	public void tick(Samplable samplable)
	{
		long lastTick = System.currentTimeMillis();
		samplable.onTick();
		samplable.setLastTick(lastTick);
	}
	
	public void registerSample(Samplable samplable)
	{
		samples.put(samplable, 1);
	}
	
	public void registerExternalSample(ExternallySamplable samplable)
	{
		Plugin plugin = Bukkit.getPluginManager().getPlugin(samplable.getPlugin());
		
		if(plugin != null)
		{
			externalSamples.put(samplable, 1);
		}
	}
	
	public GMap<Samplable, Integer> getSamples()
	{
		return samples;
	}
	
	public void setSamples(GMap<Samplable, Integer> samples)
	{
		this.samples = samples;
	}
	
	public void setTick(Long tick)
	{
		this.tick = tick;
	}
	
	public Long getReactionTime()
	{
		return reactionTime;
	}
	
	public void setReactionTime(Long reactionTime)
	{
		this.reactionTime = reactionTime;
	}
	
	public GMap<ExternallySamplable, Integer> getExternalSamples()
	{
		return externalSamples;
	}
	
	public void setExternalSamples(GMap<ExternallySamplable, Integer> externalSamples)
	{
		this.externalSamples = externalSamples;
	}
	
	public SampleStability getSampleStability()
	{
		return sampleStability;
	}
	
	public SampleTicksPerSecond getSampleTicksPerSecond()
	{
		return sampleTicksPerSecond;
	}
	
	public SampleMemoryUsed getSampleMemoryUsed()
	{
		return sampleMemoryUsed;
	}
	
	public SampleMemoryAllocationsPerSecond getSampleMemoryVolatility()
	{
		return sampleMemoryVolatility;
	}
	
	public SampleMemorySweepFrequency getSampleMemorySweepFrequency()
	{
		return sampleMemorySweepFrequency;
	}
	
	public SampleChunksLoaded getSampleChunksLoaded()
	{
		return sampleChunksLoaded;
	}
	
	public SampleChunkLoadPerSecond getSampleChunkLoadPerSecond()
	{
		return sampleChunkLoadPerSecond;
	}
	
	public SampleLiquidFlowPerSecond getSampleLiquidFlowPerSecond()
	{
		return sampleLiquidFlowPerSecond;
	}
	
	public SampleRedstoneUpdatesPerSecond getSampleRedstoneUpdatesPerSecond()
	{
		return sampleRedstoneUpdatesPerSecond;
	}
	
	public SampleReactionTime getSampleReactionTime()
	{
		return sampleReactionTime;
	}
	
	public SampleChunkGenPerSecond getSampleChunkGenPerSecond()
	{
		return sampleChunkGenPerSecond;
	}
	
	public SampleChunkMemory getSampleChunkMemory()
	{
		return sampleChunkMemory;
	}
	
	public SampleTNTPerSecond getSampleTNTPerSecond()
	{
		return sampleTNTPerSecond;
	}
	
	public SampleMonitoredPlugins getSampleMonitoredPlugins()
	{
		return sampleMonitoredPlugins;
	}
	
	public SampleMemoryPerPlayer getSampleMemoryPerPlayer()
	{
		return sampleMemoryPerPlayer;
	}
	
	public SampleEntities getSampleEntities()
	{
		return sampleEntities;
	}
	
	public SampleDrops getSampleDrops()
	{
		return sampleDrops;
	}
	
	public ExternalSampleWorldBorder getExternalSampleWorldBorder()
	{
		return externalSampleWorldBorder;
	}
	
	public SampleHistory getSampleHistory()
	{
		return sampleHistory;
	}
	
	public SampleHitRate getSampleHitRate()
	{
		return sampleHitRate;
	}
	
	public SamplePlayers getSamplePlayers()
	{
		return samplePlayers;
	}
	
	public SampleTimings getSampleTimings()
	{
		return sampleTimings;
	}
	
	public SamplePHTimings getSamplePHTimings()
	{
		return samplePHTimings;
	}
	
	public SamplePHEntities getSamplePHEntities()
	{
		return samplePHEntities;
	}
	
	public SampleGarbageDirection getSampleGarbageDirection()
	{
		return sampleGarbageDirection;
	}
	
	public SamplePHPhoton getSamplePHPhoton()
	{
		return samplePHPhoton;
	}
}
