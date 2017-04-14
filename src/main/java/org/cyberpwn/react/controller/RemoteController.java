package org.cyberpwn.react.controller;

import java.io.File;
import org.cyberpwn.react.React;
import org.cyberpwn.react.server.ReactUser;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.GList;

public class RemoteController
{
	private GList<ReactUser> users;
	
	public RemoteController()
	{
		users = new GList<ReactUser>();
		reload();
	}
	
	public ReactUser auth(String username, String password)
	{
		for(ReactUser i : users)
		{
			if(i.getUsername().equals(username) && password.equals(i.getPassword()))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void reload()
	{
		new ASYNC()
		{
			@Override
			public void async()
			{
				users.clear();
				System.out.println("[React Server]: Loading Remote Users");
				File base = new File(React.instance().getDataFolder(), "remote-users");
				
				if(!base.exists())
				{
					base.mkdirs();
				}
				
				ReactUser m = new ReactUser("example-user");
				m.reload();
				
				for(File i : base.listFiles())
				{
					ReactUser u = new ReactUser(i.getName().substring(0, i.getName().length() - 4));
					u.reload();
					
					if(!u.isEnabled())
					{
						continue;
					}
					
					System.out.println("[React Server]: Loaded User: " + u.getUsername());
					users.add(u);
				}
			}
		};
	}
}
