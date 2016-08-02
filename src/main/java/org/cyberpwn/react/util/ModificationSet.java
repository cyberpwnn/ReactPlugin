package org.cyberpwn.react.util;

import java.io.Serializable;

public class ModificationSet implements Serializable
{
	private static final long serialVersionUID = 1L;
	private GList<BlockModification> blockModifications;
	
	public void apply()
	{
		for(BlockModification i : blockModifications)
		{
			i.apply();
		}
	}
}
