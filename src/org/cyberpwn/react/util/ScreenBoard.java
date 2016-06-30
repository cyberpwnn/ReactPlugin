package org.cyberpwn.react.util;

import org.cyberpwn.react.React;
import org.cyberpwn.react.sampler.Samplable;

public class ScreenBoard
{
	private GList<Samplable> elements;
	
	public ScreenBoard()
	{
		this.elements = new GList<Samplable>();
		
		GMap<Samplable, GBiset<GList<Samplable>, Integer>> sm = ScreenMonitor.elements();
		
		for(Samplable i : sm.k())
		{
			this.elements.add(i);
			
			for(Samplable j : sm.get(i).getA())
			{
				this.elements.add(j);
			}
		}
		
		elements.remove(React.instance().getSampleController().getSampleTimings());
		elements.remove(React.instance().getSampleController().getSamplePHEntities());
		elements.remove(React.instance().getSampleController().getSamplePHTimings());
	}
	
	public int inc(int i)
	{
		if(i <= -1)
		{
			return elements.size() - 1;
		}
		
		return i - 1;
	}
	
	public int dec(int i)
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

	public GList<Samplable> getElements()
	{
		return elements;
	}
}
