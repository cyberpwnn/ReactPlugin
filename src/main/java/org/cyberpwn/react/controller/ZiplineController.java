package org.cyberpwn.react.controller;

import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.ReactHopper;
import org.cyberpwn.react.util.TaskLater;

public class ZiplineController extends Controller implements Configurable
{
	private ClusterConfig cc;
	
	public ZiplineController(React react)
	{
		super(react);
		
		this.cc = new ClusterConfig();
	}
	
	@EventHandler
	public void on(InventoryMoveItemEvent e)
	{
		if(cc.getBoolean("zipline.enable") && (e.getSource().getHolder() instanceof Hopper))
		{
			e.setCancelled(true);
		}
		
		new TaskLater(1)
		{
			public void run()
			{
				if(cc.getBoolean("zipline.enable") && (e.getSource().getHolder() instanceof Hopper))
				{
					new ReactHopper((Hopper) e.getSource().getHolder()).transfer(cc.getInt("zipline.limits.max-search-distance"));
				}
			}
		};
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("zipline.enable", false, "Zips items through hoppers as fast as possible\nto help eliminate the long process of ticking hoppers so much.");
		cc.set("zipline.limits.max-search-distance", 12, "The max distance one hopper transfer can cover.\nOnce the distance is met, another hopper tick is required to zip again.\nSetting this too high can cause drastic performance problems!");
	}
	
	@Override
	public void onReadConfig()
	{
		// Dynamic
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "zipline";
	}
}
