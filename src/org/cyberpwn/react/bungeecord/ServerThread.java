package org.cyberpwn.react.bungeecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.action.ActionInstabilityCause;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.json.JSONArray;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.object.GMap;
import org.cyberpwn.react.object.GStub;
import org.cyberpwn.react.object.GTime;
import org.cyberpwn.react.object.Value;
import org.cyberpwn.react.util.Dispatcher;
import org.cyberpwn.react.util.Verbose;

public class ServerThread extends Thread
{
	private boolean running;
	private ServerSocket serverSocket;
	private GMap<String, Client> clients;
	private ClusterConfig cc;
	
	public ServerThread(int port, ClusterConfig config) throws IOException, BindException
	{
		cc = config;
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(50);
		running = true;
		clients = new GMap<String, Client>();
	}
	
	public void run()
	{
		while(running)
		{
			if(Thread.interrupted())
			{
				running = false;
				
				try
				{
					serverSocket.close();
				}
				
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
				l("Stopping Server Thread");
				l("Stopping " + clients.size() + " Clients...");
				
				for(String i : clients.k())
				{
					clients.get(i).interrupt();
				}
				
				l("Stopped Server Thread");
				return;
			}
			
			try
			{
				Socket socket = serverSocket.accept();
				DataInputStream i = new DataInputStream(socket.getInputStream());
				DataOutputStream o = new DataOutputStream(socket.getOutputStream());
				String id = socket.getInetAddress().toString();
				
				Verbose.x("react-server", ChatColor.AQUA + "Client Connecting: " + id);
				l("Client Connecting @ " + id);
				int versionCode = i.readInt();
				String username = i.readUTF();
				String password = i.readUTF();
				String response = "ok";
				
				if(Info.VERSION_CODE != versionCode)
				{
					if(Info.VERSION_CODE > versionCode)
					{
						if(versionCode < 1505)
						{
							response = "Outdated. Protocol has changed.";
							o.writeUTF(response);
							o.flush();
							socket.close();
							continue;
						}
					}
				}
				
				if(cc.contains("react-remote.users." + username + ".enabled") && !cc.getBoolean("react-remote.users." + username + ".enabled"))
				{
					response = "Invalid Username/Password Combination";
				}
				
				else if(cc.contains("react-remote.users." + username + ".password") && cc.getString("react-remote.users." + username + ".password").equals(password))
				{
					if(clients.containsKey(username))
					{
						response = "Invalid Session. Someone else is using this account.";
					}
				}
				
				else
				{
					response = "Invalid Username/Password Combination";
				}
				
				o.writeUTF(response);
				o.flush();
				
				if(response.equals("ok"))
				{
					Boolean shot = i.readBoolean();
					
					if(shot)
					{
						MonitorPacket packet = React.packet;
						
						if(packet == null)
						{
							continue;
						}
						
						packet.put("threads", new Value(Thread.activeCount()));
						
						JSONObject js = packet.toJSON();
						js.put("stamp", System.nanoTime());
						js.put("mem", (Runtime.getRuntime().maxMemory() / 1024 / 1024));
						js.put("server", Bukkit.getBukkitVersion());
						js.put("size", Client.size);
						js.put("username", username);
						
						JSONArray jsp = new JSONArray();
						
						if(!ActionInstabilityCause.issues.isEmpty())
						{
							GStub xi = ActionInstabilityCause.issues.get(0);
							JSONObject stub = new JSONObject();
							stub.put("title", xi.getTitle());
							stub.put("text", xi.getText());
							stub.put("ago", new GTime(System.currentTimeMillis() - xi.getTime().getTotalDuration()).ago());
							jsp.put(stub);
						}
						
						js.put("issues", jsp);
						
						o.writeUTF(js.toString());
						o.flush();
					}
					
					else
					{
						l("Client OK: " + username);
						Client client = new Client(socket, username);
						client.start();
						clients.put(username, client);
						l("Client Authenticated as " + username);
						Client.size = clients.size();
					}
				}
				
				else
				{
					l(username + " Failed to connect: " + response);
				}
			}
			
			catch(SocketTimeoutException e)
			{
				for(String i : clients.k())
				{
					if(!clients.get(i).isAlive())
					{
						clients.remove(i);
						l(i + " disconnected (react client)");
						Verbose.x("react-server", ChatColor.AQUA + "Client Disconnect: " + i);
					}
				}
				
				continue;
			}
			
			catch(IOException e)
			{
				interrupt();
			}
		}
	}
	
	public void l(String msg)
	{
		System.out.println("[REACT SERVER]: " + msg);
		Dispatcher.r("[REACT SERVER]: " + msg);
	}
}
