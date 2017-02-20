package org.cyberpwn.react.action;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.nms.NMSX;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Q;
import org.cyberpwn.react.util.Q.P;
import org.cyberpwn.react.util.StackedEntity;
import org.cyberpwn.react.util.TaskLater;

public class ActionStackEntities extends Action implements Listener
{
	private GList<StackedEntity> stacks;
	
	public ActionStackEntities(ActionController actionController)
	{
		super(actionController, Material.SHEARS, "stack-entities", "ActionStackEntities", 100, "Stack Entities", L.ACTION_STACKENTITIES, false);
		
		stacks = new GList<StackedEntity>();
	}
	
	@Override
	public void act()
	{
		for(World i : new GList<World>(Bukkit.getWorlds()).shuffle())
		{
			for(Chunk j : new GList<Chunk>(i.getLoadedChunks()).shuffle())
			{
				stack(j);
			}
		}
	}
	
	public void stack(Chunk c)
	{
		int t = 0;
		
		for(Entity i : c.getEntities())
		{
			if(i instanceof LivingEntity && canTouch((LivingEntity) i))
			{
				Area a = new Area(i.getLocation(), cc.getDouble("constraints.max-distance"));
				
				for(Entity j : a.getNearbyEntities())
				{
					if(j instanceof LivingEntity && canTouch((LivingEntity) j))
					{
						if(!i.getType().equals(j.getType()))
						{
							continue;
						}
						
						t += 4;
						
						new TaskLater(t)
						{
							@Override
							public void run()
							{
								if(i.isDead() || j.isDead())
								{
									return;
								}
								
								stack((LivingEntity) i, (LivingEntity) j);
							}
						};
					}
				}
			}
		}
	}
	
	public void stack(LivingEntity a, LivingEntity b)
	{
		new Q(P.NORMAL, "Stack Chunk", true)
		{
			@Override
			public void run()
			{
				if(canStack(a, b))
				{
					if(isStacked(a))
					{
						getStack(a).insert(b);
					}
					
					else if(isStacked(b))
					{
						stack(b, a);
					}
					
					else
					{
						createStack(a);
						stack(a, b);
					}
				}
			}
		};
	}
	
	public void createStack(LivingEntity a)
	{
		addStack(new StackedEntity(a, 1));
	}
	
	public boolean canStack(LivingEntity a, LivingEntity b)
	{
		if(!canTouch(a, b))
		{
			return false;
		}
		
		if(!within(a, b, cc.getDouble("constraints.max-distance")))
		{
			return false;
		}
		
		if(getSize(a, b) > cc.getInt("constraints.max-stack-size"))
		{
			return false;
		}
		
		return true;
	}
	
	public boolean within(LivingEntity a, LivingEntity b, double dist)
	{
		return a.getLocation().distanceSquared(b.getLocation()) <= dist * dist;
	}
	
