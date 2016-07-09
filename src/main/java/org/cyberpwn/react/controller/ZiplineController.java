package org.cyberpwn.react.controller;

import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ReactHopper;

public class ZiplineController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private GList<ReactHopper> queue;
	
	public ZiplineController(React react)
	{
		super(react);
		
		this.cc = new ClusterConfig();
		this.queue = new GList<ReactHopper>();
	}
	
	@Override
	public void start()
	{
		
	}
	
	@Override
	public void stop()
	{
		
	}
	
	@Override
	public void tick()
	{
		
	}
	
	@EventHandler
	public void on(InventoryPickupItemEvent e)
	{
		if(e.getInventory().getHolder() instanceof Hopper)
		{
			ReactHopper hopper = new ReactHopper((Hopper) e.getInventory().getHolder());
			
		}
	}

	@Override
	public void onNewConfig(ClusterConfig cc)
	{

	}

	@Override
	public void onReadConfig()
	{
		//Dynamic
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
