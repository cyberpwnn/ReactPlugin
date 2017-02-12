package org.cyberpwn.react.util;

import org.bukkit.Location;
import org.cyberpwn.react.controller.LagMapController;

public class Lag
{
	public static void report(Location l, InstabilityCause c, int s)
	{
		LagMapController.report(l, c, s);
	}
}
