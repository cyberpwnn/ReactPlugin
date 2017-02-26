package org.cyberpwn.react.updater;

/**
 * Spigot resource update
 * 
 * @author Maxim Van de Wynckel
 */
public interface ResourceUpdate
{
	
	String getUpdateID();
	
	void setUpdateID(String updateID);
	
	String getUpdateVersion();
	
	String getTextHeading();
	
	void setTextHeading(String textHeading);
	
	String getArticle();
	
	void setArticle(String article);
	
	String getMessageMeta();
	
	void setMessageMeta(String messageMeta);
	
	String getDownloadLink();
	
	String getUpdateLink();
	
	void setUpdateLink(String updateLink);
}
