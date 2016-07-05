package org.cyberpwn.react.json;

import org.cyberpwn.react.util.GMap;

public class Token
{
	public enum TokenType
	{
		HOVER, COMMAND, HOVER_COMMAND;
	}
	
	private GMap<String, String> params;
	private String contents;
	
	public Token()
	{
		this.params = new GMap<String, String>();
		this.contents = "";
	}
	
	public GMap<String, String> getParams()
	{
		return params;
	}
	
	public void setParams(GMap<String, String> params)
	{
		this.params = params;
	}
	
	public String getContents()
	{
		return contents;
	}
	
	public void setContents(String contents)
	{
		this.contents = contents;
	}
}
