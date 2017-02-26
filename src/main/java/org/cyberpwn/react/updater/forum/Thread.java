package org.cyberpwn.react.updater.forum;

import java.util.List;
import org.cyberpwn.react.updater.user.User;

/**
 * Spigot forum thread
 * 
 * @author Maxim Van de Wynckel
 */
public interface Thread {
	/**
	 * Get thread replies
	 * 
	 * @return List of {@link org.cyberpwn.react.updater.forum.Post}
	 */
	List<Post> getReplies();

	/**
	 * Get original post
	 * 
	 * @return {@link org.cyberpwn.react.updater.forum.Post}
	 */
	Post getOriginalPost();

	/**
	 * Get thread creator
	 * 
	 * @return Thread creator
	 */
	User getCreator();
}
