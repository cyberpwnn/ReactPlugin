package org.cyberpwn.react.controller;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.util.GList;

public class ChannelListenController extends Controller implements PluginMessageListener
{
	private GList<Player> scanners;
	
	public ChannelListenController(React react)
	{
		super(react);
		
		scanners = new GList<Player>();
		
		getReact().getServer().getMessenger().registerIncomingPluginChannel(getReact(), "BungeeCord", this);
	}
	
	@Override
	public void onPluginMessageReceived(String ch, Player pl, byte[] data)
	{
		if(!scanners.isEmpty())
		{
			DataInputStream dos = new DataInputStream(new ByteArrayInputStream(data));
			
			try
			{
				String s = dos.readUTF();
				
				for(Player i : scanners)
				{
					i.sendMessage(Info.TAG + ChatColor.RED + " $RAW: " + s);
				}
			}
			
			catch(IOException e)
			{
				
			}
		}
	}
	
	public void scan(Player p)
	{
		if(scanners.contains(p))
		{
			p.sendMessage(Info.TAG + ChatColor.RED + "Message Sniffing Disabled");
			scanners.remove(p);
		}
		
		else
		{
			if(p.hasPermission(Info.PERM_MONITOR))
			{
				p.sendMessage(Info.TAG + ChatColor.GREEN + "Message Sniffing Enabled");
				scanners.add(p);
			}
		}
	}
}
