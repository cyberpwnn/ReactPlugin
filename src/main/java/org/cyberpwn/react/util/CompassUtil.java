package org.cyberpwn.react.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CompassUtil
{
	public static void updatePlayerCompassFor(Player p, double percent)
	{
		percent = percent > 1 ? 1 : percent;
		Location ra = p.getLocation().clone();
		CNum cx = new CNum(360);
		cx.set((int) (Math.min((365.0 * percent), 365.0) + p.getLocation().getYaw()) + 180);
		ra.setYaw(cx.get());
		Vector va = ra.getDirection();
		Location c = ra.clone().add(va.clone().multiply(16));
		
		p.setCompassTarget(c);
	}
}
