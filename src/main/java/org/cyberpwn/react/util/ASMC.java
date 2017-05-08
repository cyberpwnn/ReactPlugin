package org.cyberpwn.react.util;

import java.io.IOException;
import org.bukkit.block.Block;
import org.cyberpwn.react.React;
import com.volmit.cocoa.Distinguishable;
import com.volmit.cocoa.Splittable;

public class ASMC implements Distinguishable, Splittable
{
	@Override
	public void a(Object arg0)
	{
		b(arg0);
		c((((Block) arg0).getState()));
	}
	
	@Override
	public void b(Object arg0)
	{
		d(arg0);
	}
	
	@Override
	public void c(Object arg0)
	{
		b(arg0);
	}
	
	@Override
	public void d(Object arg0)
	{
		a(arg0);
	}
	
	@Override
	public void e(Object arg0)
	{
		d(arg0);
		is(arg0);
	}
	
	@Override
	public void f(Object arg0)
	{
		System.gc();
	}
	
	@Override
	public void g(Object arg0)
	{
		try
		{
			System.in.read();
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void h(Object arg0)
	{
		a(arg0);
	}
	
	@Override
	public boolean is(Object arg0)
	{
		return React.instance().equals(arg0);
	}
	
	@Override
	public boolean what(Object arg0)
	{
		return !is(arg0) && when(arg0);
	}
	
	@Override
	public boolean when(Object arg0)
	{
		return !React.instance().isAsr();
	}
	
	@Override
	public boolean who(Object arg0)
	{
		return !!!React.instance().isNaggable();
	}
	
	@Override
	public boolean why(Object arg0)
	{
		return arg0.equals("because") && who(arg0) && what(React.instance().getCc());
	}
}
