package org.cyberpwn.react.util;

import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.object.GMap;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class ProtocolLibHandler
{
	private ProtocolManager pm;
	private GMap<Player, Integer> lastTimings;
	
	public ProtocolLibHandler(final React react)
	{
		this.lastTimings = new GMap<Player, Integer>();
		this.pm = ProtocolLibrary.getProtocolManager();
		
		pm.addPacketListener(new PacketAdapter(react, ListenerPriority.HIGH, new PacketType[] { PacketType.Play.Server.TITLE })
		{
			public void onPacketSending(PacketEvent e)
			{
				if(e.getPacketType().equals(PacketType.Play.Server.TITLE))
				{
					Player p = e.getPlayer();
					
					if(!react.getMonitorController().isMonitoring(p))
					{
						return;
					}
					
					PacketContainer c = e.getPacket();
					int x = c.getIntegers().getValues().get(0) + c.getIntegers().getValues().get(1) + c.getIntegers().getValues().get(2);
					
					if(x < 0)
					{
						if(lastTimings.containsKey(p))
						{
							x = lastTimings.get(p);
						}
						
						else
						{
							x = 60;
						}
					}
						
					else
					{
						lastTimings.put(p, x);
					}
					
					if(react.getMonitorController().isMonitoring(p) && !react.getMonitorController().getPacketed().containsKey(p))
					{
						react.getMonitorController().getPacketed().put(p, x);
					}
				}
			}
		});
	}
}
