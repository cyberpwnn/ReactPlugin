package com.volmit.react;

import com.volmit.react.core.SampledValue;
import com.volmit.react.sample.ReactSampler;
import com.volmit.react.sample.Sampler;
import com.volmit.react.sample.TICK;
import com.volmit.react.sample.TickTimer;
import com.volmit.react.util.C;
import com.volmit.react.util.F;
import com.volmit.react.util.GList;

public class SampleController
{
	private GList<Sampler> samplers;
	
	public SampleController()
	{
		samplers = new GList<Sampler>();
		React.i.getServer().getPluginManager().registerEvents(new TICK(), React.i);
	}
	
	public void start()
	{
		registerSampler(new ReactSampler("TPS", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.tps);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(20);
			}
			
			@Override
			public String onFormat()
			{
				return C.GREEN + getValue().formatDouble(0);
			}
		});
		
		registerSampler(new ReactSampler("SAMPLETIME", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.sampleTime);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(20);
			}
			
			@Override
			public String onFormat()
			{
				return C.GREEN + F.nsMs(getValue().getLong(), 1) + "ms";
			}
		});
		
		registerSampler(new ReactSampler("TICK", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TickTimer.tickTime);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				long nsx = 50000000 - getValue().getLong();
				return C.GREEN + F.nsMs(nsx < 0 ? 0 : nsx, 1) + "ms";
			}
		});
		
		registerSampler(new ReactSampler("MEM", 10)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.amem);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(4);
			}
			
			@Override
			public String onFormat()
			{
				return C.GOLD + F.memSize(getValue().getLong());
			}
		});
		
		registerSampler(new ReactSampler("MAH/S", 5)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.mahs);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(3);
			}
			
			@Override
			public String onFormat()
			{
				return C.GOLD + F.memSize(getValue().getLong());
			}
		});
		
		registerSampler(new ReactSampler("MAH/T", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.maht);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				return C.GOLD + F.memSize(getValue().getLong());
			}
		});
		
		registerSampler(new ReactSampler("GC/S", 20)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.gcs);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(2);
			}
			
			@Override
			public String onFormat()
			{
				return C.GOLD + "" + getValue().getLong();
			}
		});
		
		registerSampler(new ReactSampler("GC/M", 20)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.gcm == 0 ? TICK.gcs * 60 : TICK.gcm);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(2);
			}
			
			@Override
			public String onFormat()
			{
				return C.GOLD + "" + getValue().getLong();
			}
		});
		
		registerSampler(new ReactSampler("WORLDS", 20)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.worlds);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(1);
			}
			
			@Override
			public String onFormat()
			{
				return C.RED + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("CHUNKS", 20)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.chunks);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(1);
			}
			
			@Override
			public String onFormat()
			{
				return C.RED + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("CLOAD/S", 20)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.chunkLoads);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(2);
			}
			
			@Override
			public String onFormat()
			{
				return C.RED + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("CGEN/S", 20)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.chunkGens);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(2);
			}
			
			@Override
			public String onFormat()
			{
				return C.RED + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("CUNLOAD/S", 20)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.chunkUnloads);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(2);
			}
			
			@Override
			public String onFormat()
			{
				return C.RED + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("ENTS", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.entities);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				return C.AQUA + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("DROPS", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.drops);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				return C.AQUA + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("RED/T", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.redstoneTick);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("RED/S", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.redstoneSecond);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(20);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("GROW/T", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.growthTick);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("GROW/S", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.growthSecond);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(20);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("BURN/T", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.burnTick);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("BURN/S", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.burnSecond);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(20);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("LIQUID/T", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.liquidTick);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(5);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
		
		registerSampler(new ReactSampler("LIQUID/S", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.liquidSecond);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(20);
			}
			
			@Override
			public String onFormat()
			{
				return C.LIGHT_PURPLE + "" + getValue().formatLong();
			}
		});
	}
	
	public Sampler getSampler(String name)
	{
		for(Sampler i : samplers)
		{
			if(i.getName().equals(name))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void tick()
	{
		for(Sampler i : samplers)
		{
			i.onPreSample();
		}
	}
	
	public void registerSampler(Sampler s)
	{
		samplers.add(s);
	}
	
	public GList<Sampler> getSamplers()
	{
		return samplers;
	}
}
