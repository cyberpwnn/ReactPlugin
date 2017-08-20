package org.cyberpwn.react.util;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.action.ActionStackEntities;

public class StackedEntity implements Listener
{
	private LivingEntity host;
	private Integer size;
	
	public StackedEntity(LivingEntity host, Integer size)
	{
		this.size = size;
		this.host = host;
		React.instance().register(this);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null)
		{
			return false;
		}
		
		if(o instanceof LivingEntity)
		{
			LivingEntity e = (LivingEntity) o;
			if(!e.equals(host))
			{
				return false;
			}
			
			return true;
		}
		
		if(o instanceof StackedEntity)
		{
			StackedEntity e = (StackedEntity) o;
			
			if(e.size != size)
			{
				return false;
			}
			
			if(!e.host.equals(host))
			{
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	public void on(EntityDeathEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				if(e.getEntity().equals(getHost()))
				{
					e.setDroppedExp(getSize() * e.getDroppedExp());
					getRoot().multiplyDrops(e.getDrops(), getSize());
					destroy();
				}
			}
		};
	}
	
	public void destroy()
	{
		React.instance().unRegister(this);
	}
	
	public void insert(LivingEntity e)
	{
		if(equals(e))
		{
			return;
		}
		
		React.instance().getActionController().getActionStackEntities().updateHealth(host, e);
		setSize(getSize() + getRoot().getSize(e));
		getRoot().animateStack(e, getHost());
		getRoot().removeStack(e);
		E.r(e);
		
		if(React.instance().getActionController().getActionStackEntities().getConfiguration().getBoolean("modifications.stack-sounds"))
		{
			try
			{
				new GSound(Sound.valueOf("CHICKEN_EGG_POP"), 1f, 0.6f + ((float) size) / (float) (React.instance().getActionController().getActionStackEntities().maxSize())).play(e.getLocation());
			}
			
			catch(Exception exx)
			{
				try
				{
					new GSound(Sound.valueOf("ENTITY_CHICKEN_EGG"), 1f, 0.6f + ((float) size) / (float) (React.instance().getActionController().getActionStackEntities().maxSize())).play(e.getLocation());
				}
				
				catch(Exception ex)
				{
					
				}
			}
		}
	}
	
	public ActionStackEntities getRoot()
	{
		return React.instance().getActionController().getActionStackEntities();
	}
	
	public LivingEntity getHost()
	{
		return host;
	}
	
	public Integer getSize()
	{
		return size;
	}
	
	public void setHost(LivingEntity host)
	{
		this.host = host;
	}
	
	public void setSize(Integer size)
	{
		this.size = size;
	}
}