	public boolean canTouch(LivingEntity... es)
	{
		for(LivingEntity i : es)
		{
			if(!canTouch(i))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canTouch(LivingEntity e)
	{
		if(isTamed(e) && !cc.getBoolean("constraints.stack-yamed-entities"))
		{
			return false;
		}
		
		if(isNamed(e) && !cc.getBoolean("constraints.stack-named-entities"))
		{
			actionController.s("Not named");
			return false;
		}
		
		if(!cc.getStringList("constraints.stackable").contains(e.getType().toString()))
		{
			return false;
		}
		
		return true;
	}
	
	public void updateHealth(LivingEntity e, LivingEntity v)
	{
		if(!cc.getBoolean("modifications.stack-health"))
		{
			return;
		}
		
		double m = e.getMaxHealth();
		double h = e.getHealth();
		
		m += v.getMaxHealth() * cc.getDouble("modifications.health-stack-multiplier");
		h += v.getHealth() * cc.getDouble("modifications.health-stack-multiplier");
		
		if(m > 2060)
		{
			m = 2060;
		}
		
		if(h > 2060)
		{
			h = 2060;
		}
		
		if(h > m)
		{
			h = m;
		}
		
		try
		{
			e.setMaxHealth(m);
			e.setHealth(h);
		}
		
		catch(Exception ex)
		{
			
		}
	}
	
	public int getSize(LivingEntity... es)
	{
		int size = 0;
		
		for(LivingEntity i : es)
		{
			if(isStacked(i))
			{
				size += getStack(i).getSize();
			}
			
			else
			{
				size++;
			}
		}
		
		return size;
	}
	
	public boolean isTamed(LivingEntity e)
	{
		if(e instanceof Tameable)
		{
			return ((Tameable) e).isTamed();
		}
		
		return false;
	}
	
	public boolean isNamed(LivingEntity e)
	{
		if(e.getCustomName() == null)
		{
			return false;
		}
		
		return true;
	}
	
	public void multiplyDrops(List<ItemStack> drops, int f)
	{
		for(ItemStack i : drops)
		{
			i.setAmount((int) ((Math.random() * f * i.getAmount()) + 1));
		}
	}
	
	public void animateStack(LivingEntity from, LivingEntity to)
	{
		Area a = new Area(to.getLocation(), 24.0);
		
		for(Player i : a.getNearbyPlayers())
		{
			NMSX.showPickup(i, to, from);
		}
	}
	
	public void addStack(StackedEntity e)
	{
		if(!stacks.contains(e) && !stacks.contains(e.getHost()))
		{
			stacks.add(e);
		}
	}
	
	public void removeStack(LivingEntity e)
	{
		removeStack(getStack(e));
	}
	
	public void removeStack(StackedEntity e)
	{
		stacks.remove(e);
	}
	
	public boolean isStacked(LivingEntity e)
	{
		return getStack(e) != null;
	}
	
	public StackedEntity getStack(LivingEntity e)
	{
		for(StackedEntity i : getStacks())
		{
			if(i.equals(e))
			{
				return i;
			}
		}
		
		return null;
	}
	
	@Override
	public void manual(CommandSender p)
	{
		ManualActionEvent mae = new ManualActionEvent(p, this);
		React.instance().getServer().getPluginManager().callEvent(mae);
		
		if(mae.isCancelled())
		{
			return;
		}
		
		super.manual(p);
		long ms = System.currentTimeMillis();
		act();
		p.sendMessage(Info.TAG + ChatColor.GREEN + L.MESSAGE_MANUAL_FINISH + getName() + L.MESSAGE_MANUAL_FINISHED + "in " + (System.currentTimeMillis() - ms) + "ms");
	}
	
	@Override
	public void onReadConfig()
	{
		super.onReadConfig();
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		
		cc.set("component.enable", false, "ABOUT " + getName() + "\n" + getDescription() + "\n\nYou can disable " + getName() + " here.");
		
		GList<String> allow = new GList<String>();
		
		for(EntityType i : EntityType.values())
		{
			if(i.equals(EntityType.PLAYER) || i.equals(EntityType.VILLAGER) || i.equals(EntityType.HORSE) || i.equals(EntityType.OCELOT) || i.equals(EntityType.WOLF) || i.equals(EntityType.ARROW) || i.equals(EntityType.BOAT) || i.equals(EntityType.COMPLEX_PART) || i.equals(EntityType.WITHER_SKULL) || i.equals(EntityType.DROPPED_ITEM) || i.equals(EntityType.UNKNOWN) || i.equals(EntityType.THROWN_EXP_BOTTLE) || i.equals(EntityType.EGG) || i.equals(EntityType.ENDER_CRYSTAL) || i.equals(EntityType.ENDER_PEARL) || i.equals(EntityType.ENDER_SIGNAL) || i.equals(EntityType.ITEM_FRAME) || i.equals(EntityType.PAINTING) || i.equals(EntityType.ARMOR_STAND))
			{
				continue;
			}
			
			if(LivingEntity.class.isAssignableFrom(i.getEntityClass()))
			{
				allow.add(i.toString());
			}
		}
		
		cc.set("modifications.stack-health", true);
		cc.set("modifications.health-stack-multiplier", 0.3);
		cc.set("constraints.max-distance", 4.4);
		cc.set("constraints.max-stack-size", 8);
		cc.set("constraints.stack-named-entities", false);
		cc.set("constraints.stack-tamed-entities", false);
		cc.set("constraints.stackable", allow, "Stackable entities. Anything in this list can be stacked");
	}
	
	public GList<StackedEntity> getStacks()
	{
		return stacks;
	}
}
