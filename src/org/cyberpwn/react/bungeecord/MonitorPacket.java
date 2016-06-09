package org.cyberpwn.react.bungeecord;

import org.cyberpwn.react.json.JSONObject;
import org.cyberpwn.react.object.GMap;
import org.cyberpwn.react.object.Value;

public class MonitorPacket
{
	private GMap<String, Value> data;
	
	public MonitorPacket()
	{
		data = new GMap<String, Value>();
	}
	
	public MonitorPacket(JSONObject json)
	{
		this();
		
		for(String i : json.getJSONObject("data").keySet())
		{
			data.put(i, new Value(json.getJSONObject("data").getDouble(i)));
		}
	}
	
	public void put(String s, Value v)
	{
		data.put(s, v);
	}
	
	public JSONObject toJSON()
	{
		JSONObject json = new JSONObject();
		JSONObject mdata = new JSONObject();
		
		for(String i : data.keySet())
		{
			mdata.put(i, data.get(i).getDouble());
		}
		
		json.put("data", mdata);
		
		return json;
	}
}
