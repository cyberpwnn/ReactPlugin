package org.cyberpwn.react;

import java.util.List;
import org.cyberpwn.react.util.GList;

public class Version
{
	public static final int C = 4330;
	public static final String V = "4.3.3";
	public static final List<String> D = new GList<String>(new String[] {"Timings Processing is now much faster (on the cpu)", "Added timings-controller setting max threads for multithreaded processing", "Paperspigot now only takes 2 seconds to get the first batch of timings instead of 5 minutes", "Improved performance while timings are off"});
	
	public static String toV(int b)
	{
		String bu = String.valueOf(b);
		String v = "";
		
		for(Character i : bu.toCharArray())
		{
			v = v + i + ".";
		}
		
		if(bu.charAt(bu.length() - 1) == '0')
		{
			v = v.substring(0, v.length() - 3);
		}
		
		else
		{
			v = v.substring(0, v.length() - 1);
		}
		
		return v;
	}
	
	public static int toB(String v)
	{
		String vx = v.replaceAll("\\.", "");
		
		if(vx.length() == 3)
		{
			vx += "0";
		}
		
		return Integer.valueOf(vx);
	}
}
