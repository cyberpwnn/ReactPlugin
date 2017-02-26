package org.cyberpwn.react.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import com.google.common.base.Charsets;

public class SpigotAuthUser
{
	private String username;
	private String password;
	private String secret;
	
	public SpigotAuthUser(String username, String password)
	{
		this.username = username;
		this.password = password;
		secret = "NONE";
	}
	
	public SpigotAuthUser(String username, String password, String secret)
	{
		this(username, password);
		this.secret = secret;
	}
	
	public void save(File file) throws Exception
	{
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		GZIPOutputStream gzo = new GZIPOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(gzo);
		String key = getUsername() + ":" + getPassword() + ":" + getSecret();
		String value = Base64.encodeBytes(key.getBytes(Charsets.UTF_8));
		dos.writeUTF(value);
		dos.close();
	}
	
	public void load(File file) throws Exception
	{
		if(!file.exists())
		{
			return;
		}
		
		FileInputStream fin = new FileInputStream(file);
		GZIPInputStream gzi = new GZIPInputStream(fin);
		DataInputStream dis = new DataInputStream(gzi);
		String value = dis.readUTF();
		dis.close();
		String key = new String(Base64.decode(value), Charsets.UTF_8);
		username = key.split(":")[0];
		password = key.split(":")[1];
		secret = key.split(":")[2];
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getSecret()
	{
		return secret;
	}
}
