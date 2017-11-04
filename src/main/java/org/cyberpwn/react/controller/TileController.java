package org.cyberpwn.react.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.InventoryHolder;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.util.C;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.HandledEvent;

public class TileController extends Controller implements Configurable
{
	private int maxTc;
	private ClusterConfig cc;

	public TileController(React react)
	{
		super(react);

		maxTc = 256;
		cc = new ClusterConfig();
	}

	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		new HandledEvent()
		{
			@Override
			public void execute()
			{
				if(!is())
				{
					return;
				}

				int btc = e.getBlock().getChunk().getTileEntities().length;

				if(e.getBlock().getState() instanceof InventoryHolder)
				{
					btc++;
				}

				if(btc > maxTc)
				{
					e.setCancelled(true);
					e.getPlayer().sendMessage(Info.TAG + C.RED + F.color(cc.getString("deny-message")));
				}
			}
		};
	}

	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("max-tiles-per-chunk", 256);
		cc.set("deny-message", "There are too many tile entities in this chunk!");
		cc.set("enable-tile-control", false);
	}

	@Override
	public void onReadConfig()
	{
		maxTc = cc.getInt("max-tiles-per-chunk");
	}

	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}

	@Override
	public String getCodeName()
	{
		return "tile-controller";
	}

	public boolean is()
	{
		return cc.getBoolean("enable-tile-control");
	}

	public int getMaxTc()
	{
		return maxTc;
	}
}
