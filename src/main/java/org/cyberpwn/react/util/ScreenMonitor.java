package org.cyberpwn.react.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.nms.Title;
import org.cyberpwn.react.sampler.Samplable;

public class ScreenMonitor
{
	private GMap<Samplable, GBiset<GList<Samplable>, Integer>> elements;
	private GList<Player> usingDisp;
	private final React react;
	private static ScreenMonitor ins;
	
	public ScreenMonitor(React react)
	{
		this.react = react;
		ins = this;
		usingDisp = new GList<Player>();
		elements = new GMap<Samplable, GBiset<GList<Samplable>, Integer>>();
		
		elements.put(this.react.getSampleController().getSampleTicksPerSecond(), new GBiset<GList<Samplable>, Integer>(new GList<Samplable>().qadd(this.react.getSampleController().getSampleStability()).qadd(this.react.getSampleController().getSampleReactionTime()), 0));
		elements.put(this.react.getSampleController().getSampleMemoryUsed(), new GBiset<GList<Samplable>, Integer>(new GList<Samplable>().qadd(this.react.getSampleController().getSampleMemoryVolatility()).qadd(this.react.getSampleController().getSampleMemorySweepFrequency()).qadd(this.react.getSampleController().getSampleGarbageDirection()), 1));
		elements.put(this.react.getSampleController().getSampleChunksLoaded(), new GBiset<GList<Samplable>, Integer>(new GList<Samplable>().qadd(this.react.getSampleController().getSampleChunkLoadPerSecond()).qadd(this.react.getSampleController().getSampleRedstoneUpdatesPerSecond()).qadd(this.react.getSampleController().getSampleMemoryPerPlayer()), 2));
		elements.put(this.react.getSampleController().getSamplePHEntities(), new GBiset<GList<Samplable>, Integer>(new GList<Samplable>().qadd(this.react.getSampleController().getSampleDrops()).qadd(this.react.getSampleController().getSampleEntities()), 3));
	}
	
	public static GMap<Samplable, GBiset<GList<Samplable>, Integer>> elements()
	{
		return ins.elements;
	}
	
	public void toggleDisp(Player p)
	{
		if(usingDisp.contains(p))
		{
			usingDisp.remove(p);
		}
		
		else
		{
			usingDisp.add(p);
		}
	}
	
	public void off(Player p)
	{
		usingDisp.remove(p);
	}
	
	public String getRoot()
	{
		GList<Samplable> samplables = new GList<Samplable>();
		String s = "";
		
		for(int max = 0; max < elements.size(); max++)
		{
			for(Samplable i : elements.keySet())
			{
				if(elements.get(i).getB() == max)
				{
					samplables.add(i);
				}
			}
		}
		
		for(Samplable i : samplables)
		{
			s = s + " " + i.color() + i.formatted(false);
		}
		
		return s;
	}
	
	public Title update(Integer cursor, boolean acc, boolean light)
	{
		Title t = new Title();
		String disp = react.getMonitorController().getDisp();
		t.setAction("");
		t.setFadeIn(0);
		t.setFadeOut(20);
		t.setStayTime(40);
		GList<Samplable> samplables = new GList<Samplable>();
		
		if(!React.instance().getConfiguration().getBoolean("monitor.shift-accuracy"))
		{
			acc = false;
		}
		
		for(int max = 0; max < elements.size(); max++)
		{
			for(Samplable i : elements.keySet())
			{
				if(elements.get(i).getB() == max)
				{
					samplables.add(i);
				}
			}
		}
		
		for(Samplable i : samplables)
		{
			if(cursor != null)
			{
				if(cursor == -1)
				{
					t.setAction(t.getAction() + " " + colorNight(light, i) + i.formatted(acc));
				}
				
				else if(samplables.get(cursor).equals(i))
				{
					t.setAction(t.getAction() + " " + colorNight(light, i) + i.formatted(acc));
				}
				
				else
				{
					t.setAction(t.getAction() + " " + ChatColor.BLACK + ChatColor.stripColor(i.formatted(acc)));
				}
			}
			
			else
			{
				t.setAction(t.getAction() + " " + colorNight(light, i) + i.formatted(acc));
			}
		}
		
		if(cursor != null && cursor != -1)
		{
			t.setSubTitle(" ");
			t.setTitle(" ");
			
			for(Samplable i : elements.get(samplables.get(cursor)).getA())
			{
				t.setSubTitle(t.getSubTitle() + " " + ChatColor.DARK_GRAY + " " + colorNight(light, i) + i.formatted(acc));
				t.setTitle(disp);
			}
		}
		
		if((cursor != null && cursor == -1) || cursor == null)
		{
			t.setSubTitle(disp);
			t.setTitle(" ");
		}
		
		return t;
	}
	
	public String colorNight(boolean c, Samplable i)
	{
		if(!c)
		{
			return i.color() + "" + ChatColor.BOLD;
		}
		
		return i.color() + "";
	}
	
	public int dec(int i)
	{
		if(i <= -1)
		{
			return elements.size() - 1;
		}
		
		return i - 1;
	}
	
	public int inc(int i)
	{
		if(i >= elements.size() - 1)
		{
			return -1;
		}
		
		return i + 1;
	}
	
	public boolean doubled(int m)
	{
		return m != -1;
	}
	
	public GMap<Samplable, GBiset<GList<Samplable>, Integer>> getElements()
	{
		return elements;
	}
	
	public void setElements(GMap<Samplable, GBiset<GList<Samplable>, Integer>> elements)
	{
		this.elements = elements;
	}
	
	public GList<Player> getIgnoreDisp()
	{
		return usingDisp;
	}
	
	public void setIgnoreDisp(GList<Player> ignoreDisp)
	{
		usingDisp = ignoreDisp;
	}
}
