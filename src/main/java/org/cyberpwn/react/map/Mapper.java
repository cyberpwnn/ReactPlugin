package org.cyberpwn.react.map;

import org.bukkit.Bukkit;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.util.GTimeBank;

public class Mapper
{
	private static Mapper instance;
	private Quadrant q1;
	private Quadrant q2;
	private Quadrant q3;
	
	public Mapper()
	{
		instance = this;
		q1 = new Quadrant(0, 0);
		q2 = new Quadrant(64, 0);
		q3 = new Quadrant(0, 64);
	}
	
	@SuppressWarnings("deprecation")
	public void updateSlow(GTimeBank tb)
	{
		q3.flush();
		
		for(Double i : tb.get("stability"))
		{
			q3.put(MapPalette.DARK_GREEN, i);
		}
		
		for(Double i : tb.get("players"))
		{
			if(i > 32)
			{
				for(Double j : tb.get("players"))
				{
					q3.put(MapPalette.BLUE, j);
				}
				
				break;
			}
		}
		
		for(Double i : tb.get("memory"))
		{
			q3.put(MapPalette.RED, i);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void sample(SampleController s)
	{
		try
		{
			q1.put(MapPalette.DARK_GREEN, s.getSampleTicksPerSecond().getPercent());
			q1.put(MapPalette.LIGHT_GREEN, s.getSampleStability().getValue().getDouble());
			q2.put(MapPalette.DARK_BROWN, 1.0 - s.getSampleChunkMemory().getPercent() * (s.getSampleMemoryUsed().getPercentAverage()));
			q2.put(MapPalette.PALE_BLUE, 1.0 - (Bukkit.getServer().getOnlinePlayers().size() * s.getSampleMemoryPerPlayer().getPercentOfMem()) * (s.getSampleMemoryUsed().getPercentAverage()) / 2);
			q2.put(MapPalette.RED, 1.0 - s.getSampleMemoryUsed().getPercentAverage());
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	@SuppressWarnings("deprecation")
	public void render(MapCanvas canvas)
	{
		clear(canvas);
		q1.renderDual(canvas, MapPalette.DARK_GREEN, MapPalette.LIGHT_GREEN);
		q2.render(canvas);
		q3.renderLogLog(canvas);
		grid(canvas);
	}
	
	@SuppressWarnings("deprecation")
	public void clear(MapCanvas canvas)
	{
		for(int i = 0; i < 128; i++)
		{
			for(int j = 0; j < 128; j++)
			{
				canvas.setPixel(i, j, MapPalette.WHITE);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void grid(MapCanvas canvas)
	{
		for(int i = 0; i < 128; i++)
		{
			for(int j = 0; j < 128; j++)
			{
				if((j == 64 || i == 64) && j < 65)
				{
					canvas.setPixel(i, j, MapPalette.GRAY_1);
				}
			}
		}
	}
	
	public static Mapper instance()
	{
		return instance;
	}
}
