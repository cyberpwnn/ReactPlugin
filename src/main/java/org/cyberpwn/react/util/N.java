package org.cyberpwn.react.util;

import org.cyberpwn.react.React;

public class N
{
	public static void t(String e, String... fs)
	{
		GMap<String, String> map = new GMap<String, String>();
		
		if(fs.length % 2 == 0 && fs.length > 0)
		{
			for(int i = 0; i < fs.length; i += 2)
			{
				try
				{
					map.put(fs[i], fs[i + 1]);
				}
				
				catch(Exception ex)
				{
					
				}
			}
		}
		
		React.instance().getNetworkController().trackANA(e, map);
	}
}
