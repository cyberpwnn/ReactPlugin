package org.cyberpwn.react.json;

public class TextToken
{
	/*
	 * === Lang ==== HOVER: [h='TEXT']HOVER TEXT[/] COMMAND: [c='/command arg
	 * arg']CLICK TO EXECUTE[/] HOVER + COMMAND: [h='TEXT' c='/command arg
	 * arg']HOVER TEXT[/] NOTE: Colors work on all elements.
	 * 
	 * 
	 * == Example ==
	 * 
	 * &aAqua text as normal, but [h='&cTHIS']&aGreen &fand &cRed text
	 * hovering![/] &a is cool isn't it? What about a [c='/suicide']&c Go kill
	 * yourself button?[/] &a Or, what if we warn the player before
	 * [h='&cCLICKING' c='/suicide']&cSeriously, don't click it.[/]
	 * 
	 * ==============
	 */
	
	public static RawText t(String text)
	{
		RawText rt = new RawText();
		
		return rt;
	}
}
