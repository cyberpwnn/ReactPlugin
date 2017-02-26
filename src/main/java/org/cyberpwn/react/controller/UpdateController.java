package org.cyberpwn.react.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.logging.LogFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.updater.ConnectionFailedException;
import org.cyberpwn.react.updater.SpigotSiteCore;
import org.cyberpwn.react.updater.resource.Resource;
import org.cyberpwn.react.updater.resource.ResourceManager;
import org.cyberpwn.react.updater.user.InvalidCredentialsException;
import org.cyberpwn.react.updater.user.SpigotUser;
import org.cyberpwn.react.updater.user.TwoFactorAuthenticationException;
import org.cyberpwn.react.updater.user.User;
import org.cyberpwn.react.updater.user.UserManager;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.LibDownloader;
import org.cyberpwn.react.util.LibDownloader.Library;
import org.cyberpwn.react.util.N;
import org.cyberpwn.react.util.PluginUtil;
import org.cyberpwn.react.util.SilentSender;
import org.cyberpwn.react.util.SpigotAuthUser;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.TaskLater;

public class UpdateController extends Controller implements Configurable
{
	private ClusterConfig cc;
	private SpigotSiteCore api;
	private SpigotUser user;
	public static boolean invalid = false;
	private File browseCookies;
	private GMap<String, String> cookie;
	private boolean setup;
	private boolean updated;
	
	public UpdateController(React react)
	{
		super(react);
		
		updated = false;
		setup = false;
		browseCookies = new File(new File(React.instance().getDataFolder(), "cache"), "chocolate-chip.cookie");
		cc = new ClusterConfig();
		api = null;
		cookie = new GMap<String, String>();
		user = null;
		initialize();
	}
	
	public void readCookies() throws IOException
	{
		if(!browseCookies.exists())
		{
			browseCookies.getParentFile().mkdirs();
			browseCookies.createNewFile();
		}
	}
	
