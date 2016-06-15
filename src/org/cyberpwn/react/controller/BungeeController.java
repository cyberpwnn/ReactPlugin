package org.cyberpwn.react.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.Verbose;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeController extends Controller implements PluginMessageListener, Configurable
{
	private String name;
	private ClusterConfig cc;
	private GMap<String, JSONObject> data;
	
	public BungeeController(React react)
	{
		super(react);
		
		data = new GMap<String, JSONObject>();
		
		try
		{
			cc = new ClusterConfig();
			name = "1337";
			
			react.getDataController().load(null, this);
			
			if(cc.getBoolean("support-bungeecord"))
			{
				react.getServer().getMessenger().registerOutgoingPluginChannel(react, "BungeeCord");
				react.getServer().getMessenger().registerIncomingPluginChannel(react, "BungeeCord", this);
			}
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void start()
	{
		if(!cc.getBoolean("support-bungeecord"))
		{
			return;
		}
		
		new Task(100)
		{
			public void run()
			{
				try
				{
					askName();
				}
				
				catch(Exception e)
				{
					
				}
			}
		};
				
		new Task(cc.getInt("interval"))
		{
			public void run()
			{
				broadcastData();
			}
		};
	}
	
	public void stop()
	{
		
	}
	
	public void askName()
	{
		if(!cc.getBoolean("support-bungeecord"))
		{
			return;
		}
		
		try
		{
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer");
			Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(react, "BungeeCord", out.toByteArray());
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void broadcast(String msg)
	{
		if(!cc.getBoolean("support-bungeecord"))
		{
			return;
		}
		
		try
		{
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("ReactBroadcast");
			
			try
			{
				msgout.writeUTF(name);
				msgout.writeUTF(msg);
			}
			
			catch(IOException e)
			{
				
			}
			
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
			Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(react, "BungeeCord", out.toByteArray());
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void broadcastData()
	{
		if(!cc.getBoolean("support-bungeecord"))
		{
			return;
		}
		
		try
		{
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("ReactData");
			
			try
			{
				msgout.writeUTF(name);
				msgout.writeUTF(React.getPacket().toJSON().toString());
			}
			
			catch(IOException e)
			{
				
			}
			
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
			Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(react, "BungeeCord", out.toByteArray());
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{
		if(!cc.getBoolean("support-bungeecord"))
		{
			return;
		}
		
		if(!channel.equals("BungeeCord"))
		{
			return;
		}
		
		try
		{
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			
			if(subchannel.equals("ReactBroadcast"))
			{
				short len = in.readShort();
				byte[] msgbytes = new byte[len];
				in.readFully(msgbytes);
				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				
				try
				{
					String name = msgin.readUTF();
					String data = msgin.readUTF();
					react.getMonitorController().broadcast(name, data);
					Verbose.x("bungeecord", "REACT PANIC FROM " + name + " >> " + data);
				}
				
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			else if(subchannel.equals("GetServer"))
			{
				String s = in.readUTF();
				name = s;
			}
			
			else if(subchannel.equals("ReactData"))
			{
				short len = in.readShort();
				byte[] msgbytes = new byte[len];
				in.readFully(msgbytes);
				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				
				try
				{
					String name = msgin.readUTF();
					String data = msgin.readUTF();
					
					if(!name.equals("1337"))
					{
						this.data.put(name, new JSONObject(data));
					}
				}
				
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			else if(subchannel.equals("GetServer"))
			{
				String s = in.readUTF();
				name = s;
			}
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void onNewConfig()
	{
		cc.set("support-bungeecord", true);
		cc.set("interval", 20);
	}
	
	@Override
	public void onReadConfig()
	{
		
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "bungeecord";
	}
	
	public JSONObject get(String server)
	{
		return data.get(server);
	}
	
	public String getName()
	{
		return name;
	}
	
	public GMap<String, JSONObject> getData()
	{
		return data;
	}
}
