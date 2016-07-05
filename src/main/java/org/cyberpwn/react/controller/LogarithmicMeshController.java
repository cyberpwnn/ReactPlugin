package org.cyberpwn.react.controller;

import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBiset;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.LogarithmicMesh;
import org.cyberpwn.react.util.State;

public class LogarithmicMeshController extends Controller
{
	private int tickDelay;
	private LogarithmicMesh mesh;
	private State state;
	
	public LogarithmicMeshController(React react)
	{
		super(react);
		
		tickDelay = 0;
		mesh = new LogarithmicMesh();
		state = State.DISABLED;
	}
	
	public void start()
	{
		state = State.PASSIVE;
	}
	
	public void stop()
	{
		state = State.DISABLED;
	}
	
	public void tick()
	{
		tickDelay++;
		
		if(tickDelay > 0)
		{
			tickDelay = 0;
			
			if(getReact().getSampleController().getSampleTicksPerSecond().get().getDouble() < 16.5)
			{
				state = State.ACTIVE;
				GBiset<GList<Samplable>, GMap<Samplable, Double>> log = mesh.push();
				GList<Samplable> order = log.getA();
				GMap<Samplable, Double> logs = log.getB();
				
				System.out.println("  ");
				for(Samplable i : order)
				{
					if(logs.get(i) > 0.122)
					{
						i.handleAction();
						w(i.getClass().getSimpleName() + " <" + ChatColor.LIGHT_PURPLE + "> " + F.pc(logs.get(i), 9));
					}
					
					else
					{
						f(i.getClass().getSimpleName() + " <" + ChatColor.LIGHT_PURPLE + "> " + F.pc(logs.get(i), 9));
					}
				}
				
				tickDelay = -20;
			}
			
			else
			{
				state = State.PASSIVE;
				mesh.pull();
			}
		}
	}
	
	public int getTickDelay()
	{
		return tickDelay;
	}
	
	public LogarithmicMesh getMesh()
	{
		return mesh;
	}
	
	public State getState()
	{
		return state;
	}
}
