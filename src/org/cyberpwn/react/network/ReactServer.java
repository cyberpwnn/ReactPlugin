package org.cyberpwn.react.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.cyberpwn.react.React;
import org.cyberpwn.react.action.Actionable;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ReactRunnable;

public class ReactServer extends Thread
{
	public static ReactData reactData;
	public static GList<ReactRunnable> runnables;
	
	private boolean running;
	private ServerSocket serverSocket;
	private ClusterConfig cc;
	private GList<String> actions;
	
	public ReactServer(int port, ClusterConfig config) throws IOException
	{
		reactData = new ReactData();
		cc = config;
		runnables = new GList<ReactRunnable>();
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(50);
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
					handleCommand(request.getCommand().toUpperCase(), response);
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
				interrupt();
			}
		}
	}
	
	public void handleCommand(String command, final PacketResponse response)
	{
		if(command.equals(PacketRequestType.GET_SAMPLES.toString()))
		{
			response.put("type", PacketResponseType.OK);
			
			for(String i : reactData.getSamples().keySet())
			{
				response.put(i, reactData.getSamples().get(i));
			}
		}
		
		else if(command.equals(PacketRequestType.GET_ACTIONS.toString()))
		{
			response.put("type", PacketResponseType.OK);
			response.put("actions", actions);
		}
		
		else if(command.startsWith("ACTION "))
		{
			for(final String i : actions)
			{
				if(command.equals("ACTION " + i))
				{
					runnables.add(new ReactRunnable()
					{
						public void run()
						{
							for(Actionable j : getReact().getActionController().getActions().k())
							{
								if(j.getName().toLowerCase().equals(i.toLowerCase()))
								{
									j.act();
									response.put("type", PacketResponseType.OK);
									return;
								}
							}
						}
					});
				}
				
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
