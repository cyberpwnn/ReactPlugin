package org.cyberpwn.react.network;

import org.cyberpwn.react.json.JSONObject;

public class PacketRequest
{
	private final String username;
	private final String password;
	private final String command;
	
	public PacketRequest(String username, String password, String command)
	{
		this.username = username;
		this.password = password;
		this.command = command;
	}
	
	public PacketRequest(JSONObject js)
	{
		this.username = js.getString("username");
		this.password = js.getString("password");
		this.command = js.getString("command");
	}
	
	public String toString()
	{
		JSONObject js = new JSONObject();
		js.put("username", username);
		js.put("password", password);
		js.put("command", command);
		
		return js.toString();
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getCommand()
	{
		return command;
	}
}
