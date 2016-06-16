package org.cyberpwn.react.map;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MinecraftFont;
import org.cyberpwn.react.React;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;

public class Quadrant
{
	private static int THICKNESS = 3;
	private int sx;
	private int sy;
	private GMap<Byte, Double> logv;
	private GMap<Byte, GList<Double>> data;
	private GMap<Byte, Integer> pData;
	private Samplable sxm;
	private int xx = 0;
	
	public Quadrant(int sx, int sy)
	{
		this.sx = sx;
		this.sy = sy;
		logv = new GMap<Byte, Double>();
		data = new GMap<Byte, GList<Double>>();
		pData = new GMap<Byte, Integer>();
		sxm = React.instance().getSampleController().getSamples().k().pickRandom();
	}
	
	public void render(MapCanvas canvas)
	{
		for(Byte i : data.keySet())
		{
			int x = sx;
			
			for(Double j : data.get(i))
			{
				int y = (int) ((double) ((64 * j))) + sy;
				draw(canvas, i, x, y);
				
				if(pData.containsKey(i) && x > sx)
				{
					int oy = pData.get(i);
					
					if(oy > y)
					{
						for(int k = y; k < oy; k++)
						{
							draw(canvas, i, x, k);
						}
					}
					
					if(oy < y)
					{
						for(int k = oy; k < y; k++)
						{
							draw(canvas, i, x, k);
						}
					}
				}
				
				x++;
				pData.put(i, y);
			}
		}
	}
	
	public void renderLog(MapCanvas canvas)
	{
		for(Byte i : data.k())
		{
			double max = 1.0;
			
			for(Double j : data.get(i))
			{
				if(j > max)
				{
					max = j;
				}
			}
			
			int x = 1;
			
			while(x < max)
			{
				x *= 2;
			}
			
			x *= 2;
			
			logv.put(i, (double) (x));
		}
		
		for(Byte i : data.keySet())
		{
			int x = sx;
			
			for(Double jx : data.get(i))
			{
				Double j = jx;
				j = j / (logv.get(i));
				j = 1.0 - j;
				int y = (int) ((double) ((64 * j))) + sy;
				draw(canvas, i, x, y);
				
				if(pData.containsKey(i) && x > sx)
				{
					int oy = pData.get(i);
					
					if(oy > y)
					{
						for(int k = y; k < oy; k++)
						{
							draw(canvas, i, x, k);
						}
					}
					
					if(oy < y)
					{
						for(int k = oy; k < y; k++)
						{
							draw(canvas, i, x, k);
						}
					}
				}
				
				x++;
				pData.put(i, y);
			}
		}
	}
	
	public void renderLogLog(MapCanvas canvas)
	{
		xx++;
		
		if(xx > 44)
		{
			xx = 0;
			sxm = React.instance().getSampleController().getSamples().k().pickRandom();
		}
		
		
		MapFont mf = MinecraftFont.Font;
		canvas.drawText(5, 50, mf, F.f(React.instance().getSampleController().getSampleTicksPerSecond().get().getDouble(), 1) + " TPS");
		canvas.drawText(70, 5, mf, F.f(React.instance().getSampleController().getSampleMemoryUsed().get().getDouble(), 0) + " MB");
		canvas.drawText(5, 90, mf, sxm.formatted());
	}
	
	@SuppressWarnings("deprecation")
	public void renderDual(MapCanvas canvas, byte main, byte invis)
	{
		int x = sx;
		byte i = main;
		byte v = invis;
		
		if(data.get(i) == null)
		{
			return;
		}
		
		for(Double j : data.get(i))
		{
			int y = (int) ((double) ((64 * j))) + sy;
			int yi = (int) ((double) 64 - ((64 * data.get(v).get(x)))) + sy;
			
			if(y > yi)
			{
				for(int k = yi; k < y; k++)
				{
					draw(canvas, new GList<Byte>().qadd(MapPalette.DARK_GRAY).qadd(MapPalette.WHITE).pickRandom(), x, k);
				}
			}
			
			draw(canvas, i, x, y);
			
			if(pData.containsKey(i) && x > sx)
			{
				int oy = pData.get(i);
				
				if(oy > y)
				{
					for(int k = y; k < oy; k++)
					{
						draw(canvas, i, x, k);
					}
				}
				
				if(oy < y)
				{
					for(int k = oy; k < y; k++)
					{
						draw(canvas, i, x, k);
					}
				}
			}
			
			x++;
			pData.put(i, y);
		}
		
		for(Byte xi : data.keySet())
		{
			if(xi == main || xi == invis)
			{
				continue;
			}
			
			int xx = sx;
			
			for(Double j : data.get(xi))
			{
				int y = (int) ((double) ((64 * (j / 40)))) + sy;
				
				draw(canvas, xi, xx, y);
				
				if(pData.containsKey(xi) && xx > sx)
				{
					int oy = pData.get(xi);
					
					if(oy > y)
					{
						for(int k = y; k < oy; k++)
						{
							draw(canvas, xi, xx, k);
						}
					}
					
					if(oy < y)
					{
						for(int k = oy; k < y; k++)
						{
							draw(canvas, xi, xx, k);
						}
					}
				}
				
				xx++;
				pData.put(xi, y);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void draw(MapCanvas canvas, byte c, int x, int y)
	{
		if(c == MapPalette.RED || c == MapPalette.DARK_BROWN || c == MapPalette.PALE_BLUE)
		{
			for(int i = y; i < sy + 64; i++)
			{
				draw(canvas, c, x, i, true);
			}
			
			return;
		}
		
		if(c == MapPalette.BROWN)
		{
			draw(canvas, c, x, y, true);
			return;
		}
		
		for(int i = y; i < y + THICKNESS; i++)
		{
			draw(canvas, c, x, i, true);
		}
	}
	
	public void drawm(MapCanvas canvas, byte c, int x, int y)
	{
		if(y == 64)
		{
			y = 65;
		}
		
		draw(canvas, c, x, y, true);
	}
	
	public void draw(MapCanvas canvas, byte c, int x, int y, boolean safe)
	{
		if(!safe)
		{
			canvas.setPixel(x, y, c);
			return;
		}
		
		if(x < 128 && x >= 0 && y < 128 && y >= 0)
		{
			canvas.setPixel(x, y, c);
		}
	}
	
	public void put(Byte c, double d)
	{
		if(!data.containsKey(c))
		{
			data.put(c, new GList<Double>());
		}
		
		data.get(c).add(d);
		
		if(sx == 0 && sy == 64)
		{
			if(data.get(c).size() > 128)
			{
				data.get(c).remove(0);
			}
		}
		
		else
		{
			if(data.get(c).size() > 64)
			{
				data.get(c).remove(0);
			}
		}
	}
	
	public void flush()
	{
		data.clear();
		logv.clear();
		pData.clear();
	}
}
