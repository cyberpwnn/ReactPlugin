package org.cyberpwn.react.lang;

import org.cyberpwn.react.util.GList;

public class TextBlock
{
	private GList<String> block;
	
	public TextBlock()
	{
		block = new GList<String>();
	}
	
	public void add(String s)
	{
		block.add(s);
	}
	
	public GList<String> getBlock()
	{
		return block;
	}
}
