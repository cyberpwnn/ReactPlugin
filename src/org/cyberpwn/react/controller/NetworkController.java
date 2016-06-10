package org.cyberpwn.react.controller;

import java.io.IOException;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.network.ReactServer;
import org.cyberpwn.react.util.ReactRunnable;

public class NetworkController extends Controller
{
	private ReactServer server;
	
	public NetworkController(React react)
	{
		super(react);
		
		server = null;
	}
	
	public void start()
	{
		getReact().scheduleSyncTask(40, new Runnable()
		{
			@Override
			public void run()
			{
				ClusterConfig cc = React.instance().getConfiguration();
				
				if(cc.getBoolean("react-remote.enable"))
				{
					int port = cc.getInt("react-remote.port");
					
					try
					{
						server = new ReactServer(port, cc);
						server.start();
					}
					
					catch(IOException e)
					{
						React.fail(e, "Failed to bind to port.");
						React.instance().getD().f("FAILED TO BIND TO PORT: " + cc.getInt("react-remote.port"));
						React.instance().getD().f("React Server failed to bind to the target address");
						React.instance().getD().w("1. React Failed to shut down the server previously");
						React.instance().getD().w("2. That port is already in use.");
						React.instance().getD().s("To fix this, try rebooting. Reloading won't work.");
					}
				}
			}
		});
	}
	
	public void tick()
	{
		if(server == null)
		{
			return;
		}
		
		ReactServer.reactData.sample(getReact());
		
		for(ReactRunnable i : ReactServer.runnables)
		{
			i.run(getReact());
		}
		
		ReactServer.runnables.clear();
	}
	
	public void stop()
	{
		server.interrupt();
	}
}
