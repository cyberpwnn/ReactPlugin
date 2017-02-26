package org.cyberpwn.react.updater;

import org.cyberpwn.react.updater.forum.ForumManager;
import org.cyberpwn.react.updater.resource.ResourceManager;
import org.cyberpwn.react.updater.user.ConversationManager;
import org.cyberpwn.react.updater.user.UserManager;

/**
 * Spigot Site Application Programmable Interface
 * 
 * @author Maxim Van de Wynckel
 */
public interface SpigotSiteAPI {
	/**
	 * Get spigot user manager
	 * 
	 * @return {@link org.cyberpwn.react.updater.user.UserManager}
	 */
	UserManager getUserManager();

	/**
	 * Get spigot resource manager
	 * 
	 * @return {@link org.cyberpwn.react.updater.resource.ResourceManager}
	 */
	ResourceManager getResourceManager();

	/**
	 * Get spigot forum manager
	 *
	 * @return {@link org.cyberpwn.react.updater.forum.ForumManager}
	 */
	ForumManager getForumManager();

	/**
	 * Get spigot conversation manager
	 * 
	 * @return {@link org.cyberpwn.react.updater.user.ConversationManager}
	 */
	ConversationManager getConversationManager();
}
