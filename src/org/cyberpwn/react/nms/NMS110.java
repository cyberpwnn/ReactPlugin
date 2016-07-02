package org.cyberpwn.react.nms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.cyberpwn.react.util.Default;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PlayerConnection;

public class NMS110 implements AbstractNMS
{
	@Override
	public boolean isCapable(String version)
	{
		return version.startsWith("1.10");
	}
	
	@Override
	public boolean isCapable()
	{
		return isCapable(Bukkit.getBukkitVersion());
	}
	
	@Override
	public int ping(Player player)
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
	
	@Override
	public void packetTitle(Player player, String title, String subTitle)
	{
		packetTitle(player, title);
		packetSubTitle(player, subTitle);
	}
	
	@Override
	public void packetTitle(Player player, String title, String subTitle, String actionTitle)
	{
		packetTitle(player, title);
		packetSubTitle(player, subTitle);
		packetActionTitle(player, actionTitle);
	}
	
	@Override
	public void packetTitle(Player player, String title, String subTitle, int in, int out, int stay)
	{
		packetTitle(player, title, in, out, stay);
		packetSubTitle(player, subTitle, in, out, stay);
	}
	
	@Override
	public void packetTitle(Player player, String title, String subTitle, String actionTitle, int in, int out, int stay)
	{
		packetTitle(player, title, in, out, stay);
		packetSubTitle(player, subTitle, in, out, stay);
		packetActionTitle(player, actionTitle);
	}
	
	@Override
	public void packetTitle(Player player, String title)
	{
		packetTitle(player, title, Default.TITLE_FADE_IN, Default.TITLE_FADE_OUT, Default.TITLE_FADE_STAY);
	}
	
	@Override
	public void packetSubTitle(Player player, String title)
	{
		packetSubTitle(player, title, Default.TITLE_FADE_IN, Default.TITLE_FADE_OUT, Default.TITLE_FADE_STAY);
	}
	
	@Override
	public void packetTitle(Player player, String title, int in, int out, int stay)
	{
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, in, stay, out);
		connection.sendPacket((Packet<?>) packetPlayOutTimes);
		title = title.replaceAll("%player%", player.getDisplayName());
		title = ChatColor.translateAlternateColorCodes((char) '&', (String) title);
		IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a((String) ("{\"text\": \"" + title + "\"}"));
		PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
		connection.sendPacket((Packet<?>) packetPlayOutTitle);
	}
	
	@Override
	public void packetSubTitle(Player player, String title, int in, int out, int stay)
	{
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, in, stay, out);
		connection.sendPacket((Packet<?>) packetPlayOutTimes);
		title = title.replaceAll("%player%", player.getDisplayName());
		title = ChatColor.translateAlternateColorCodes((char) '&', (String) title);
		IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a((String) ("{\"text\": \"" + title + "\"}"));
		PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
		connection.sendPacket((Packet<?>) packetPlayOutSubTitle);
	}
	
	@Override
	public void packetActionTitle(Player player, String title)
	{
		CraftPlayer p = (CraftPlayer) player;
		IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a((String) ("{\"text\": \"" + title + "\"}"));
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
		p.getHandle().playerConnection.sendPacket((Packet<?>) ppoc);
	}
	
	public String getEntityName(Entity e)
	{
		return e.getCustomName();
	}
	
	@Override
	public void clearTitle(Player player)
	{
		packetTitle(player, " ", " ", 20, 10, 20);
	}
	
	@Override
	public void relight(Location location)
	{
		((CraftWorld) location.getWorld()).getHandle().w(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}
}
