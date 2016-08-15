package org.cyberpwn.react.controller;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.TaskLater;

public class EntityStackController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private GMap<Integer, Integer> stacks;
	
	public EntityStackController(React react)
	{
		super(react);
		
		stacks = new GMap<Integer, Integer>();
		cc = new ClusterConfig();
	}
	
	public boolean isStacked(LivingEntity e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return false;
		}
		
		return stacks.containsKey(e.getEntityId());
	}
	
	public boolean canTouch(LivingEntity e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return false;
		}
		
		if(!getReact().getWorldController().canStack(e.getWorld()))
		{
			return false;
		}
		
		if(cc.getBoolean("stacker.ignore.named-entities"))
		{
			if(e.getCustomName() != null)
			{
				return false;
			}
		}
		
		if(cc.getBoolean("stacker.ignore.non-cullable-mobs"))
		{
			if(!getReact().getActionController().getActionCullEntities().getConfiguration().getStringList(getReact().getActionController().getActionCullEntities().getCodeName() + ".cullable").contains(e.getType().toString()))
			{
				return false;
			}
		}
		
		LivingEntity i = e;
		
		if(i.getType().toString().equals("PLAYER"))
		{
			return false;
		}
		
		if(i.getType().toString().equals("COMPLEX_PART"))
		{
			return false;
		}
		
		if(i.getType().toString().equals("PAINTING"))
		{
			return false;
		}
		
		if(i.getType().toString().equals("PAINTING"))
		{
			return false;
		}
		
		if(i.getType().toString().equals("ITEM_FRAME"))
		{
			return false;
		}
		
		if(i.getType().toString().equals("WITHER_SKULL"))
		{
			return false;
		}
		
		if(i.getType().toString().equals("ARMOR_STAND"))
		{
			return false;
		}
		
		if(!React.instance().getWorldController().canTouch(e))
		{
			return false;
		}
		
		return true;
	}
	
	public void stop()
	{
		if(!cc.getBoolean("stacker.enabled"))
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
	
	public boolean hasStackedName(LivingEntity e)
	{
		if(e.getCustomName() == null)
		{
			return false;
		}
		
		for(int i = 0; i < cc.getInt("stacker.settings.max-size"); i++)
		{
			@SuppressWarnings("deprecation")
			String f = F.color(cc.getString("stacker.lang.name-format").replaceAll("<number>", i + "").replaceAll("<mob>", StringUtils.capitalise(e.getType().toString().toLowerCase().replaceAll("_", " "))));
			
			if(e.getCustomName().equals(f))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void check(LivingEntity e)
	{
		if(isStacked(e))
		{
			return;
		}
		
		if(hasStackedName(e))
		{
			new TaskLater(5)
			{
				public void run()
				{
					e.setCustomName(null);
				}
			};
		}
	}
	
	public void check(Chunk c)
	{
		for(Entity i : c.getEntities())
		{
			if(i instanceof LivingEntity)
			{
				check((LivingEntity) i);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void update(LivingEntity e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return;
		}
		
		if(isStacked(e) && cc.getBoolean("stacker.lang.change-names"))
		{
			e.setCustomName(F.color(cc.getString("stacker.lang.name-format").replaceAll("<number>", stacks.get(e.getEntityId()) + "").replaceAll("<mob>", StringUtils.capitalise(e.getType().toString().toLowerCase().replaceAll("_", " ")))));
		}
		
		else
		{
			if(cc.getBoolean("stacker.lang.change-names"))
			{
				e.setCustomName(null);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(EntityDamageEvent e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return;
		}
		
		if(e.getEntity() instanceof LivingEntity)
		{
			if(isStacked((LivingEntity) e.getEntity()) && canTouch((LivingEntity) e.getEntity()))
			{
				update((LivingEntity) e.getEntity());
			}
		}
	}
	
	@EventHandler
	public void on(ChunkLoadEvent e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return;
		}
		
		check(e.getChunk());
		
		for(Entity i : e.getChunk().getEntities())
		{
			new TaskLater(5)
			{
				public void run()
				{
					if(i instanceof LivingEntity)
					{
						LivingEntity x = (LivingEntity) i;
						
						if(canTouch(x))
						{
							stack(x);
						}
					}
				}
			};
		}
	}
	
	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		if(!cc.getBoolean("stacker.enabled"))
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
					stacks.remove(x.getEntityId());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(EntityDeathEvent e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return;
		}
		
		if(e.getEntity() instanceof LivingEntity)
		{
			if(isStacked(e.getEntity()))
			{
				new TaskLater(1)
				{
					public void run()
					{
						stacks.put(e.getEntity().getEntityId(), stacks.get(e.getEntity().getEntityId()) - 1);
						
						if(stacks.get(e.getEntity().getEntityId()) > 1)
						{
							EntityType t = e.getEntity().getType();
							Location l = e.getEntity().getLocation();
							Integer s = stacks.get(e.getEntity().getEntityId());
							
							stacks.remove(e.getEntity().getEntityId());
							e.getEntity().remove();
							
							new TaskLater(1)
							{
								public void run()
								{
									LivingEntity et = (LivingEntity) l.getWorld().spawnEntity(l.clone().add(0, 0.5, 0), t);
									
									if(et != null)
									{
										stacks.put(e.getEntity().getEntityId(), s);
										update(et);
									}
								}
							};
							
							((ExperienceOrb) e.getEntity().getLocation().getWorld().spawn(e.getEntity().getLocation(), ExperienceOrb.class)).setExperience(e.getDroppedExp());
						}
						
						else if(stacks.get(e.getEntity().getEntityId()) > 0)
						{
							EntityType t = e.getEntity().getType();
							Location l = e.getEntity().getLocation();
							e.getEntity().remove();
							
							stacks.remove(e.getEntity().getEntityId());
							e.getEntity().remove();
							
							new TaskLater(1)
							{
								public void run()
								{
									LivingEntity et = (LivingEntity) l.getWorld().spawnEntity(l, t);
									
									if(et != null)
									{
										update(et);
									}
								}
							};
							
							((ExperienceOrb) e.getEntity().getLocation().getWorld().spawn(e.getEntity().getLocation(), ExperienceOrb.class)).setExperience(e.getDroppedExp());
						}
						
						else
						{
							stacks.remove(e.getEntity().getEntityId());
							e.getEntity().remove();
							((ExperienceOrb) e.getEntity().getLocation().getWorld().spawn(e.getEntity().getLocation(), ExperienceOrb.class)).setExperience(e.getDroppedExp());
						}
						
					}
				};
			}
		}
	}
	
	public void entitySpawned(boolean spawner, Entity e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return;
		}
		
		if(cc.getBoolean("stacker.ignore.non-spawner-spawns") && !spawner)
		{
			return;
		}
		
		if(e instanceof LivingEntity)
		{
			stack((LivingEntity) e);
			update((LivingEntity) e);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(EntitySpawnEvent e)
	{
		entitySpawned(false, e.getEntity());
	}
	
	@EventHandler
	public void on(SpawnerSpawnEvent e)
	{
		entitySpawned(true, e.getEntity());
	}
	
	public void stack(LivingEntity e)
	{
		if(!cc.getBoolean("stacker.enabled"))
		{
			return;
		}
		
		if(!canTouch(e))
		{
			return;
		}
		
		if(isStacked(e))
		{
			Area a = new Area(e.getLocation(), cc.getDouble("stacker.settings.stack-range"));
			
			for(Entity i : a.getNearbyEntities())
			{
				if(i instanceof LivingEntity)
				{
					LivingEntity ex = (LivingEntity) i;
					
					if(ex.getType().equals(e.getType()))
					{
						if(isStacked(ex))
						{
							if(stacks.get(ex.getEntityId()) + stacks.get(e.getEntityId()) <= cc.getInt("stacker.settings.max-size"))
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
							if(stacks.get(e.getEntityId()) + 1 <= cc.getInt("stacker.settings.max-size"))
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
			Area a = new Area(e.getLocation(), cc.getDouble("stacker.settings.stack-range"));
			
			for(Entity i : a.getNearbyEntities())
			{
				if(i instanceof LivingEntity)
				{
					LivingEntity ex = (LivingEntity) i;
					
					if(ex.getType().equals(e.getType()))
					{
						if(isStacked(ex))
						{
							if(stacks.get(ex.getEntityId()) + 1 <= cc.getInt("stacker.settings.max-size"))
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
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("stacker.enabled", false, "Enable mob stacking?");
		cc.set("stacker.ignore.named-entities", true, "Ignore stacking named entities?");
		cc.set("stacker.ignore.non-cullable-mobs", true, "Dont stack mobs that cant be culled in the action-cull-entities.yml file.");
		cc.set("stacker.ignore.non-spawner-spawns", true, "Don't stack mobs that did not come from mob spawners.");
		cc.set("stacker.settings.stack-range", 4.3, "The range for entities of the same type to stack?");
		cc.set("stacker.settings.max-size", 16, "The max amount of entities stacked into one mob?");
		cc.set("stacker.lang.change-names", true, "Modify the name of the mob to display how many stacked entities are in it.");
		cc.set("stacker.lang.name-format", "&a<number>x &b<mob>", "The format for stacking entities where\n<number> = the number of stacked entities\n<name> = the type of mob\n(color codes enabled)");
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
		return "entity-stacker";
	}
}
