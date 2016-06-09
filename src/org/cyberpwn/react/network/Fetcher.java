package org.cyberpwn.react.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Fetcher extends Thread
{
	private URL url;
	private FCCallback callback;
	
	public Fetcher(URL url, FCCallback callback)
	{
		this.url = url;
		this.callback = callback;
	}
	
	public void run()
	{
		FileConfiguration fc = new YamlConfiguration();
		
		try
		{
			fc.load(new InputStreamReader(url.openStream()));
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
