package org.cyberpwn.react.util;

import org.bukkit.entity.Player;

public class IndividualSlate extends PhantomSlate
{
	public IndividualSlate(String name, Player viewer)
	{
		super(name);
		
		addViewer(viewer);
	}
	
	/**
	 * This sets a new viewer (only supports one viewer)
	 */
	@Override
	public void addViewer(Player p)
	{
		viewers = new GList<Player>().qadd(p);
	}
}