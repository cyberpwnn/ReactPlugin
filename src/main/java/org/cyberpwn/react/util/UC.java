package org.cyberpwn.react.util;

import org.apache.commons.lang.StringUtils;

public class UC
{
	public static double cpu = -1;
	
	public static String getTerm()
	{
		return StringUtils.capitalize(getLoad().toString().toLowerCase()) + " Load";
	}
	
	public static LOAD getLoad()
	{
		LOAD l = LOAD.UNKNOWN;
		int c = (int) (cpu * 100);
		
		if(c > 0)
		{
			l = LOAD.IDLE;
		}
		
		if(c > 25)
		{
			l = LOAD.LIGHT;
		}
		
		if(c > 45)
		{
			l = LOAD.MODERATE;
		}
		
		if(c > 70)
		{
			l = LOAD.HEAVY;
		}
		
		if(c > 95)
		{
			l = LOAD.INTENSE;
		}
		
		if(c > 99)
		{
			l = LOAD.MAX;
		}
		
		return l;
	}
}
