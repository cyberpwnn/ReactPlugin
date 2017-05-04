package org.cyberpwn.react;

import java.util.List;
import org.cyberpwn.react.util.GList;

public class Version
{
	public static final int C = 4710;
	public static final String V = "4.7.1";
	public static final List<String> D = new GList<String>(new String[] {"Updater overhaul. Now downloads updates from spigot (you need to authenticate yourself)", "Added a guide on how to set this up on the release page", "Major performance improvements at startup", "You may need to reconfigure your updater.yml file"});
	
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
		
		if(vx.length() == 2)
		{
			vx += "00";
		}
		
		return Integer.valueOf(vx);
	}
}
