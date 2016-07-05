package org.cyberpwn.react.network;

import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberpwn.react.util.GInputStreamReader;
import org.cyberpwn.react.util.GThread;
import org.cyberpwn.react.util.GURL;

public class Fetcher extends GThread
{
	private String url;
	private FCCallback callback;
	
	public Fetcher(String s, FCCallback callback)
	{
		this.url = s;
		this.callback = callback;
	}
	
	public void run()
	{
		FileConfiguration fc = new YamlConfiguration();
		
		try
		{
			fc.load(new GInputStreamReader(GURL.um(url).openStream()));
			callback.run(fc);
		}
		
		catch(IOException e)
		{
			return;
		}
		
		catch(InvalidConfigurationException e)
		{
			return;
		}
	}
}
