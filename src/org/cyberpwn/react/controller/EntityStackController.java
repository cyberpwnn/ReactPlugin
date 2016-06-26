package org.cyberpwn.react.controller;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.Verbose;

public class EntityStackController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private GMap<Integer, Integer> stacks;
	
	private Boolean enabled;
	
	public EntityStackController(React react)
	{
		super(react);
		
		enabled = false;
		stacks = new GMap<Integer, Integer>();
	}
	
	public boolean isStacked(LivingEntity e)
	{
		if(!enabled)
		{
			return false;
		}
		
		return stacks.containsKey(e.getEntityId());
	}
	
	public boolean canTouch(LivingEntity e)
	{
		if(!enabled)
		{
			return false;
		}
		
		return true;
	}
	
	public void stop()
	{
		if(!enabled)
		{
			return;
		}
		
		for(World i : Bukkit.getWorlds())
		{
			for(LivingEntity j : i.getLivingEntities())
			{
				if(stacks.contains(j.getEntityId()))
				{
					j.setCustomName(null);
				}
			}
		}
		
		stacks.clear();
	}
	
	@SuppressWarnings("deprecation")
	public void update(LivingEntity e)
	{
		if(!enabled)
		{
			return;
		}
		
		if(isStacked(e))
		{
			e.setCustomName(ChatColor.AQUA + "" + stacks.get(e.getEntityId()) + " X " + ChatColor.LIGHT_PURPLE + StringUtils.capitalise(e.getType().toString().toLowerCase().replaceAll("_", " ")));
		}
		
		else
		{
			e.setCustomName(null);
		}
		
		Verbose.x("STACKS", stacks.size() + " stacks");
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(EntityDamageEvent e)
	{
		if(!enabled)
		{
			return;
		}
		
		if(e.getEntity() instanceof LivingEntity)
		{
			if(isStacked((LivingEntity) e.getEntity()))
			{
				update((LivingEntity) e.getEntity());
			}
		}
	}
	
	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		if(!enabled)
		{
			return;
		}
		
		for(Entity i : e.getChunk().getEntities())
		{
			if(i instanceof LivingEntity)
			{
				LivingEntity x = (LivingEntity) i;
				
				if(isStacked(x))
				{
					x.setCustomName(null);
					stacks.remove(x.getEntityId());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(EntityDeathEvent e)
	{
		if(!enabled)
		{
			return;
		}
		
		if(e.getEntity() instanceof LivingEntity)
		{
			if(isStacked(e.getEntity()))
			{
				stacks.put(e.getEntity().getEntityId(), stacks.get(e.getEntity().getEntityId()) - 1);
				
				if(stacks.get(e.getEntity().getEntityId()) > 1)
				{
					e.getEntity().setHealth(e.getEntity().getMaxHealth());
				}
				
				else if(stacks.get(e.getEntity().getEntityId()) > 0)
				{
					e.getEntity().setHealth(e.getEntity().getMaxHealth());
					stacks.remove(e.getEntity().getEntityId());
				}
				
				else
				{
					stacks.remove(e.getEntity().getEntityId());
					e.getEntity().remove();
				}
				
				update(e.getEntity());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(EntitySpawnEvent e)
	{
		if(!enabled)
		{
			return;
		}
		
		if(e.getEntity() instanceof LivingEntity)
		{
			stack((LivingEntity) e.getEntity());
			update((LivingEntity) e.getEntity());
		}
	}
	
	public void stack(LivingEntity e)
	{
		if(!enabled)
		{
			return;
		}
		
		if(isStacked(e))
		{
			Area a = new Area(e.getLocation(), 5.0);
			
			for(Entity i : a.getNearbyEntities())
			{
				if(i instanceof LivingEntity)
				{
					LivingEntity ex = (LivingEntity) i;
					
					if(ex.getType().equals(e.getType()))
					{
						if(isStacked(ex))
						{
							if(stacks.get(ex.getEntityId()) + stacks.get(e.getEntityId()) <= 16)
							{
								stacks.put(e.getEntityId(), stacks.get(ex.getEntityId()) + stacks.get(e.getEntityId()));
								ex.remove();
								stacks.remove(ex.getEntityId());
								update(e);
								break;
							}
						}
						
						else
						{
							if(stacks.get(e.getEntityId()) + 1 <= 16)
							{
								stacks.put(e.getEntityId(), stacks.get(e.getEntityId()) + 1);
								ex.remove();
								update(e);
								break;
							}
						}
					}
				}
			}
		}
		
		else
		{
			Area a = new Area(e.getLocation(), 5.0);
			
			for(Entity i : a.getNearbyEntities())
			{
				if(i instanceof LivingEntity)
				{
					LivingEntity ex = (LivingEntity) i;
					
					if(ex.getType().equals(e.getType()))
					{
						if(isStacked(ex))
						{
							if(stacks.get(ex.getEntityId()) + 1 <= 16)
							{
								stacks.put(ex.getEntityId(), stacks.get(ex.getEntityId()) + 1);
								e.remove();
								update(ex);
								break;
							}
						}
						
						else
						{
							stacks.put(ex.getEntityId(), 2);
							e.remove();
							update(ex);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void onNewConfig()
	{
		cc.set("stacker.enabled", false);
		cc.set("stacker.ignore.named-entities", true);
		cc.set("stacker.ignore.non-cullable-mobs", true);
		cc.set("stacker.settings.stack-range", 4.3);
		cc.set("stacker.settings.max-size", 16);
		cc.set("stacker.lang.change-names", true);
		cc.set("stacler.lang.name-format", "&a<number> X &b<mob>");
	}

	@Override
	public void onReadConfig()
	{
		enabled = cc.getBoolean("stacker.enabled");
	}

	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}

	@Override
	public String getCodeName()
	{
		return "entity-stacker";
	}
}
