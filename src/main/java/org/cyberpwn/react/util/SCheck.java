package org.cyberpwn.react.util;

public class SCheck
{
	private static String[] k = new String[]{"%", "%", "_", "_", "N", "O", "N", "C", "E", "_", "_", "%", "%"};
	
	public static String nc(String a, String b)
	{
		String n = "";
		String kx = "%%_" + "_" + "NONCE" + "__%" + "%";
		
		for(String i : k)
		{
			n = n + i;
		}
		
		if(kx.equals(k))
		{
			if(a.equals(b) && a.equals(k))
			{
				return null;
			}
			
			if(a.equals(k))
			{
				return b;
			}
			
			else
			{
				return a;
			}
		}
		
		else
		{
			return null;
		}
	}
}
