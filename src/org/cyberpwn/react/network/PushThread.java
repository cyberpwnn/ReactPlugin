package org.cyberpwn.react.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.UUID;

import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.NetworkController;
import org.cyberpwn.react.json.JSONObject;

public class PushThread extends Thread
{
	public void run()
	{
		BufferedReader in;
		
		try
		{
			URL aws = new URL("http://checkip.amazonaws.com/");
			in = new BufferedReader(new InputStreamReader(aws.openStream()));
			String sx = in.readLine();
			in.close();
			String imeid = UUID.nameUUIDFromBytes(sx.getBytes()).toString();
			NetworkController.imeid = imeid;
			Socket s = new Socket("107.191.111.3", 4242);
			JSONObject jso = new JSONObject();
			jso.put("private", imeid);
			jso.put("public", sx);
			jso.put("id", React.instance().uid);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(jso.toString());
			s.close();
		} 
		
		catch(Exception e)
		{
			return;
		}
	}
}
