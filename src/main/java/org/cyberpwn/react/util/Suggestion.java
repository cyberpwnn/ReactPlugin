package org.cyberpwn.react.util;

public enum Suggestion
{
	REBOOT_MORE_OFTEN("The server has been up for a while, its best to reboot more often.");
	
	private String msg;
	
	private Suggestion(String s)
	{
		this.msg = s;
	}
	
	public String msg()
	{
		return msg;
	}
}
