package org.cyberpwn.react.nms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.cyberpwn.react.util.Default;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class NMS18 implements AbstractNMS
{
	@Override
	public boolean isCapable(String version)
	{
		return version.startsWith("1.8");
	}
	
	@Override
	public boolean isCapable()
	{
		return isCapable(Bukkit.getBukkitVersion());
	}
	
	@Override
	public int ping(Player player)
	{
		return ((CraftPlayer) player).getHandle().ping;
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
}
