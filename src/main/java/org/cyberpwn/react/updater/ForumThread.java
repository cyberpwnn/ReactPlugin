package org.cyberpwn.react.updater;

import java.util.List;

/**
 * Spigot forum thread
 * 
 * @author Maxim Van de Wynckel
 */
public interface ForumThread
{
	/**
	 * Get thread replies
	 * 
	 * @return List of {@link org.cyberpwn.react.updater.Post}
	 */
	List<Post> getReplies();
	
	/**
	 * Get original post
	 * 
	 * @return {@link org.cyberpwn.react.updater.Post}
	 */
	Post getOriginalPost();
	
	/**
	 * Get thread creator
	 * 
	 * @return Thread creator
	 */
	User getCreator();
}
