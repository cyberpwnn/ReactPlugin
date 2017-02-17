package org.cyberpwn.react.action;

import java.util.Collections;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.Listener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.api.ManualActionEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.controller.ActionController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GBiset;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.MaterialList;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.TaskLater;
import org.cyberpwn.react.util.Verbose;

public class ActionCullDrops extends Action implements Listener
{
	private GMap<String, GBiset<Double, GList<Material>>> worth;
	private Double undefinedWorth;
	private GMap<Double, String> worths;
	private GList<Double> worthsx;
	
	public ActionCullDrops(ActionController actionController)
	{
		super(actionController, Material.SHEARS, "cull-drops", "ActionCullDrops", 1200, "Cull Drops", L.ACTION_CULLDROPS, true);
		worth = new GMap<String, GBiset<Double, GList<Material>>>();
		undefinedWorth = 100.0;
		worths = new GMap<Double, String>();
		worthsx = new GList<Double>();
	}
	
	@Override
	public void act()
	{
		final int[] cpt = new int[] {0, 0};
		
		for(World i : getActionController().getReact().getServer().getWorlds())
		{
			cpt[0] += i.getLoadedChunks().length;
		}
		
		cpt[0] /= (idealTick + 1);
		
		for(World i : getActionController().getReact().getServer().getWorlds())
		{
			final Iterator<Chunk> it = new GList<Chunk>(i.getLoadedChunks()).iterator();
			
			new Task(0)
			{
				@Override
				public void run()
				{
					int[] itx = new int[] {0};
					while(it.hasNext() && itx[0] <= cpt[0])
					{
						cull(it.next());
						itx[0]++;
						cpt[1]++;
					}
					
					if(!it.hasNext())
					{
						cancel();
						
						if(cpt[0] > 0)
						{
							Verbose.x("cull", "Culled " + cpt[0] + " Drops");
						}
					}
				}
			};
		}
	}
	
