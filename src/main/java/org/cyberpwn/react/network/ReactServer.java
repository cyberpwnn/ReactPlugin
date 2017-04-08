package org.cyberpwn.react.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.cyberpwn.react.React;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.controller.RemoteController;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.server.ReactUser;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.HijackedConsole;
import org.cyberpwn.react.util.M;
import org.cyberpwn.react.util.Platform;
import org.cyberpwn.react.util.ReactRunnable;
import org.cyberpwn.react.util.TaskLater;
import org.cyberpwn.react.util.TimingsPackage;

public class ReactServer extends Thread
{
	public static ReactData reactData;
	public static GList<ReactRunnable> runnables;
	public static int requests;
	public static TimingsPackage pack = null;
	
	private boolean running;
	private ServerSocket serverSocket;
	private GList<String> actions;
	private RemoteController rc;
	private long ms;
	public static int size;
	public static double perc;
	
	public ReactServer(int port) throws IOException
	{
		size = 0;
		perc = 0;
		React.instance().getD().info("Starting React Server @port/" + port);
		reactData = new ReactData();
		runnables = new GList<ReactRunnable>();
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(500);
		actions = new GList<String>();
		rc = new RemoteController();
		
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
		ms = M.ms();
		
		while(running)
		{
			if(React.STOPPING)
			{
				interrupt();
			}
			
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
				ReactUser u = rc.auth(request.getUsername(), request.getPassword());
				
				if(u != null)
				{
					handleCommand(request.getCommand(), response, request.getUsername(), u);
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
	
	public void handleCommand(String command, final PacketResponse response, String name, ReactUser u)
	{
		if(command.equals(PacketRequestType.GET_SAMPLES.toString()))
		{
			response.put("type", PacketResponseType.OK);
			
			for(String i : reactData.getSamples().keySet())
			{
				response.put(i, reactData.getSamples().get(i));
			}
			
			response.put("task queue", size);
			response.put("task load", perc * 100.0);
			response.put("memory allocated exact (MB)", (double) (Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0));
			response.put("memory free exact (MB)", (double) (Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0));
			response.put("requests per second", (double) (requests / (1 + (M.ms() - ms) / 1000)));
			response.put("memory-max", Runtime.getRuntime().maxMemory() / 1024 / 1024);
			response.put("processor-cores", Runtime.getRuntime().availableProcessors());
			response.put("CPU Usage", 100 * (Platform.CPU.getCPULoad()));
			response.put("CPU Process Usage", 100 * (Platform.CPU.getProcessCPULoad()));
			response.put("Physical Memory Usage", Platform.MEMORY.PHYSICAL.getUsedMemory() / 1024 / 1024);
			response.put("Virtual Memory Usage", Platform.MEMORY.VIRTUAL.getUsedMemory() / 1024 / 1024);
			response.put("Virtual Commit", Platform.MEMORY.VIRTUAL.getCommittedVirtualMemory() / 1024 / 1024);
			
			GList<String> console = HijackedConsole.out.copy();
			String data = console.toString("\n");
			
			response.put("console-s", u.canViewConsole() ? data : StringUtils.repeat("\n", 40) + "Sorry! You do not have permission to view the console!\n\n=== IDENTITY ===\n" + u.toString());
		}
		
		else if(command.equals(PacketRequestType.GET_ACTIONS.toString()))
		{
			response.put("type", PacketResponseType.OK);
			response.put("actions", actions);
		}
		
		else if(command.equals(PacketRequestType.GET_TIMINGS.toString()))
		{
			response.put("type", PacketResponseType.OK);
			
			try
			{
				response.put("timings", pack.getData());
			}
			
			catch(IOException e)
			{
				response.put("type", PacketResponseType.ERROR_NODATA);
			}
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
					
					if(u.canUseConsole())
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
									
									if(u.canUseActions())
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
