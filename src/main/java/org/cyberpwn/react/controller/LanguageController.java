package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.util.GList;

public class LanguageController extends Controller
{
	public LanguageController(React react)
	{
		super(react);
	}
	
	@Override
	public void start()
	{
		File f = new File(new File(React.getInstance().getDataFolder(), "lang"), "en.yml");
		
		try
		{
			getDefaultLanguage().toYaml().save(f);
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void handleLanguage()
	{
		final String code = React.getInstance().getConfiguration().getString("lang").toLowerCase();
		
		React.instance().getD().s("Language Key: " + code);
		
		if(!code.equals("en"))
		{
			React.instance().getD().s(String.format(Info.URL_LANGUAGE, code));
			
			File f = new File(React.instance().getDataFolder(), "lang");
			
			for(File i : f.listFiles())
			{
				if(i.getName().equals(code + ".yml"))
				{
					ClusterConfig cc = new ClusterConfig();
					FileConfiguration fc = new YamlConfiguration();
					
					try
					{
						fc.load(i);
						cc.set(fc);
						React.instance().getD().s("Found local language. Setting");
						setLanguage(cc);
						return;
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
			new Fetcher(String.format(Info.URL_LANGUAGE, code), new FCCallback()
			{
				@Override
				public void run()
				{
					React.instance().getD().s("Found Language for Key: " + code);
					ClusterConfig cc = new ClusterConfig();
					cc.set(fc());
					setLanguage(cc);
				}
			}).start();
		}
	}
	
	public void setLanguage(ClusterConfig cc)
	{
		for(Field i : new GList<Field>(L.class.getDeclaredFields()))
		{
			try
			{
				String key = "react." + i.getName().toLowerCase().replaceAll("_", ".");
				
				if(cc.contains(key))
				{
					i.set(null, cc.getString(key));
				}
			}
			
			catch(Exception e)
			{
				
			}
		}
	}
	
	public ClusterConfig getDefaultLanguage()
	{
		ClusterConfig cc = new ClusterConfig();
		
		for(Field i : L.class.getDeclaredFields())
		{
			try
			{
				String key = "react." + i.getName().toLowerCase().replaceAll("_", ".");
				String value = (String) i.get(null);
				
				cc.set(key, value);
			}
			
			catch(Exception e)
			{
				
			}
		}
		
		return cc;
	}
}
