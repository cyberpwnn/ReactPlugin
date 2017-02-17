package org.cyberpwn.react.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.bukkit.Bukkit;
import org.cyberpwn.react.React;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.HijackedConsole;
import org.cyberpwn.react.util.ReactRunnable;
import org.cyberpwn.react.util.TaskLater;

public class ReactServer extends Thread
{
	public static ReactData reactData;
	public static GList<ReactRunnable> runnables;
	public static int requests;
	
	private boolean running;
	private ServerSocket serverSocket;
	private ClusterConfig cc;
	private GList<String> actions;
	
	public ReactServer(int port, ClusterConfig config) throws IOException
	{
		React.instance().getD().info("Starting React Server @port/" + port);
		reactData = new ReactData();
		cc = config;
		runnables = new GList<ReactRunnable>();
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(500);
		actions = new GList<String>();
		
		for(Actionable i : React.instance().getActionController().getActions().k())
		{
			if(i.isManual() && i.isEnabled())
			{
				actions.add(i.getName());
			}
		}
		
		running = true;
	}
	
	@Override
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
				
				l("Stopped Server Thread");
				return;
			}
			
			try
			{
				Socket s = serverSocket.accept();
				DataInputStream i = new DataInputStream(s.getInputStream());
				DataOutputStream o = new DataOutputStream(s.getOutputStream());
				
				PacketRequest request = new PacketRequest(new JSONObject(i.readUTF()));
				PacketResponse response = new PacketResponse();
				
				if(cc.contains("react-remote.users." + request.getUsername() + ".enabled") && cc.getBoolean("react-remote.users." + request.getUsername() + ".enabled") && cc.contains("react-remote.users." + request.getUsername() + ".password") && cc.getString("react-remote.users." + request.getUsername() + ".password").equals(request.getPassword()))
				{
					handleCommand(request.getCommand(), response, request.getUsername());
					requests++;
				}
				
				else
				{
					response.put("type", PacketResponseType.ERROR_INVALID_LOGIN);
				}
				
				o.writeUTF(response.toString());
				o.flush();
				s.close();
			}
			
			catch(SocketTimeoutException e)
			{
				continue;
			}
			
			catch(IOException e)
			{
				
			}
		}
	}
	
	public void handleCommand(String command, final PacketResponse response, String name)
	{
		if(command.equals(PacketRequestType.GET_SAMPLES.toString()))
		{
			response.put("type", PacketResponseType.OK);
			
			for(String i : reactData.getSamples().keySet())
			{
				response.put(i, reactData.getSamples().get(i));
			}
			
			response.put("memory-max", Runtime.getRuntime().maxMemory() / 1024 / 1024);
			response.put("processor-cores", Runtime.getRuntime().availableProcessors());
			
			GList<String> console = HijackedConsole.out.copy();
			String data = console.toString("\n");
			
			response.put("console-s", data);
		}
		
		else if(command.equals(PacketRequestType.GET_ACTIONS.toString()))
		{
			response.put("type", PacketResponseType.OK);
			response.put("actions", actions);
		}
		
		else if(command.equals(PacketRequestType.GET_BASIC.toString()))
		{
			response.put("type", PacketResponseType.OK);
			response.put("version", Bukkit.getVersion());
			response.put("bukkit-version", Bukkit.getBukkitVersion());
		}
		
		else if(command.startsWith("COMMAND "))
		{
			String cmd = command.replaceFirst("COMMAND ", "");
			
			response.put("type", PacketResponseType.OK);
			
			runnables.add(new ReactRunnable()
			{
				@Override
				public void run()
				{
					l("Received Remote command: " + cmd);
					
					if(cc.contains("react-remote.users." + name + ".permission.use-console"))
					{
						if(cc.getBoolean("react-remote.users." + name + ".permission.use-console"))
						{
							new TaskLater(2)
							{
								@Override
								public void run()
								{
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
								}
							};
						}
						
						else
						{
							l("Permission denied for remote user: " + name + " to use command " + cmd);
						}
					}
					
					else
					{
						l("Permission denied for remote user: " + name + " to use command " + cmd);
					}
				}
			});
		}
		
		else if(command.startsWith("ACTION "))
		{
			boolean fi = false;
			
			for(final String i : actions)
			{
				if(command.equalsIgnoreCase("ACTION " + i))
				{
					fi = true;
					response.put("type", PacketResponseType.OK);
					
					runnables.add(new ReactRunnable()
					{
						@Override
						public void run()
						{
							for(Actionable j : getReact().getActionController().getActions().k())
							{
								if(j.getName().equalsIgnoreCase(i))
								{
									l("Action Packet Received: " + j.getName());
									
									if(cc.contains("react-remote.users." + name + ".permission.use-console"))
									{
										if(cc.getBoolean("react-remote.users." + name + ".permission.use-console"))
										{
											l("Action " + j.getName() + " Executed");
											j.manual(Bukkit.getServer().getConsoleSender());
											return;
										}
										
										else
										{
											l("Permission denied for remote user: " + name + " to use action " + j.getName());
										}
									}
									
									else
									{
										l("Permission denied for remote user: " + name + " to use action " + j.getName());
									}
								}
							}
						}
					});
				}
			}
			
			if(!fi)
			{
				response.put("type", PacketResponseType.ERROR_INVALID_ACTION);
			}
		}
		
		else
		{
			response.put("type", PacketResponseType.ERROR_INVALID_REQUEST);
		}
	}
	
	public void l(String s)
	{
		System.out.println("[ReactServer]: " + s);
	}
}