	public void initialize()
	{
		if(!isSetup())
		{
			return;
		}
		
		new ASYNC()
		{
			@Override
			public void async()
			{
				try
				{
					LibDownloader.downloadLib(Library.HTMMLUNIT);
					LibDownloader.downloadLib(Library.JSOUP);
					
					api = new SpigotSiteCore();
					readCookies();
					
					LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
					java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
					java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
					setup = true;
				}
				
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};
	}
	
	public void auth(final CommandSender sender, String u, String p, String h)
	{
		sender.sendMessage(Info.TAG + ChatColor.GRAY + "Encrypting Credentials");
		
		try
		{
			updateUser(u, p, h);
			sender.sendMessage(Info.TAG + ChatColor.GRAY + "Credentials Updated!");
			authConnect();
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public void version(final CommandSender sender)
	{
		if(updated)
		{
			sender.sendMessage(Info.TAG + ChatColor.YELLOW + "An update has already been downloaded. It will be applied when the server restarts.");
			return;
		}
		
		if(invalid)
		{
			sender.sendMessage(Info.TAG + ChatColor.YELLOW + "Your spigot credentials are invalid. Use /re auth");
			return;
		}
		
		new ASYNC()
		{
			@Override
			public void async()
			{
				if(check(sender))
				{
					try
					{
						React.runnables.add(new Runnable()
						{
							@Override
							public void run()
							{
								sender.sendMessage(Info.TAG + ChatColor.GRAY + "Checking for Updates...");
							}
						});
						
						if(couldUpdate())
						{
							String lasv = getLatestVersion();
							
							React.runnables.add(new Runnable()
							{
								@Override
								public void run()
								{
									sender.sendMessage(Info.TAG + ChatColor.GREEN + "Update Avalible! " + ChatColor.GRAY + lasv);
								}
							});
						}
						
						else
						{
							React.runnables.add(new Runnable()
							{
								@Override
								public void run()
								{
									sender.sendMessage(Info.TAG + ChatColor.GREEN + "You have the latest version!");
								}
							});
						}
					}
					
					catch(Exception e)
					{
						
					}
				}
			}
		};
	}
	
	public boolean check(final CommandSender sender)
	{
		if(!isSetup())
		{
			sender.sendMessage(Info.TAG + ChatColor.RED + "Please authenticate with spigot.");
			sender.sendMessage(Info.TAG + ChatColor.RED + "To authenticate, use /re auth");
			sender.sendMessage(Info.HR);
			return false;
		}
		
		if(!setup)
		{
			sender.sendMessage(Info.TAG + ChatColor.RED + "Still setting up the updater Please wait.");
			return false;
		}
		
		return true;
	}
	
	public void update(final CommandSender sender)
	{
		if(updated)
		{
			sender.sendMessage(Info.TAG + ChatColor.YELLOW + "An update has already been downloaded. It will be applied when the server restarts.");
			return;
		}
		
		if(invalid)
		{
			sender.sendMessage(Info.TAG + ChatColor.YELLOW + "Your spigot credentials are invalid. Use /re auth");
			return;
		}
		
		new ASYNC()
		{
			@Override
			public void async()
			{
				if(check(sender))
				{
					try
					{
						React.runnables.add(new Runnable()
						{
							@Override
							public void run()
							{
								sender.sendMessage(Info.TAG + ChatColor.GRAY + "Checking for Updates...");
							}
						});
						
						if(couldUpdate())
						{
							Resource react = getReactResource(getPurchasedResources(user));
							
							if(react != null)
							{
								String lasver = getLatestVersion();
								
								React.runnables.add(new Runnable()
								{
									@Override
									public void run()
									{
										N.t("Downloading Update " + lasver);
										sender.sendMessage(Info.TAG + ChatColor.GREEN + "Update Avalible! " + ChatColor.GRAY + lasver);
										sender.sendMessage(Info.TAG + ChatColor.GRAY + "Starting Update");
									}
								});
								
								Thread.sleep(1000);
								
								updatePlugin(sender, React.instance(), react.getResourceId(), user);
								updated = true;
							}
						}
						
						else
						{
							React.runnables.add(new Runnable()
							{
								@Override
								public void run()
								{
									sender.sendMessage(Info.TAG + ChatColor.GREEN + "You have the latest version!");
								}
							});
						}
					}
					
					catch(Exception e)
					{
						
					}
				}
			}
		};
	}
	
	@Override
	public void start()
	{
		if(canAutoCheck())
		{
			new TaskLater(200)
			{
				@Override
				public void run()
				{
					new Task(1200)
					{
						@Override
						public void run()
						{
							if(invalid)
							{
								return;
							}
							
							if(updated)
							{
								cancel();
								return;
							}
							
							new ASYNC()
							{
								@Override
								public void async()
								{
									if(canAutoDownload() && setup && isSetup())
									{
										React.runnables.add(new Runnable()
										{
											@Override
											public void run()
											{
												update(new SilentSender());
											}
										});
									}
								}
							};
						}
					};
				}
			};
		}
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		cc.set("updater.auto-check", true, "Enable update checking");
		cc.set("updater.auto-download", true, "Automatically download updates");
		cc.set("updater.auto-install", true, "Automatically install downloaded updates");
	}
	
	@Override
	public void onReadConfig()
	{
		
	}
	
	@Override
	public ClusterConfig getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "updater";
	}
	
	///////////////////////////////
	
	public void updateUser(String user, String pass, String h) throws Exception
	{
		File auth = new File(new File(React.instance().getDataFolder(), "cache"), "user.auth");
		SpigotAuthUser sau = new SpigotAuthUser(user, pass, h);
		sau.save(auth);
		browseCookies.delete();
		invalid = false;
	}
	
	public boolean couldUpdate() throws ConnectionFailedException, Exception
	{
		if(mAuthenticate())
		{
			Resource react = getReactResource(getPurchasedResources(user));
			
			if(react != null)
			{
				String ver = getResourceVersionString(react.getResourceId());
				int build = Version.toB(ver);
				
				if(build > Version.C)
				{
					return true;
				}
				
				else
				{
					return false;
				}
			}
		}
		
		return false;
	}
	
	public boolean isSetup()
	{
		try
		{
			if(getUser() != null)
			{
				return true;
			}
		}
		
		catch(Exception e)
		{
			
		}
		
		return false;
	}
	
	public SpigotAuthUser getUser()
	{
		File auth = new File(new File(React.instance().getDataFolder(), "cache"), "user.auth");
		
		if(!auth.exists())
		{
			return null;
		}
		
		SpigotAuthUser sau = new SpigotAuthUser("", "");
		
		try
		{
			sau.load(auth);
		}
		
		catch(Exception e)
		{
			return null;
		}
		
		return sau;
	}
	
	public boolean canAutoCheck()
	{
		return cc.getBoolean("updater.auto-check");
	}
	
	public boolean canAutoDownload()
	{
		return cc.getBoolean("updater.auto-download");
	}
	
	public boolean canAutoInstall()
	{
		return cc.getBoolean("updater.auto-install");
	}
	
	public String getLatestVersion() throws Exception
	{
		if(mAuthenticate())
		{
			Resource react = getReactResource(getPurchasedResources(user));
			
			if(react != null)
			{
				String ver = getResourceVersionString(react.getResourceId());
				
				return ver;
			}
		}
		
		return null;
	}
	
	public boolean mAuthenticate() throws Exception
	{
		if(!setup)
		{
			return false;
		}
		
		if(!authConnect())
		{
			return false;
		}
		
		return true;
	}
	
	public void updatePlugin(CommandSender s, Plugin plugin, int resourceId, User user) throws ConnectionFailedException, UnknownDependencyException, InvalidPluginException, InvalidDescriptionException
	{
		if(user == null)
		{
			return;
		}
		
		List<Resource> premiums = getPurchasedResources(user);
		
		for(Resource premium : premiums)
		{
			if(premium.getResourceId() == resourceId)
			{
				React.runnables.add(new Runnable()
				{
					@Override
					public void run()
					{
						s.sendMessage(ChatColor.GRAY + "Downloading Metadata...");
					}
				});
				
				Resource premiumResource = api.getResourceManager().getResourceById(premium.getResourceId(), user);
				
				File pluginFile = null;
				
				try
				{
					pluginFile = new File(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
					File ff = pluginFile;
					
					React.runnables.add(new Runnable()
					{
						@Override
						public void run()
						{
							s.sendMessage(ChatColor.GRAY + "Preparing patch for " + ff);
						}
					});
				}
				
				catch(UnsupportedEncodingException e)
				{
					
				}
				
				File outputFile = null;
				
				try
				{
					if(!Bukkit.getUpdateFolderFile().exists())
					{
						Bukkit.getUpdateFolderFile().mkdirs();
					}
					
					outputFile = new File(Bukkit.getUpdateFolderFile(), pluginFile.getName());
				}
				
				catch(Exception ex)
				{
					
				}
				
				if(pluginFile != null && outputFile != null)
				{
					React.runnables.add(new Runnable()
					{
						@Override
						public void run()
						{
							s.sendMessage(ChatColor.GRAY + "Downloading Patch...");
						}
					});
					
					premiumResource.downloadResource(user, outputFile);
					
					File oo = outputFile;
					
					React.runnables.add(new Runnable()
					{
						@Override
						public void run()
						{
							s.sendMessage(ChatColor.GRAY + "Download Complete! " + "(" + F.fileSize(oo.length()) + ")");
						}
					});
					
					if(canAutoInstall())
					{
						try
						{
							React.runnables.add(new Runnable()
							{
								@Override
								public void run()
								{
									s.sendMessage(ChatColor.GRAY + "Installing...");
								}
							});
							
							Thread.sleep(1000);
						}
						
						catch(InterruptedException e)
						{
							
						}
						
						try
						{
							React.runnables.add(new Runnable()
							{
								@Override
								public void run()
								{
									s.sendMessage(ChatColor.GRAY + "Completed!");
								}
							});
							
							Thread.sleep(100);
						}
						
						catch(InterruptedException e)
						{
							
						}
						
						PluginUtil.reload(Bukkit.getPluginManager().getPlugin("React"));
					}
				}
				
				break;
			}
		}
	}
	
	public Resource getReactResource(List<Resource> resources)
	{
		for(Resource i : resources)
		{
			if(i.getResourceId() == 21057)
			{
				return i;
			}
		}
		
		return null;
	}
	
	public boolean authConnect() throws InvalidCredentialsException, TwoFactorAuthenticationException, FileNotFoundException, IOException, InvalidConfigurationException
	{
		try
		{
			SpigotAuthUser sau = getUser();
			
			if(sau == null)
			{
				return false;
			}
			
			user = sauth(sau.getUsername(), sau.getPassword(), sau.getSecret());
		}
		
		catch(Exception e)
		{
			
		}
		
		if(user == null)
		{
			return false;
		}
		
		FileConfiguration fc = new YamlConfiguration();
		Map<String, String> cookies = user.getCookies();
		
		for(String key : fc.getKeys(false))
		{
			String value = fc.getString(key);
			cookies.put(key, value);
		}
		
		for(String i : cookies.keySet())
		{
			cookie.put(i, cookies.get(i));
			fc.set(i, cookies.get(i));
		}
		
		if(cookies.size() > 0)
		{
			user = new SpigotUser(user.getUsername());
			user.setCookies(cookie);
		}
		
		fc.save(browseCookies);
		return true;
	}
	
	public List<Resource> getPurchasedResources(User user) throws ConnectionFailedException
	{
		if(user == null)
		{
			return new ArrayList<Resource>();
		}
		
		ResourceManager resourceManager = api.getResourceManager();
		return resourceManager.getPurchasedResources(user);
	}
	
	public String getResourceVersionString(int resourceId)
	{
		try
		{
			HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + resourceId).getBytes("UTF-8"));
			String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			
			if(version.length() <= 7)
			{
				return version;
			}
		}
		
		catch(Exception ex)
		{
			
		}
		
		return null;
	}
	
	public boolean hasBought(SpigotUser user, int resourceId) throws ConnectionFailedException
	{
		if(user == null)
		{
			return false;
		}
		
		for(Resource resource : getPurchasedResources(user))
		{
			if(resource.getResourceId() == resourceId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public org.cyberpwn.react.util.Version getResourceVersion(int resourceId)
	{
		return new org.cyberpwn.react.util.Version(getResourceVersionString(resourceId));
	}
	
	public boolean isUpdateAvailable(Plugin plugin, int resourceId)
	{
		org.cyberpwn.react.util.Version pluginVersion = new org.cyberpwn.react.util.Version(plugin.getDescription().getVersion());
		
		if(pluginVersion.compare(getResourceVersion(resourceId)) == 1)
		{
			return true;
		}
		
		return false;
	}
	
	public List<Resource> getResources()
	{
		if(user != null)
		{
			try
			{
				List<Resource> resources = getPurchasedResources(user);
				return resources;
			}
			
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		return null;
	}
	
	public SpigotUser sauth(String username, String password) throws InvalidCredentialsException, TwoFactorAuthenticationException
	{
		UserManager userManager = api.getUserManager();
		SpigotUser user = null;
		
		try
		{
			user = (SpigotUser) userManager.authenticate(username, password);
		}
		
		catch(ConnectionFailedException e)
		{
			
		}
		
		catch(GeneralSecurityException e)
		{
			
		}
		
		return user;
	}
	
	public SpigotUser sauth(String username, String password, String totpSecret) throws ConnectionFailedException
	{
		UserManager userManager = api.getUserManager();
		
		SpigotUser user = null;
		
		try
		{
			if(totpSecret.equals("NONE"))
			{
				totpSecret = null;
				user = (SpigotUser) userManager.authenticate(username, password);
				
				return user;
			}
			
			user = (SpigotUser) userManager.authenticate(username, password, totpSecret);
			return user;
		}
		
		catch(InvalidCredentialsException e)
		{
			invalid = true;
		}
		
		catch(TwoFactorAuthenticationException e)
		{
			invalid = true;
		}
		
		catch(Exception e)
		{
			
		}
		
		return user;
	}
}
