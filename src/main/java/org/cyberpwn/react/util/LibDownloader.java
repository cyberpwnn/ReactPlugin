package org.cyberpwn.react.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.cyberpwn.react.React;

public class LibDownloader
{
	private static final Class<?>[] parameters = new Class[] {URL.class};
	
	public enum Library
	{
		HTMMLUNIT("https://github.com/cyberpwnn/React/blob/master/serve/lib/HTMLUnit-2.15-OSGi.jar?raw=true/", "HTMLUnit 2.15", "Used for HTTP connections with cloudflare protected sites", "htmlunit_2_15"),
		JSOUP("https://github.com/cyberpwnn/React/blob/master/serve/lib/jsoup-1.7.2.jar?raw=true", "JSoup 1.7.2", "Used for HTTP Processing", "jsoup_1_7_2");
		
		private String url = "";
		private String name = "";
		private String description = "";
		private String fileName = "";
		
		Library(String url, String name, String description, String fileName)
		{
			setUrl(url);
			setName(name);
			setDescription(description);
			setFileName(fileName);
		}
		
		public String getUrl()
		{
			return url;
		}
		
		public void setUrl(String url)
		{
			this.url = url;
		}
		
		public String getName()
		{
			return name;
		}
		
		public void setName(String name)
		{
			this.name = name;
		}
		
		public String getDescription()
		{
			return description;
		}
		
		public void setDescription(String description)
		{
			this.description = description;
		}
		
		public String getFileName()
		{
			return fileName;
		}
		
		public void setFileName(String fileName)
		{
			this.fileName = fileName;
		}
	}
	
	/**
	 * Download a library
	 *
	 * @param lib
	 *            Library to download
	 */
	public static void downloadLib(Library lib)
	{
		downloadLib(lib.getUrl(), lib.getName(), lib.getDescription(), lib.getFileName());
	}
	
	/**
	 * Download a library
	 *
	 * @param url
	 *            URL to download it from
	 * @param name
	 *            Name of the lib
	 * @param description
	 *            Description
	 * @param fileName
	 *            filename to save it as
	 */
	public static void downloadLib(String url, String name, String description, String fileName)
	{
		File root = new File(React.instance().getDataFolder(), "lib");
		File jar = new File(root, fileName + ".jar");
		
		if(!jar.exists())
		{
			try
			{
				FU.copyURLToFile(new URL(url), jar);
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			addURL(new URL("jar:file:" + jar.toString() + "!/"));
		}
		
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void addURL(URL u) throws IOException
	{
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		
		try
		{
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] {u});
		}
		
		catch(Throwable t)
		{
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}
}
