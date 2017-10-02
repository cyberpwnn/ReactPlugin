package org.cyberpwn.react.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.nms.NMSX;
import org.cyberpwn.react.util.Area;
import org.cyberpwn.react.util.E;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GSound;
import org.cyberpwn.react.util.HandledEvent;
import org.cyberpwn.react.util.StackedEntity;
import org.cyberpwn.react.util.TaskLater;

public class ActionStackEntities extends Action implements Listener
{
	public static String RC_NONCE = "%%__NONCE__%%";
	public static String RC_UIVD = "%%__UID__%%";
	private GList<StackedEntity> stacks;
	private GList<LivingEntity> unstack;

	public ActionStackEntities(ActionController actionController)
	{
		super(actionController, Material.SHEARS, "stack-entities", "ActionStackEntities", 100, "Stack Entities", L.ACTION_STACKENTITIES, false);

		unstack = new GList<LivingEntity>();
		stacks = new GList<StackedEntity>();
		React.instance().register(this);
		aliases.add("stack");
		maxSleepFactor = 3.2;
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

		for(LivingEntity i : unstack.copy())
		{
			if(i.isDead())
			{
				unstack.remove(i);
			}
		}
	}

	public boolean canUnstack(LivingEntity e)
	{
		if(!isStacked(e))
		{
			return false;
		}

		if(getStack(e).getSize() < 2)
		{
			return false;
		}

		return true;
	}

	public void unstack(LivingEntity e)
	{
		StackedEntity s = getStack(e);
		EntityType et = e.getType();
		int va = 0;
		int vb = 0;
		int to = s.getSize();

		if(to % 2 == 0)
		{
			va = to / 2;
			vb = va;
		}

		else
		{
			to--;
			va = to / 2;
			vb = va + 1;
		}

		Vector da = new Vector((Math.random() - 0.5), 0, (Math.random() - 0.5));
		Vector db = new Vector(-da.getX(), 0, -da.getZ());
		Entity ea = e.getLocation().getWorld().spawnEntity(e.getLocation(), et);
		Entity eb = e.getLocation().getWorld().spawnEntity(e.getLocation(), et);
		Entity ec = e.getLocation().getWorld().spawnEntity(e.getLocation(), et);
		Entity ed = e.getLocation().getWorld().spawnEntity(e.getLocation(), et);
		ea.setVelocity(da);
		eb.setVelocity(db);
		removeStack(e);

		if(cc.getBoolean("modifications.stack-sounds"))
		{
			try
			{
				new GSound(Sound.valueOf("CHICKEN_EGG_POP"), 1f, 0.7f).play(e.getLocation());
			}

			catch(Exception exx)
			{
				try
				{
					new GSound(Sound.valueOf("ENTITY_CHICKEN_EGG"), 1f, 0.7f).play(e.getLocation());
				}

				catch(Exception ex)
				{

				}
			}
		}

		E.r(e);
		addStack(new StackedEntity((LivingEntity) ea, va));
		addStack(new StackedEntity((LivingEntity) eb, vb));
		animateStack((LivingEntity) ec, (LivingEntity) ea);
		animateStack((LivingEntity) ed, (LivingEntity) eb);
		E.r(ec);
		E.r(ed);
		unstack.add((LivingEntity) ea);
		unstack.add((LivingEntity) eb);
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent e)
	{
		new HandledEvent()
		{

			@Override
			public void execute()
			{
				if(!isEnabled())
				{
					return;
				}

				if(!cc.getBoolean("modifications.allow-player-splitting"))
				{
					return;
				}

				if(e.getRightClicked() != null && e.getRightClicked() instanceof LivingEntity && e.getPlayer().isSneaking() && isStacked((LivingEntity) e.getRightClicked()))
				{
					LivingEntity ee = (LivingEntity) e.getRightClicked();

					if(canUnstack(ee))
					{
						unstack(ee);
					}
				}
			}
		};
	}

