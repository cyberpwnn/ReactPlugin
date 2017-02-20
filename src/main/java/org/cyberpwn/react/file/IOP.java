package org.cyberpwn.react.file;

import org.cyberpwn.react.React;
import net.md_5.bungee.api.ChatColor;

public abstract class IOP implements FOP
{
	protected FileHack h;
	
	public IOP(FileHack h)
	{
		this.h = h;
	}
	
	public void queue(FOP f)
	{
		h.queue(f);
	}
	
	@Override
	public void log(String op, CharSequence... s)
	{
		String m = op + ": ";
		
		for(CharSequence i : s)
		{
			m = m + ChatColor.RED + i + ChatColor.YELLOW + " -> ";
		}
		
		React.instance().getD().w(m);
	}
}
