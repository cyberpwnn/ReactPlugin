package org.cyberpwn.react.updater;

/**
 * Spigot Site Application Programmable Interface
 * 
 * @author Maxim Van de Wynckel
 */
public interface SpigotSiteAPI {
	/**
	 * Get spigot user manager
	 * 
	 * @return {@link org.cyberpwn.react.updater.UserManager}
	 */
	UserManager getUserManager();

	/**
	 * Get spigot resource manager
	 * 
	 * @return {@link org.cyberpwn.react.updater.ResourceManager}
	 */
	ResourceManager getResourceManager();

	/**
	 * Get spigot forum manager
	 *
	 * @return {@link org.cyberpwn.react.updater.ForumManager}
	 */
	ForumManager getForumManager();

	/**
	 * Get spigot conversation manager
	 * 
	 * @return {@link org.cyberpwn.react.updater.ConversationManager}
	 */
	ConversationManager getConversationManager();
}
