package org.cyberpwn.react.util;

public class GPage
{
	private GMap<String, String> elements;
	
	public GPage()
	{
		this.elements = new GMap<String, String>();
	}
	
	public GPage put(String title, String paragraph)
	{
		if(title == null)
		{
			title = "";
		}
		
		if(paragraph == null)
		{
			paragraph = "";
		}
		
		elements.put(title, paragraph);
		return this;
	}
	
	public GMap<String, String> getElements()
	{
		return elements;
	}
	
	public void setElements(GMap<String, String> elements)
	{
		this.elements = elements;
	}
}