	public void updateDrop(Item item)
	{
		int alive = item.getTicksLived();
		int max = Bukkit.spigot().getConfig().getInt("world-settings.default.item-despawn-rate");
		int ticksLeft = max - alive;
		int secondsLeft = ticksLeft / 20;
		
		if(ticksLeft < 1)
		{
			item.remove();
			return;
		}
		
		if(cc.getBoolean("visual.warn.enable-drop-warnings"))
		{
			if(ticksLeft < 20 * cc.getInt("visual.warn.time-threshold-seconds"))
			{
				String form = F.color(cc.getString("visual.warn.time-format").replaceAll("%time%", String.valueOf(secondsLeft)));
				item.setCustomName(form);
				item.setCustomNameVisible(true);
				
				if(secondsLeft > 0)
				{
					new TaskLater(20)
					{
						@Override
						public void run()
						{
							updateDrop(item);
						}
					};
				}
			}
		}
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
	
	public int cull(Chunk chunk)
	{
		int max = Bukkit.spigot().getConfig().getInt("world-settings.default.item-despawn-rate");
		GList<Item> drops = new GList<Item>();
		
		for(Entity i : chunk.getEntities())
		{
			if(i.getType().equals(EntityType.DROPPED_ITEM) && i.getTicksLived() < max - (cc.getInt("culler.despawn-delay") * 15))
			{
				drops.add((Item) i);
			}
		}
		
		drops = sort(drops);
		int ix = 0;
		
		while(!drops.isEmpty() && drops.size() > Math.abs(cc.getInt("drops-per-chunk")))
		{
			mark(drops.get(0));
			drops.remove(0);
			ix++;
		}
		
		return ix;
	}
	
	public void mark(Item item)
	{
		int max = Bukkit.spigot().getConfig().getInt("world-settings.default.item-despawn-rate");
		item.setTicksLived(max - (cc.getInt("culler.despawn-delay") * 15));
	}
	
	public GList<Item> sort(GList<Item> d)
	{
		GList<Item> drops = new GList<Item>();
		
		for(Double i : worthsx)
		{
			for(Item j : d)
			{
				if(i == getWorth(j.getItemStack().getType()))
				{
					drops.add(j);
				}
			}
		}
		
		return drops;
	}
	
	public Double getWorth(Material m)
	{
		for(String i : worth.k())
		{
			if(worth.get(i).getB().contains(m))
			{
				return worth.get(i).getA();
			}
		}
		
		return undefinedWorth;
	}
	
	@Override
	public void onReadConfig()
	{
		super.onReadConfig();
		
		worth.put("rubble", new GBiset<Double, GList<Material>>(cc.getDouble("worth.rubble"), new MaterialList(cc.getStringList("define.rubble")).getMaterials()));
		worth.put("herbs", new GBiset<Double, GList<Material>>(cc.getDouble("worth.herbs"), new MaterialList(cc.getStringList("define.herbs")).getMaterials()));
		worth.put("mob-drops", new GBiset<Double, GList<Material>>(cc.getDouble("worth.mob-drops"), new MaterialList(cc.getStringList("define.mob-drops")).getMaterials()));
		worth.put("food", new GBiset<Double, GList<Material>>(cc.getDouble("worth.food"), new MaterialList(cc.getStringList("define.food")).getMaterials()));
		worth.put("lumber", new GBiset<Double, GList<Material>>(cc.getDouble("worth.lumber"), new MaterialList(cc.getStringList("define.lumber")).getMaterials()));
		worth.put("cheap-materials", new GBiset<Double, GList<Material>>(cc.getDouble("worth.cheap-materials"), new MaterialList(cc.getStringList("define.cheap-materials")).getMaterials()));
		worth.put("cheap-tools-armor", new GBiset<Double, GList<Material>>(cc.getDouble("worth.cheap-tools-armor"), new MaterialList(cc.getStringList("define.cheap-tools-armor")).getMaterials()));
		worth.put("rare-materials", new GBiset<Double, GList<Material>>(cc.getDouble("worth.rare-materials"), new MaterialList(cc.getStringList("define.rare-materials")).getMaterials()));
		worth.put("ore", new GBiset<Double, GList<Material>>(cc.getDouble("worth.ore"), new MaterialList(cc.getStringList("define.ore")).getMaterials()));
		worth.put("expensive-tools-armor", new GBiset<Double, GList<Material>>(cc.getDouble("worth.expensive-tools-armor"), new MaterialList(cc.getStringList("define.expensive-tools-armor")).getMaterials()));
		worth.put("other", new GBiset<Double, GList<Material>>(cc.getDouble("worth.other"), new MaterialList(cc.getStringList("define.other")).getMaterials()));
		undefinedWorth = cc.getDouble("worth.undefined");
		
		for(String i : worth.k())
		{
			worths.put(worth.get(i).getA(), i);
			worthsx.add(worth.get(i).getA());
		}
		
		Collections.sort(worthsx);
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		
		cc.set("visual.warn.enable-drop-warnings", true, "Show despawn warnings on items");
		cc.set("visual.warn.time-threshold-seconds", 15, "Time in seconds to start counting down.");
		cc.set("visual.warn.time-format", "&c\u26a0 &6&l%time%", "The countdown format");
		cc.set("drops-per-chunk", 26, "Max drops per chunk before react starts clipping drops.");
		cc.set("ignore-all-worth-when-culling", false, "Blatantly ignore worth and just remove whater react wants first.");
		cc.set("worth.rubble", 0.1, "The worth of rubble. Keep this lower than more expensive stuff\nunless you want to remove expensive stuff first :P");
		cc.set("worth.herbs", 0.2, "The worth of plants, seeds and stuff.");
		cc.set("worth.mob-drops", 0.3, "The worth of common drops from mobs.");
		cc.set("worth.food", 0.4, "The worth of food, cooked and raw.");
		cc.set("worth.lumber", 0.5, "The worth of wood logs sticks and more.");
		cc.set("worth.cheap-materials", 0.6, "The worth of Cheap stuff that probobly wasnt that hard to get.");
		cc.set("worth.cheap-tools-armor", 0.7, "The worth of Cheap tools and armor (wood/leather & stone)");
		cc.set("worth.rare-materials", 0.8, "The worth of Rare stuff that you really wouldnt want to loose.");
		cc.set("worth.ore", 0.9, "The worth of Ores and mined things that could be hard to find.");
		cc.set("worth.expensive-tools-armor", 1.0, "The worth of expensive tools and armor");
		cc.set("worth.other", 1.1, "The worth of Other things that cant be categorized.");
		cc.set("worth.undefined", 1.2, "The worth of Undefined items in this config");
		cc.set("culler.despawn-delay", 12, "The delay in seconds to despawn an item.");
		cc.set("define.rubble", new MaterialList(Material.DIRT, Material.GRASS, Material.COBBLESTONE, Material.COBBLE_WALL, Material.COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE, Material.BEDROCK, Material.SANDSTONE, Material.SAND, Material.CLAY, Material.STAINED_CLAY, Material.HARD_CLAY, Material.STONE).getStrings());
		cc.set("define.herbs", new MaterialList(Material.SEEDS, Material.SUGAR, Material.SUGAR_CANE, Material.SUGAR_CANE_BLOCK, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.CROPS, Material.SAPLING, Material.VINE, Material.BROWN_MUSHROOM, Material.YELLOW_FLOWER, Material.INK_SACK, Material.RED_MUSHROOM, Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.CACTUS, Material.DEAD_BUSH, Material.LONG_GRASS, Material.LEAVES, Material.LEAVES_2).getStrings());
		cc.set("define.mob-drops", new MaterialList(Material.SULPHUR, Material.EGG, Material.LEATHER, Material.RAW_BEEF, Material.RAW_CHICKEN, Material.RAW_FISH, Material.FEATHER, Material.PORK, Material.ROTTEN_FLESH).getStrings());
		cc.set("define.food", new MaterialList(Material.COOKED_BEEF, Material.GRILLED_PORK, Material.COOKED_CHICKEN, Material.COOKED_FISH).getStrings());
		cc.set("define.lumber", new MaterialList(Material.WOOD, Material.LOG, Material.LOG_2, Material.FENCE, Material.FENCE_GATE, Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.SPRUCE_WOOD_STAIRS).getStrings());
		cc.set("define.cheap-materials", new MaterialList(Material.TNT, Material.IRON_FENCE, Material.NETHER_FENCE, Material.NETHERRACK, Material.OBSIDIAN, Material.STICK, Material.CARROT_STICK, Material.WOOL, Material.CARPET, Material.BED, Material.BED_BLOCK, Material.WRITTEN_BOOK, Material.BOOK, Material.BOOK_AND_QUILL, Material.BOOKSHELF, Material.WRITTEN_BOOK, Material.CLAY_BALL, Material.SNOW_BALL, Material.SNOW, Material.SNOW_BLOCK).getStrings());
		cc.set("define.cheap-tools-armor", new MaterialList(Material.WOOD_AXE, Material.WOOD_HOE, Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.WOOD_SWORD, Material.WOOD_PLATE, Material.WOOD_BUTTON, Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS).getStrings());
		cc.set("define.rare-materials", new MaterialList(Material.DIAMOND, Material.EMERALD, Material.IRON_INGOT, Material.GOLD_INGOT, Material.REDSTONE, Material.GLOWSTONE, Material.GLOWSTONE_DUST, Material.COAL_BLOCK, Material.COAL, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.QUARTZ, Material.QUARTZ_BLOCK).getStrings());
		cc.set("define.ore", new MaterialList(Material.QUARTZ_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.COAL_ORE, Material.GOLD_ORE, Material.IRON_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE, Material.QUARTZ_ORE).getStrings());
		cc.set("define.expensive-tools-armor", new MaterialList(Material.DIAMOND_AXE, Material.DIAMOND_BARDING, Material.DIAMOND_BOOTS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET, Material.DIAMOND_LEGGINGS, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE, Material.DIAMOND_SWORD, Material.GOLD_AXE, Material.GOLD_BARDING, Material.DIAMOND_BARDING, Material.IRON_BARDING, Material.GOLD_AXE, Material.GOLD_BOOTS, Material.GOLD_CHESTPLATE, Material.GOLD_HELMET, Material.GOLD_LEGGINGS, Material.GOLD_NUGGET, Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.GOLD_SWORD, Material.GOLD_HELMET, Material.IRON_AXE, Material.IRON_BOOTS, Material.IRON_CHESTPLATE, Material.IRON_HELMET, Material.IRON_LEGGINGS, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_PICKAXE, Material.IRON_SPADE, Material.IRON_SWORD).getStrings());
		cc.set("define.other", new MaterialList(Material.GOLDEN_APPLE, Material.ENDER_PEARL, Material.ENCHANTED_BOOK).getStrings());
	}
	
	public GMap<String, GBiset<Double, GList<Material>>> getWorth()
	{
		return worth;
	}
	
	public Double getUndefinedWorth()
	{
		return undefinedWorth;
	}
}
