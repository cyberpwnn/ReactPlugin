package org.cyberpwn.react.util;

import java.util.Collections;
import java.util.Iterator;

import org.cyberpwn.react.React;
import org.cyberpwn.react.sampler.Samplable;

public class LogMesh
{
	private GMap<Samplable, Double> base;
	private GMap<Samplable, Double> range;
	
	public LogMesh()
	{
		base = new GMap<Samplable, Double>();
		range = new GMap<Samplable, Double>();
	}
	
	public void pull()
	{
		for(Samplable i : React.instance().getSampleController().getSamples().k())
		{
			base.put(i, i.get().getDouble());
		}
	}
	
	private GMap<Samplable, Double> pullRange()
	{
		GMap<Samplable, Double> dof = new GMap<Samplable, Double>();
		
		for(Samplable i : React.instance().getSampleController().getSamples().k())
		{
			range.put(i, i.get().getDouble());
		}
		
		for(Samplable i : base.k())
		{
			dof.put(i, M.dof(base.get(i), range.get(i)));
		}
		
		return dof;
	}
	
	public GBiset<GList<Samplable>, GMap<Samplable, Double>> push()
	{
		GList<Samplable> samp = new GList<Samplable>();
		GMap<Samplable, Double> dof = pullRange();
		GList<Double> flip = dof.v();
		Collections.sort(flip);
		Collections.reverse(samp);
		Iterator<Double> it = flip.iterator();
		
		while(it.hasNext())
		{
			Double v = it.next();
			
			for(Samplable i : dof.k())
			{
				if(dof.get(i) == v)
				{
					samp.add(i);
				}
			}
		}
		
		return new GBiset<GList<Samplable>, GMap<Samplable, Double>>(samp, dof);
	}
}
