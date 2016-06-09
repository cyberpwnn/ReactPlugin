package org.cyberpwn.react.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.cyberpwn.react.React;

public class JavaPlugin extends org.bukkit.plugin.java.JavaPlugin
{
	public UUID mdsha;
	
	public void onEnable()
	{
		mdsha = UUID.randomUUID();
		
		React.instance().scheduleSyncTask(25, new Runnable()
		{
			@Override
			public void run()
			{
				MessageDigest md = null;
				MessageDigest sha = null;
				MessageDigest shx = null;
				try
				{
					md = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
					sha = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
					shx = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_512);
				}
				
				catch(NoSuchAlgorithmException e1)
				{
					e1.printStackTrace();
				}
				
				try
				{
					InputStream is = Files.newInputStream(Paths.get(new File(React.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()));
					new DigestInputStream(is, md);
					new DigestInputStream(is, sha);
					new DigestInputStream(is, shx);
				}
				
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
				
				catch(URISyntaxException e2)
				{
					e2.printStackTrace();
				}
				
				byte[] digest = md.digest();
				byte[] digestx = sha.digest();
				byte[] digestxx = shx.digest();
				
				String sx = "";
				
				for(byte i : digest)
				{
					sx = sx + String.valueOf(i) + ", ";
				}
				
				for(byte i : digestx)
				{
					sx = sx + String.valueOf(i) + ", ";
				}
				
				for(byte i : digestxx)
				{
					sx = sx + String.valueOf(i) + ", ";
				}
				
				mdsha = UUID.nameUUIDFromBytes(sx.getBytes());
			}
		});
	}
}
