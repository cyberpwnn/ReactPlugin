package org.cyberpwn.react.action;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.MathUtils;

public class ActionSuppressRedstone extends Action implements Listener
{
	private boolean frozen;
	private boolean freezeAll;
	
	public ActionSuppressRedstone(ActionController actionController)
	{
		super(actionController, Material.REDSTONE, "cull-redstone", "ActionSuppressRedstone", 20, "Redstone Suppression", L.ACTION_SUPPRESSREDSTONE, true);
		frozen = false;
		freezeAll = false;
	}
	
	@Override
	public void act()
	{
		
	}
	
	public void freezeAll()
	{
		if(cc.getBoolean(getCodeName() + ".freeze-all-redstone-on-lag"))
		{
			freezeAll = true;
		}
	}
	
	public void unfreezeAll()
	{
		freezeAll = false;
	}
	
	public void freeze()
	{
		if(cc.getBoolean(getCodeName() + ".freeze-all-redstone-on-lag"))
		{
			frozen = true;
		}
	}
	
	public void unfreeze()
	{
		frozen = false;
	}
	
	@Override
	public void manual(final CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		super.manual(p);
		final long ms = System.currentTimeMillis();
		freeze();
		
		getActionController().getReact().scheduleSyncTask(20, new Runnable()
		{
			@Override
			public void run()
			{
				unfreeze();
				p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
			}
		});
	}
	
	@Override
	public void start()
	{
		getActionController().getReact().register(this);
	}
	
	@Override
	public void stop()
	{
		getActionController().getReact().unRegister(this);
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		
		cc.set(getCodeName() + ".freeze-all-redstone-on-lag", true, "Hault redstone on lag?");
		cc.set(getCodeName() + ".freeze-radius", 64, "The radius to freeze in a location specific redstone lag?");
	}
	
	@EventHandler
	public void onRedstone(BlockPhysicsEvent e)
	{
		if(!can(e.getBlock().getLocation()))
		{
			return;
		}
		
		try
		{
			if(freezeAll)
			{
				if(e.getChangedType().equals(Material.PISTON_BASE) || e.getChangedType().equals(Material.REDSTONE_LAMP_OFF) || e.getChangedType().equals(Material.REDSTONE_LAMP_ON) || e.getChangedType().equals(Material.PISTON_EXTENSION) || e.getChangedType().equals(Material.PISTON_MOVING_PIECE) || e.getChangedType().equals(Material.PISTON_STICKY_BASE) || e.getChangedType().equals(Material.REDSTONE_WIRE) || e.getChangedType().equals(Material.DIODE_BLOCK_OFF) || e.getChangedType().equals(Material.DIODE_BLOCK_ON) || e.getChangedType().equals(Material.REDSTONE_TORCH_OFF) || e.getChangedType().equals(Material.REDSTONE_TORCH_ON))
				{
					e.setCancelled(true);
				}
				
				return;
			}
			
			if(frozen)
			{
				if(e.getChangedType().equals(Material.PISTON_BASE) || e.getChangedType().equals(Material.REDSTONE_LAMP_OFF) || e.getChangedType().equals(Material.REDSTONE_LAMP_ON) || e.getChangedType().equals(Material.PISTON_EXTENSION) || e.getChangedType().equals(Material.PISTON_MOVING_PIECE) || e.getChangedType().equals(Material.PISTON_STICKY_BASE) || e.getChangedType().equals(Material.REDSTONE_WIRE) || e.getChangedType().equals(Material.DIODE_BLOCK_OFF) || e.getChangedType().equals(Material.DIODE_BLOCK_ON) || e.getChangedType().equals(Material.REDSTONE_TORCH_OFF) || e.getChangedType().equals(Material.REDSTONE_TORCH_ON))
				{
					try
					{
						if(MathUtils.isWithin(e.getBlock().getChunk(), getActionController().getReact().getSampleController().getSampleRedstoneUpdatesPerSecond().getChunk(), cc.getInt(getCodeName() + ".freeze-radius")))
						{
							e.setCancelled(true);
						}
					}
					
					catch(Exception ex)
					{
						
					}
				}
			}
		}
		
		catch(Exception ex)
		{
			
		}
	}
}
