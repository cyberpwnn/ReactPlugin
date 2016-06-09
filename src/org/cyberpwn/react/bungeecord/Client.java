package org.cyberpwn.react.bungeecord;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.action.ActionInstabilityCause;
import org.cyberpwn.react.json.JSONArray;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.object.GStub;
import org.cyberpwn.react.object.GTime;
import org.cyberpwn.react.object.Value;
import org.cyberpwn.react.util.Dispatcher;
import org.cyberpwn.react.util.Verbose;

public class Client extends Thread
{
	private Socket socket;
	private String username;
	public static int tickm = 100;
	private boolean running;
	private DataOutputStream o;
	public static MonitorPacket packet = null;
	public static int size;
	
	public Client(Socket socket, String username)
	{
		this.socket = socket;
		this.username = username;
		this.running = true;
	}
	
	public void run()
	{
		running = true;
		Verbose.x("react-server", ChatColor.AQUA + "Client Created: " + username);
		try
		{
			o = new DataOutputStream(socket.getOutputStream());
		}
		
		catch(IOException e2)
		{
			e2.printStackTrace();
			return;
		}
		
		while(running)
		{
			try
			{
				Thread.sleep(tickm);
				
				packet = React.packet;
				
				if(packet == null)
				{
					continue;
				}
				
				packet.put("threads", new Value(Thread.activeCount()));
				
				JSONObject js = packet.toJSON();
				js.put("stamp", System.nanoTime());
				js.put("mem", (Runtime.getRuntime().maxMemory() / 1024 / 1024));
				js.put("server", Bukkit.getBukkitVersion());
				js.put("size", size);
				js.put("username", username);
				
				JSONArray jsp = new JSONArray();
				
				if(!ActionInstabilityCause.issues.isEmpty())
				{
					GStub i = ActionInstabilityCause.issues.get(0);
					JSONObject stub = new JSONObject();
					stub.put("title", i.getTitle());
					stub.put("text", i.getText());
					stub.put("ago", new GTime(System.currentTimeMillis() - i.getTime().getTotalDuration()).ago());
					jsp.put(stub);
				}
				
				js.put("issues", jsp);
				
				o.writeUTF(js.toString());
				o.flush();
			}
			
			catch(InterruptedException e)
			{
				running = false;
				
				try
				{
					socket.close();
				}
				
				catch(IOException e1)
				{
					
				}
				
				l("Stopped Thread");
				break;
			}
			
			catch(IOException e)
			{
				running = false;
				l("Client Lost Connection");
				l("Stopped Thread");
				break;
			}
		}
	}
	
	public void l(String msg)
	{
		System.out.println("[REACT NETWORK THREAD-" + username.toUpperCase() + "]: " + msg);
		Dispatcher.r("[REACT NETWORK THREAD-" + username.toUpperCase() + "]: " + msg);
	}
	
	public boolean isRunning()
	{
		return running;
	}
}
