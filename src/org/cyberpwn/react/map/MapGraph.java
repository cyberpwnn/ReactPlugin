package org.cyberpwn.react.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapGraph extends MapRenderer
{
	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		Mapper.instance().render(canvas);
	}
}
