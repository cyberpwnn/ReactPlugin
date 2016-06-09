package org.cyberpwn.react.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.object.GList;

public class LanguageController extends Controller
{
	public LanguageController(React react)
	{
		super(react);
	}
	
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
			
			try
			{
				new Fetcher(new URL(String.format(Info.URL_LANGUAGE, code)), new FCCallback()
				{
					public void run()
					{
						React.instance().getD().s("Found Language for Key: " + code);
						ClusterConfig cc = new ClusterConfig();
						cc.set(fc());
						setLanguage(cc);
					}
				}).start();
			} 
			
			catch(MalformedURLException e)
			{
				e.printStackTrace();
			}
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
