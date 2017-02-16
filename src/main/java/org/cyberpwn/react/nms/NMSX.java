package org.cyberpwn.react.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.cyberpwn.react.util.VersionBukkit;

public class NMSX
{
	public static NMSX bountifulAPI;
	private static boolean useOldMethods;
	public static String nmsver;
	
	public static void sendPacket(Player player, Object packet)
	{
		try
		{
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke((Object) player, new Object[0]);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", NMSX.getNMSClass("Packet")).invoke(playerConnection, packet);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Class<?> getNMSClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		
		try
		{
			return Class.forName("net.minecraft.server." + version + "." + name);
		}
		
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		try
		{
			Object e;
			Constructor<?> subtitleConstructor;
			
			if(title != null)
			{
				title = ChatColor.translateAlternateColorCodes((char) '&', (String) title);
				title = title.replaceAll("%player%", player.getDisplayName());
				e = NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
				Object chatTitle = NMSX.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
				subtitleConstructor = NMSX.getNMSClass("PacketPlayOutTitle").getConstructor(NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], NMSX.getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
				Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
				NMSX.sendPacket(player, titlePacket);
				e = NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
				chatTitle = NMSX.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
				subtitleConstructor = NMSX.getNMSClass("PacketPlayOutTitle").getConstructor(NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], NMSX.getNMSClass("IChatBaseComponent"));
				titlePacket = subtitleConstructor.newInstance(e, chatTitle);
				NMSX.sendPacket(player, titlePacket);
			}
			
			if(subtitle != null)
			{
				subtitle = ChatColor.translateAlternateColorCodes((char) '&', (String) subtitle);
				subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
				e = NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
				Object chatSubtitle = NMSX.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
				subtitleConstructor = NMSX.getNMSClass("PacketPlayOutTitle").getConstructor(NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], NMSX.getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
				Object subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
				NMSX.sendPacket(player, subtitlePacket);
				e = NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
				chatSubtitle = NMSX.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
				subtitleConstructor = NMSX.getNMSClass("PacketPlayOutTitle").getConstructor(NMSX.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], NMSX.getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
				subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
				NMSX.sendPacket(player, subtitlePacket);
			}
		}
		
		catch(Exception var11)
		{
			var11.printStackTrace();
		}
	}
	
	public static void clearTitle(Player player)
	{
		NMSX.sendTitle(player, 0, 0, 0, "", "");
	}
	
	public static void sendTabTitle(Player player, String header, String footer)
	{
		if(header == null)
		{
			header = "";
		}
		
		header = ChatColor.translateAlternateColorCodes((char) '&', (String) header);
		
		if(footer == null)
		{
			footer = "";
		}
		
		footer = ChatColor.translateAlternateColorCodes((char) '&', (String) footer);
		header = header.replaceAll("%player%", player.getDisplayName());
		footer = footer.replaceAll("%player%", player.getDisplayName());
		
		try
		{
			Object tabHeader = NMSX.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
			Object tabFooter = NMSX.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
			Constructor<?> titleConstructor = NMSX.getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(NMSX.getNMSClass("IChatBaseComponent"));
			Object packet = titleConstructor.newInstance(tabHeader);
			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(packet, tabFooter);
			NMSX.sendPacket(player, packet);
		}
		
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void sendActionBar(Player player, String message)
	{
		if(!VersionBukkit.tc())
		{
			return;
		}
		
		try
		{
			Object ppoc;
			Class<?> c3;
			Class<?> c2;
			Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
			Object p = c1.cast((Object) player);
			Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
			Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
			
			if(useOldMethods)
			{
				c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
				c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
				Method m3 = c2.getDeclaredMethod("a", String.class);
				Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
				ppoc = c4.getConstructor(c3, Byte.TYPE).newInstance(cbc, Byte.valueOf((byte) 2));
			}
			
			else
			{
				c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
				c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
				Object o = c2.getConstructor(String.class).newInstance(message);
				ppoc = c4.getConstructor(c3, Byte.TYPE).newInstance(o, Byte.valueOf((byte) 2));
			}
			
			Method m1 = c1.getDeclaredMethod("getHandle", new Class[0]);
			Object h = m1.invoke(p, new Object[0]);
			Field f1 = h.getClass().getDeclaredField("playerConnection");
			Object pc = f1.get(h);
			Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
			m5.invoke(pc, ppoc);
		}
		
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void sendActionBar(final Player player, final String message, int duration)
	{
		NMSX.sendActionBar(player, message);
		if(duration >= 0)
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					NMSX.sendActionBar(player, "");
				}
			}.runTaskLater((Plugin) bountifulAPI, (long) (duration + 1));
		}
		
		while(duration > 60)
		{
			int sched = (duration -= 60) % 60;
			new BukkitRunnable()
			{
				
				@Override
				public void run()
				{
					NMSX.sendActionBar(player, message);
				}
			}.runTaskLater((Plugin) bountifulAPI, (long) sched);
		}
	}
	
	public static void sendActionBarToAllPlayers(String message)
	{
		NMSX.sendActionBarToAllPlayers(message, -1);
	}
	
	public static void sendActionBarToAllPlayers(String message, int duration)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			NMSX.sendActionBar(p, message, duration);
		}
	}
	
	public static String getEntityName(Entity e)
	{
		if(VersionBukkit.tc())
		{
			return null;
		}
		
		return e.getCustomName();
	}
	
	public static int ping(Player player)
	{
		try
		{
			String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
			Object handle = craftPlayer.getMethod("getHandle").invoke(player);
			Integer ping = (Integer) handle.getClass().getDeclaredField("ping").get(handle);
			
			return ping.intValue();
		}
		
		catch(Exception e)
		{
			
		}
		
		return -1;
	}
	
	static
	{
		nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
		
		if(nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.equalsIgnoreCase("v1_7_"))
		{
			useOldMethods = true;
		}
	}
}
