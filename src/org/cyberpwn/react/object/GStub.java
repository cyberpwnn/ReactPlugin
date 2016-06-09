package org.cyberpwn.react.object;

import java.util.UUID;

public class GStub
{
	private String title;
	private String text;
	private GTime time;
	private UUID id;
	
	public GStub(String title, String text)
	{
		this.title = title;
		this.text = text;
		this.time = new GTime(System.currentTimeMillis());
		this.id = UUID.randomUUID();
	}
	
	public boolean equals(Object o)
	{
		if(o != null && (o instanceof GStub))
		{
			GStub gs = (GStub) o;
			
			if(gs.id.equals(id))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public GTime getTime()
	{
		return time;
	}
	
	public void setTime(GTime time)
	{
		this.time = time;
	}
	
	public UUID getId()
	{
		return id;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
}
