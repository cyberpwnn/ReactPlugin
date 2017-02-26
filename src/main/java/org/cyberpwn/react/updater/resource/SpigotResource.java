package org.cyberpwn.react.updater.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import org.cyberpwn.react.updater.SpigotSiteCore;
import org.cyberpwn.react.updater.http.HTTPDownloadResponse;
import org.cyberpwn.react.updater.http.HTTPUnitRequest;
import org.cyberpwn.react.updater.user.SpigotUser;
import org.cyberpwn.react.updater.user.User;

public class SpigotResource implements Resource
{
	private int id = 0;
	private String name = "";
	private String version = "";
	private User author = null;
	private ResourceCategory category = null;
	private boolean deleted = false;
	private String downloadURL = "";
	private String externalURL = "";
	private List<ResourceUpdate> resourceUpdates = null;
	
	public SpigotResource()
	{
		
	}
	
	public SpigotResource(String name)
	{
		setResourceName(name);
	}
	
	@Override
	public int getResourceId()
	{
		return id;
	}
	
	public void setResourceId(int id)
	{
		this.id = id;
	}
	
	@Override
	public String getResourceName()
	{
		return name;
	}
	
	@Override
	public String getLastVersion()
	{
		return version;
	}
	
	@Override
	public User getAuthor()
	{
		return author;
	}
	
	public void setAuthor(User author)
	{
		this.author = author;
	}
	
	@Override
	public void setResourceName(String name)
	{
		this.name = name;
	}
	
	@Override
	public void setLastVersion(String version)
	{
		this.version = version;
	}
	
	public void setResourceUpdates(List<ResourceUpdate> updates)
	{
		resourceUpdates = updates;
	}
	
	@Override
	public ResourceCategory getResourceCategory()
	{
		return category;
	}
	
	@Override
	public String getDownloadURL()
	{
		return downloadURL;
	}
	
	public void setDownloadURL(String downloadURL)
	{
		this.downloadURL = downloadURL;
	}
	
	@Override
	public File downloadResource(User user, File output)
	{
		try
		{
			if(output.exists())
			{
				output.delete();
			}
			output.getParentFile().mkdirs();
			
			HTTPDownloadResponse dlResponse = HTTPUnitRequest.downloadFile(getDownloadURL(), user != null ? ((SpigotUser) user).getCookies() : SpigotSiteCore.getBaseCookies());
			setExternalURL(dlResponse.getUrl().toString());
			InputStream stream = dlResponse.getStream();
			FileOutputStream fos = new FileOutputStream(output);
			byte[] buffer = new byte[stream.available()];
			stream.read(buffer);
			fos.write(buffer);
			fos.close();
			return output;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean isDeleted()
	{
		return deleted;
	}
	
	public void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
	}
	
	@Override
	public int getAverageRating()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public List<Rating> getRatings()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ResourceUpdate> getResourceUpdates()
	{
		return resourceUpdates;
	}
	
	public String getExternalURL()
	{
		return externalURL;
	}
	
	public void setExternalURL(String externalURL)
	{
		this.externalURL = externalURL;
	}
	
	@Override
	public String toString()
	{
		return "Resource: " + getResourceName();
	}
}
