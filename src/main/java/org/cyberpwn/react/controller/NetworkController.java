package org.cyberpwn.react.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.cyberpwn.react.React;
import org.cyberpwn.react.Version;
import org.cyberpwn.react.api.ReactAPI;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.server.ReactServer;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.Base64;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.Keyring;
import org.cyberpwn.react.util.N;
import org.cyberpwn.react.util.ReactRunnable;

public class NetworkController extends Controller
{
	public static String imeid;
	private ReactServer server;
	public static String uid = "%%__USER__%%";
	public static String nonce = "%%__NONCE__%%";
	private boolean started;
	
	public NetworkController(React react)
	{
		super(react);
		
		started = false;
		
		server = null;
		processId();
	}
	
	@Override
	public void start()
	{
		getReact().scheduleSyncTask(40, new Runnable()
		{
			@Override
			public void run()
			{
				Base64.AU();
				ClusterConfig cc = React.instance().getConfiguration();
				
				if(cc.getBoolean("react-remote.enable"))
				{
					int port = cc.getInt("react-remote.port");
					
					try
					{
						N.t("React Server Starting");
						server = new ReactServer(port);
						server.start();
						N.t("React Server Started");
						started = true;
					}
					
					catch(IOException e)
					{
						N.t("React Server Failed to Bind");
						React.instance().getD().f("FAILED TO BIND TO PORT: " + cc.getInt("react-remote.port"));
						React.instance().getD().f("React Server failed to bind to the target address");
						React.instance().getD().w("1. React Failed to shut down the server previously");
						React.instance().getD().w("2. That port is already in use.");
						React.instance().getD().s("To fix this, try rebooting. Reloading won't work.");
					}
					
				}
			}
		});
		
		new ASYNC()
		{
			@Override
			public void async()
			{
				identifyANA();
			}
		};
		
		N.t("Server Start");
	}
	
	public void processId()
	{
		if(!uid.equals("%%__USER__%%"))
		{
			Keyring kr = new Keyring(uid, nonce);
			kr.saveKeyring();
		}
		
		else
		{
			Keyring kr = new Keyring(uid, nonce);
			kr.loadKeyring();
			uid = kr.getId();
			nonce = kr.getTa();
		}
	}
	
	public void trackANA(String event, GMap<String, String> properties)
	{
		if(!React.isStats())
		{
			return;
		}
		
		int online = React.instance().onlinePlayers().length;
		String lang = React.instance().getLanguageController().getLang();
		
		new ASYNC()
		{
			@Override
			public void async()
			{
				try
				{
					String address = "";
					URL url = new URL("http://checkip.amazonaws.com/");
					InputStream is = url.openStream();
					BufferedReader br;
					String line = "nil";
					br = new BufferedReader(new InputStreamReader(is));
					
					while((line = br.readLine()) != null)
					{
						address = line;
						break;
					}
					
					if(is != null)
					{
						is.close();
					}
					
					JSONObject js = new JSONObject();
					JSONObject jsx = new JSONObject();
					
					for(String i : properties.k())
					{
						jsx.put(i, properties.get(i));
					}
					
					jsx.put("address", address);
					jsx.put("version", Version.V);
					jsx.put("language", lang);
					jsx.put("build", Version.C);
					jsx.put("uid", uid);
					jsx.put("online", online);
					jsx.put("nonce", nonce);
					js.put("userId", "user:" + uid);
					js.put("event", event);
					js.put("properties", jsx);
					byte[] postData = js.toString().getBytes();
					String request = "https://api.segment.io/v1/track";
					URL urlr = new URL(request);
					HttpURLConnection conn = (HttpURLConnection) urlr.openConnection();
					conn.setConnectTimeout(100000);
					conn.setReadTimeout(100000);
					conn.setDoOutput(true);
					conn.setInstanceFollowRedirects(true);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Accept", "*/*");
					conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
					conn.setRequestProperty("Content-Length", postData.length + "");
					conn.setRequestProperty("User-Agent", "react/" + Version.V);
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("Authorization", "Basic MmF4YXlOT2lhTm96eFBCWXl2a3kyNnZlNTlnR0FJcVQ6");
					DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
					dos.write(postData);
					dos.flush();
					conn.getInputStream();
				}
				
				catch(Exception e)
				{
					
				}
			}
		};
	}
	
	public void identifyANA()
	{
		try
		{
			String address = "";
			URL url = new URL("http://checkip.amazonaws.com/");
			InputStream is = url.openStream();
			BufferedReader br;
			String line = "nil";
			br = new BufferedReader(new InputStreamReader(is));
			
			while((line = br.readLine()) != null)
			{
				address = line;
				break;
			}
			
			if(is != null)
			{
				is.close();
			}
			
			JSONObject js = new JSONObject();
			JSONObject jsx = new JSONObject();
			jsx.put("address", address);
			jsx.put("version", Version.V);
			jsx.put("build", Version.C);
			jsx.put("uid", uid);
			jsx.put("nonce", nonce);
			js.put("userId", "user:" + uid);
			js.put("traits", jsx);
			byte[] postData = js.toString().getBytes();
			String request = "https://api.segment.io/v1/identify";
			URL urlr = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) urlr.openConnection();
			conn.setConnectTimeout(100000);
			conn.setReadTimeout(100000);
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			conn.setRequestProperty("Content-Length", postData.length + "");
			conn.setRequestProperty("User-Agent", "react/" + Version.V);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Basic MmF4YXlOT2lhTm96eFBCWXl2a3kyNnZlNTlnR0FJcVQ6");
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.write(postData);
			dos.flush();
			conn.getInputStream();
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	public String getImeid()
	{
		return imeid;
	}
	
	@Override
	public void tick()
	{
		if(server == null)
		{
			return;
		}
		
		long tick = React.instance().getSampleController().getTick();
		
		if(tick % 72000 == 0)
		{
			N.t("Low Wake Check", "running" + tick / 72000 + " Hours", "tps", ReactAPI.getTicksPerSecond() + "", "memory-used", "" + (100 * (ReactAPI.getMemoryUsed() / (ReactAPI.getMemoryFree() + ReactAPI.getMemoryUsed()))));
		}
		
		ReactServer.reactData.sample(getReact());
		
		for(ReactRunnable i : ReactServer.runnables)
		{
			i.run(getReact());
		}
		
		ReactServer.runnables.clear();
		if(tick % 100 == 0)
		{
			ClusterConfig cc = React.instance().getConfiguration();
			if(server != null && !server.isAlive() && started && cc.getBoolean("react-remote.auto-restart"))
			{
				
				int port = cc.getInt("react-remote.port");
				
				try
				{
					React.instance().getD().w("React Remote Server not responding... Attempting to restart.");
					server = new ReactServer(port);
					server.start();
					
				}
				
				catch(IOException e)
				{
					React.instance().getD().f("Failed to restart... Aborting.");
					started = false;
				}
			}
		}
	}
	
	@Override
	public void stop()
	{
		server.interrupt();
	}
	
	public static void chain()
	{
		DataController.chain();
	}
}