	public void stack(Chunk c)
	{
		if(!cc.getBoolean("component.enable"))
		{
			return;
		}

		int t = 0;

		for(Entity i : c.getEntities())
		{
			if(i instanceof LivingEntity && canTouch((LivingEntity) i))
			{
				if(isStacked((LivingEntity) i))
				{
					if(getStack((LivingEntity) i).getSize() < 2)
					{
						removeStack((LivingEntity) i);
					}
				}

				Area a = new Area(i.getLocation(), cc.getDouble("constraints.max-distance"));

				for(Entity j : new GList<Entity>(a.getNearbyEntities()).shuffle())
				{
					if(j instanceof LivingEntity && canTouch((LivingEntity) j))
					{
						updateName((LivingEntity) j);

						if(!i.getType().equals(j.getType()))
						{
							continue;
						}

						t += 1;

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

	public void createStack(LivingEntity a)
	{
		addStack(new StackedEntity(a, 1));
		updateName(a);
	}

	public boolean canStack(LivingEntity a, LivingEntity b)
	{
		if(unstack.contains(a) || unstack.contains(b))
		{
			return false;
		}

		if(!can(a.getLocation()) || !can(b.getLocation()))
		{
			return false;
		}

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
		if(isTamed(e) && !cc.getBoolean("constraints.stack-tamed-entities"))
		{
			return false;
		}

		if(isNamed(e) && !cc.getBoolean("constraints.stack-named-entities"))
		{
			return false;
		}

		if(!cc.getStringList("constraints.stackable").contains(e.getType().toString()))
		{
			return false;
		}

		return true;
	}

	public void updateName(LivingEntity e)
	{
		if(getSize(e) < 2 && isTagged(e))
		{
			e.setCustomNameVisible(false);
			e.setCustomName(null);
		}

		else if(getSize(e) > 1)
		{
			e.setCustomNameVisible(true);
			e.setCustomName(makeName(e));
		}
	}

	public boolean isTagged(LivingEntity e)
	{
		if(e.getCustomName() == null)
		{
			return false;
		}

		if(e.getCustomName().startsWith(suff()) && e.getCustomName().endsWith(suff()))
		{
			return true;
		}

		return false;
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

		if(e.getCustomName().startsWith(suff()) && e.getCustomName().endsWith(suff()))
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

	@SuppressWarnings("unlikely-arg-type")
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
		String msg = ChatColor.WHITE + getName() + ChatColor.GRAY + " in " + ChatColor.WHITE + (System.currentTimeMillis() - ms) + "ms";
		p.sendMessage(Info.TAG + msg);
		notifyOf(msg, p);
	}

	@Override
	public void onReadConfig()
	{
		super.onReadConfig();
	}

	public String nameFormat()
	{
		return cc.getString("modifications.name-format");
	}

	public String makeName(LivingEntity e)
	{
		return makeName(e.getType(), getSize(e));
	}

	@SuppressWarnings("deprecation")
	public String makeName(EntityType t, int amt)
	{
		return suff() + ChatColor.WHITE + F.color(nameFormat().replaceAll("%amt%", F.f(amt)).replaceAll("%mob%", StringUtils.capitaliseAllWords(t.getName().toLowerCase()))) + suff();
	}

	public String suff()
	{
		return ChatColor.BLACK + "" + ChatColor.WHITE + ChatColor.GREEN;
	}

	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);

		cc.set("component.enable", false, "ABOUT " + getName() + "\n" + getDescription() + "\n\nYou can disable " + getName() + " here.");

		GList<String> allow = new GList<String>();

		for(EntityType i : EntityType.values())
		{
			if(i.toString().equals("ARMOR_STAND"))
			{
				continue;
			}

			if(i.equals(EntityType.PLAYER) || i.equals(EntityType.VILLAGER) || i.equals(EntityType.HORSE) || i.equals(EntityType.OCELOT) || i.equals(EntityType.WOLF) || i.equals(EntityType.ARROW) || i.equals(EntityType.BOAT) || i.equals(EntityType.COMPLEX_PART) || i.equals(EntityType.WITHER_SKULL) || i.equals(EntityType.DROPPED_ITEM) || i.equals(EntityType.UNKNOWN) || i.equals(EntityType.THROWN_EXP_BOTTLE) || i.equals(EntityType.EGG) || i.equals(EntityType.ENDER_CRYSTAL) || i.equals(EntityType.ENDER_PEARL) || i.equals(EntityType.ENDER_SIGNAL) || i.equals(EntityType.ITEM_FRAME) || i.equals(EntityType.PAINTING))
			{
				continue;
			}

			if(LivingEntity.class.isAssignableFrom(i.getEntityClass()))
			{
				allow.add(i.toString());
			}
		}

		cc.set("modifications.name-format", "&l&a%amt%x &r&7%mob%");
		cc.set("modifications.stack-health", true);
		cc.set("modifications.stack-sounds", true);
		cc.set("modifications.health-stack-multiplier", 0.3);
		cc.set("modifications.allow-player-splitting", true, "Allow the player to shift right click a mob to split a stack.");
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

	public double maxSize()
	{
		return cc.getInt("constraints.max-stack-size");
	}

	public void unstackClear()
	{
		unstack.clear();
	}
}
