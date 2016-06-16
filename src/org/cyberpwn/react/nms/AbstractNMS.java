package org.cyberpwn.react.nms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface AbstractNMS
{
	/**
	 * If the supplied version is capable of working with the NMS/Craftbukkit
	 * classpath.
	 * 
	 * @param version
	 *            The version of bukkit/spigot.
	 * @return true if it is capable of executing packets.
	 */
	boolean isCapable(String version);
		
	/**
	 * If the current version is capable of working with the current version
	 * 
	 * @return true if it is capable of executing packets.
	 */
	boolean isCapable();
	
	/**
	 * Returns the ping of the given player
	 * 
	 * @param player
	 *            the player
	 * @return ping in milliseconds
	 */
	int ping(Player player);
	
	void clearTitle(Player player);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param title
	 *            the title
	 * @param subTitle
	 *            the subtitle
	 */
	void packetTitle(Player player, String title, String subTitle);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param title
	 *            the title
	 * @param subTitle
	 *            the subtitle
	 * @param actionTitle
	 *            the action title
	 */
	void packetTitle(Player player, String title, String subTitle, String actionTitle);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param title
	 *            the title
	 * @param subTitle
	 *            the subtitle
	 * @param in
	 *            the fade in (ticks)
	 * @param out
	 *            the fade out (ticks)
	 * @param stay
	 *            the stay time (ticks)
	 */
	void packetTitle(Player player, String title, String subTitle, int in, int out, int stay);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param title
	 *            the title
	 * @param subTitle
	 *            the subtitle
	 * @param actionTitle
	 *            the action title
	 * @param in
	 *            the fade in (ticks)
	 * @param out
	 *            the fade out (ticks)
	 * @param stay
	 *            the stay time (ticks)
	 */
	void packetTitle(Player player, String title, String subTitle, String actionTitle, int in, int out, int stay);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param title
	 *            the title
	 */
	void packetTitle(Player player, String title);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param subtitle
	 *            the title
	 */
	void packetSubTitle(Player player, String subtitle);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param title
	 *            the title
	 * @param in
	 *            the fade in (ticks)
	 * @param out
	 *            the fade out (ticks)
	 * @param stay
	 *            the stay time (ticks)
	 */
	void packetTitle(Player player, String title, int in, int out, int stay);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param subtitle
	 *            the subtitle
	 * @param in
	 *            the fade in (ticks)
	 * @param out
	 *            the fade out (ticks)
	 * @param stay
	 *            the stay time (ticks)
	 */
	void packetSubTitle(Player player, String subtitle, int in, int out, int stay);
	
	/**
	 * Sends a title packet to the player
	 * 
	 * @param player
	 *            the player
	 * @param actionTitle
	 *            the action title
	 */
	void packetActionTitle(Player player, String actionTitle);
	
	String getEntityName(Entity e);
}
