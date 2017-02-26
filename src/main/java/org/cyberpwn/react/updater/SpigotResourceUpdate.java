package org.cyberpwn.react.updater;

import org.apache.commons.lang3.StringUtils;

public class SpigotResourceUpdate implements ResourceUpdate
{
	private String updateID = "";
	private String textHeading = "";
	private String article = "";
	private String messageMeta = "";
	private String updateLink = "";
	private String version = null;
	
	@Override
	public String getUpdateID()
	{
		return updateID;
	}
	
	@Override
	public String getUpdateVersion()
	{
		return version;
	}
	
	@Override
	public void setUpdateID(String updateID)
	{
		this.updateID = updateID;
	}
	
	@Override
	public String getTextHeading()
	{
		return textHeading;
	}
	
	@Override
	public void setTextHeading(String textHeading)
	{
		this.textHeading = textHeading;
	}
	
	@Override
	public String getArticle()
	{
		return article;
	}
	
	@Override
	public void setArticle(String article)
	{
		this.article = article;
		
		if(article.contains("Supports Updater {{"))
		{
			String version = StringUtils.substringBetween(article, "{{", "}}");
			setVersion(version);
		}
	}
	
	@Override
	public String getMessageMeta()
	{
		return messageMeta;
	}
	
	@Override
	public void setMessageMeta(String messageMeta)
	{
		this.messageMeta = messageMeta;
	}
	
	@Override
	public String getUpdateLink()
	{
		return updateLink;
	}
	
	@Override
	public void setUpdateLink(String updateLink)
	{
		this.updateLink = updateLink;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	@Override
	public String getDownloadLink()
	{
		return "https://www.spigotmc.org/resources/react-smart-server-performance.21057/download?version=" + getUpdateID().replaceAll("update-", "");
	}
}
